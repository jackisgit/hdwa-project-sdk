package com.hdwa.alarm.kafka;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hdwa.alarm.cache.AlarmInfoCache;
import com.hdwa.alarm.config.CommonConst;
import com.hdwa.alarm.entity.ZktAlarmRecord;
import com.hdwa.alarm.mapper.ZktAlarmRecordMapper;
import com.hdwa.alarm.service.ZktAlarmRecordServiceImpl;
import com.hdwa.alarm.util.AlarmDefineUtil;
import com.hdwa.alarm.util.LockUtil;
import com.hdwa.alarm.vo.AlarmDefineVO;
import com.hdwa.alarm.vo.AlarmStateVO;
import com.hdwa.alarm.vo.NettyMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Configuration
public class KafkaMessageReceiverAlarm {

    @Resource
    private ZktAlarmRecordMapper zktAlarmRecordMapper;
    @Resource
    private ZktAlarmRecordServiceImpl zktAlarmRecordService;

    /**
     * listenerContainerFactory设置了批量拉取消息，因此参数是List<ConsumerRecord<Integer, String>>，否则是ConsumerRecord
     */
    @KafkaListener(
            containerFactory = "alarmKafkaListenerContainerFactory",
            topics = "${spring.kafka.alarm.consumer.alarm-topic}",
            groupId = "${spring.kafka.alarm.consumer.alarm-group-id}")
    public void registryReceiver(List<ConsumerRecord<Integer, String>> record, Acknowledgment ack) {
        for (ConsumerRecord<?, String> consumerRecords : record) {
            Optional<String> message = Optional.ofNullable(consumerRecords.value());
            if (message.isPresent()) {
                NettyMessage<?> msg = JSONObject.parseObject(message.get(), NettyMessage.class);
                try {
                    if (Objects.equals(msg.getProjectId(), CommonConst.projectId)) {
                        handlerMsg(msg);
                        ack.acknowledge();
                    }
                } catch (Exception e) {
                    log.error("处理kafka消息失败", e);
                }
            }
        }
    }


    private void handlerMsg(NettyMessage<?> msg) {
        if (msg.getOpCode() == 7) {
            log.debug("--报警定义-新增/更新--{}", msg);
            NettyMessage<AlarmDefineVO> alarmDefineMessage = JSONObject.parseObject(msg.toString(), new TypeReference<NettyMessage<AlarmDefineVO>>() {
            });
            List<AlarmDefineVO> definesList = alarmDefineMessage.getContent();
            if (CollectionUtil.isNotEmpty(definesList)) {
                AlarmDefineUtil.listSomeAlarmDefine(definesList);
            }
        } else if (msg.getOpCode() == 8) {
            log.debug("-----报警记录-id推送----[{}]", msg);
            List<?> content = msg.getContent();
            if (CollectionUtil.isNotEmpty(content)) {
                JSONObject parseObject = JSONObject.parseObject(JSONObject.toJSONString(content.get(0)));
                String defineId = AlarmInfoCache.getAlarmDefineId(parseObject);

                LambdaQueryWrapper<ZktAlarmRecord> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(ZktAlarmRecord::getId, defineId);
                ZktAlarmRecord zktAlarmRecord = zktAlarmRecordMapper.selectOne(queryWrapper);
                boolean exist = true;
                if (zktAlarmRecord == null) {
                    exist = false;
                    zktAlarmRecord = new ZktAlarmRecord();
                }
                zktAlarmRecord.setId(defineId);
                zktAlarmRecord.setObjId(parseObject.getString("objId"));
                zktAlarmRecord.setItemId(parseObject.getString("itemId"));
                zktAlarmRecord.setAlarmId(parseObject.getString("id"));

                if (exist) {
                    zktAlarmRecordMapper.updateById(zktAlarmRecord);
                } else {
                    zktAlarmRecordMapper.insert(zktAlarmRecord);
                }
            }
        } else if (msg.getOpCode() == 9) {
            log.debug("-----报警定义-9----[{}]", msg);
            NettyMessage<AlarmDefineVO> alarmDefineMessage = JSONObject.parseObject(msg.toString(), new TypeReference<NettyMessage<AlarmDefineVO>>() {
            });
            List<AlarmDefineVO> definesList = alarmDefineMessage.getContent();
            if (CollectionUtil.isNotEmpty(definesList)) {
                try {
                    LockUtil.getInstance().lock.lock();
                    LockUtil.getInstance().setExecute(false);
                    //加个等待，保证正在执行的逻辑执行成功
                    Thread.sleep(4000);
                    AlarmDefineUtil.listAllAlarmDefine(definesList);
                    LockUtil.getInstance().setExecute(true);
                    LockUtil.getInstance().condition.signalAll();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    LockUtil.getInstance().lock.unlock();
                }
            }
        } else if (msg.getOpCode() == 10) {
            log.debug("-----报警定义-删除----[{}]", msg);
            NettyMessage<AlarmDefineVO> alarmDefineMessage = JSONObject.parseObject(msg.toString(), new TypeReference<NettyMessage<AlarmDefineVO>>() {
            });
            List<AlarmDefineVO> definesList = alarmDefineMessage.getContent();
            if (CollectionUtil.isNotEmpty(definesList)) {
                AlarmDefineUtil.deleteAlarmDefine(definesList);
            }
        } else if (msg.getOpCode() == 11) {
            log.debug("-----报警定义-11----[{}]", msg);
            // 更新隔离的系统对象
            NettyMessage<String> alarmDefineMessage = JSONObject.parseObject(msg.toString(), new TypeReference<NettyMessage<String>>() {
            });
            List<String> isolationSystemList = alarmDefineMessage.getContent();
            if (CollectionUtil.isNotEmpty(isolationSystemList)) {
                AlarmInfoCache.isolationSystemList = isolationSystemList;
            }
        } else if (msg.getOpCode() == 12) {
            log.debug("-----报警记录-更新（报警转工单处理完成）----[{}]", msg);
            // 云端更新报警记录状态
            NettyMessage<JSONObject> alarmDefineMessage = JSONObject.parseObject(msg.toString(), new TypeReference<NettyMessage<JSONObject>>() {
            });
            List<JSONObject> stateList = alarmDefineMessage.getContent();
            zktAlarmRecordService.updateAlarmDefine(stateList);
        } else if (msg.getOpCode() == 13) {
            log.debug("-----报警定义-隔离-更新----[{}]", msg);
            // 报警隔离或取消隔离
            NettyMessage<JSONObject> alarmConfigMessage = JSONObject.parseObject(msg.toString(), new TypeReference<NettyMessage<JSONObject>>() {
            });
            List<JSONObject> alarmConfigs = alarmConfigMessage.getContent();
            AlarmDefineUtil.isolationAlarmDefine(alarmConfigs);
            //隔离报警后 云端自动删除报警记录 边端将报警状态置为正常

            for (JSONObject alarmConfig : alarmConfigs) {
                int open = alarmConfig.getIntValue("open");
                if (open == 0) {
                    String meter = alarmConfig.getString("objId");
                    String itemId = alarmConfig.getString("itemId");
                    AlarmStateVO alarmState = AlarmInfoCache.getAlarmState(meter + "-" + itemId);
                    if (alarmState != null) {
                        alarmState.setState("0");
                    } else {
                        log.debug(alarmConfig.toString());
                    }
                }
            }
        } else {
            log.debug("-----报警-位置操作码----[{}]", msg);
        }
    }
}
