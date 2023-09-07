package com.hdwa.sdk.kafka;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.hdwa.sdk.entity.ZktAlarmRecord;
import com.hdwa.sdk.service.ZktAlarmRecordServiceImpl;
import com.redxun.core.cache.alarm.AlarmInfoCache;
import com.redxun.core.constant.alarm.CommonConst;
import com.redxun.core.entity.alarm.AlarmDefineVO;
import com.redxun.core.entity.alarm.AlarmStateVO;
import com.redxun.core.entity.alarm.netty.NettyMessage;
import com.redxun.core.util.alarm.AlarmDefineUtil;
import com.redxun.core.util.alarm.LockUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * kafak消费
 */
@Configuration
@Slf4j
@ConditionalOnProperty(prefix = "spring.kafka", name = "enable", havingValue = "true")
public class KafkaConsumer {

    @Autowired
    private ZktAlarmRecordServiceImpl alarmRecordService;

    @KafkaListener(topics = {"#{'${topicName}'.split(',')}"}, containerFactory = "listenerContainerFactory")
    public void topicCloudAlarmConsumer(List<ConsumerRecord<?, String>> record, Acknowledgment ack) {
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
            log.info("--报警定义新增或更新--{}", msg);
            NettyMessage<AlarmDefineVO> AlarmDefineMessage = JSONObject.parseObject(msg.toString(), new TypeReference<NettyMessage<AlarmDefineVO>>() {
            });
            List<AlarmDefineVO> definesList = AlarmDefineMessage.getContent();
            if (CollectionUtil.isNotEmpty(definesList)) {
                AlarmDefineUtil.listSomeAlarmDefine(definesList);
            }
        } else if (msg.getOpCode() == 8) {
            log.info("-----报警记录id推送----[{}]", msg);
            List<?> content = msg.getContent();
            if (CollectionUtil.isNotEmpty(content)) {
                JSONObject parseObject = JSONObject.parseObject(JSONObject.toJSONString(content.get(0)));
                String defineId = AlarmInfoCache.getAlarmDefineId(parseObject);
                ZktAlarmRecord zktAlarmRecord = alarmRecordService.getById(defineId);
                if (zktAlarmRecord == null) {
                    zktAlarmRecord = new ZktAlarmRecord();
                }
                zktAlarmRecord.setId(defineId);
                zktAlarmRecord.setObjId(parseObject.getString("objId"));
                zktAlarmRecord.setItemId(parseObject.getString("itemId"));
                zktAlarmRecord.setAlarmId(parseObject.getString("id"));
                alarmRecordService.save(zktAlarmRecord);
            }
        } else if (msg.getOpCode() == 9) {
            NettyMessage<AlarmDefineVO> AlarmDefineMessage = JSONObject.parseObject(msg.toString(), new TypeReference<NettyMessage<AlarmDefineVO>>() {
            });
            List<AlarmDefineVO> definesList = AlarmDefineMessage.getContent();
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
            NettyMessage<AlarmDefineVO> AlarmDefineMessage = JSONObject.parseObject(msg.toString(), new TypeReference<NettyMessage<AlarmDefineVO>>() {
            });
            List<AlarmDefineVO> definesList = AlarmDefineMessage.getContent();
            if (CollectionUtil.isNotEmpty(definesList)) {
                AlarmDefineUtil.deleteAlarmDefine(definesList);
            }
        } else if (msg.getOpCode() == 11) {
            // 更新隔离的系统对象
            NettyMessage<String> AlarmDefineMessage = JSONObject.parseObject(msg.toString(), new TypeReference<NettyMessage<String>>() {
            });
            List<String> isolationSystemList = AlarmDefineMessage.getContent();
            if (CollectionUtil.isNotEmpty(isolationSystemList)) {
                AlarmInfoCache.isolationSystemList = isolationSystemList;
            }
        } else if (msg.getOpCode() == 12) {
            // 云端更新报警记录状态
            NettyMessage<JSONObject> AlarmStateMessage = JSONObject.parseObject(msg.toString(), new TypeReference<NettyMessage<JSONObject>>() {
            });
            List<JSONObject> stateList = AlarmStateMessage.getContent();
            alarmRecordService.updateAlarmDefine(stateList);
        } else if (msg.getOpCode() == 13) {
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
                        log.warn(alarmConfig.toString());
                    }
                }
            }
        }
    }
}