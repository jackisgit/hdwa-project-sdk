package com.hdwa.sdk.service;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONObject;
import com.hdwa.sdk.cache.AlarmInfoCache;
import com.hdwa.sdk.cache.ExpireAlarmQueue;
import com.hdwa.sdk.entity.ZktAlarmRecord;
import com.hdwa.sdk.mapper.ZktAlarmRecordMapper;
import com.hdwa.sdk.vo.AlarmStateVO;
import com.hdwa.sdk.vo.ExpireAlarmMessageVO;
import com.redxun.common.base.db.BaseDao;
import com.redxun.common.base.db.BaseService;
import com.redxun.common.service.impl.SuperServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
* [报警记录ID]业务服务类
*/
@Service
public class ZktAlarmRecordServiceImpl extends SuperServiceImpl<ZktAlarmRecordMapper, ZktAlarmRecord> implements BaseService<ZktAlarmRecord> {

    @Resource
    private ZktAlarmRecordMapper zktAlarmRecordMapper;

    @Override
    public BaseDao<ZktAlarmRecord> getRepository() {
        return zktAlarmRecordMapper;
    }

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
                ZktAlarmRecord zktAlarmRecord = this.getById(defineId);
                if (zktAlarmRecord == null) {
                    zktAlarmRecord = new ZktAlarmRecord();
                }
                zktAlarmRecord.setId(defineId);
                zktAlarmRecord.setProjectId(stateItem.getString("projectId"));
                zktAlarmRecord.setObjId(stateItem.getString("objId"));
                zktAlarmRecord.setItemCode(stateItem.getString("itemCode"));
                zktAlarmRecord.setItemId(stateItem.getString("itemId"));
                zktAlarmRecord.setState(stateItem.getString("state"));
                this.save(zktAlarmRecord);
                if("2".equals(state)||"3".equals(state)) {
                    try {
                        ExpireAlarmMessageVO em = new ExpireAlarmMessageVO();
                        //取消过期消息
                        em.setType("2");
                        em.setJobName(defineId);
                        em.setJobGroupName("expire");
                        ExpireAlarmQueue.getExpireAlarmMessageQueue().produce(em);
                        //报警恢复，报警状态重置回默认
                        alarmState.reset();
                    }catch (Exception e){
                        log.error("取消过期定时任务失败",e);
                    }
                }
            }
        }
    }
}