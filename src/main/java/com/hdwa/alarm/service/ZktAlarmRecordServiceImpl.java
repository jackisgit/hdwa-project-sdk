package com.hdwa.alarm.service;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hdwa.alarm.cache.AlarmInfoCache;
import com.hdwa.alarm.cache.ExpireAlarmQueue;
import com.hdwa.alarm.entity.ZktAlarmRecord;
import com.hdwa.alarm.mapper.ZktAlarmRecordMapper;
import com.hdwa.alarm.vo.AlarmStateVO;
import com.hdwa.alarm.vo.ExpireAlarmMessageVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * [报警记录]业务服务类
 */
@Service
@Slf4j
public class ZktAlarmRecordServiceImpl {

    @Resource
    private ZktAlarmRecordMapper zktAlarmRecordMapper;

    public void updateAlarmDefine(List<JSONObject> alarmDefineList) {
        if (CollectionUtil.isNotEmpty(alarmDefineList)) {
            for (JSONObject stateItem : alarmDefineList) {
                String defineId = AlarmInfoCache.getAlarmDefineId(stateItem);
                AlarmStateVO alarmState = new AlarmStateVO(defineId);
                String state = stateItem.getString("state");
                //1：未恢复；2：已恢复；3：已过期
                //报警状态（ 0-正常 1-报警）
                String newState = "1".equals(state) ? "1" : "0";
                alarmState.setState(newState);
                AlarmInfoCache.setAlarmState(defineId, alarmState);
                LambdaQueryWrapper<ZktAlarmRecord> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(ZktAlarmRecord::getId, defineId);
                ZktAlarmRecord zktAlarmRecord = zktAlarmRecordMapper.selectOne(queryWrapper);
                boolean exist = true;
                if (zktAlarmRecord == null) {
                    exist = false;
                    zktAlarmRecord = new ZktAlarmRecord();
                }
                zktAlarmRecord.setId(defineId);
                zktAlarmRecord.setProjectId(stateItem.getString("projectId"));
                zktAlarmRecord.setObjId(stateItem.getString("objId"));
                zktAlarmRecord.setItemCode(stateItem.getString("itemCode"));
                zktAlarmRecord.setItemId(stateItem.getString("itemId"));
                zktAlarmRecord.setState(stateItem.getString("state"));

                if (exist) {
                    zktAlarmRecordMapper.updateById(zktAlarmRecord);
                } else {
                    zktAlarmRecordMapper.insert(zktAlarmRecord);
                }

                if ("2".equals(state) || "3".equals(state)) {
                    try {
                        ExpireAlarmMessageVO em = new ExpireAlarmMessageVO();
                        //取消过期消息
                        em.setType("2");
                        em.setJobName(defineId);
                        em.setJobGroupName("expire");
                        ExpireAlarmQueue.getExpireAlarmMessageQueue().produce(em);
                        //报警恢复，报警状态重置回默认
                        alarmState.reset();
                    } catch (Exception e) {
                        log.error("取消过期定时任务失败", e);
                    }
                }
            }
        }
    }
}