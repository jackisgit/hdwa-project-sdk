package com.hdwa.sdk.utils;

import com.alibaba.fastjson.JSONObject;
import com.hdwa.sdk.entity.repository.DataContainer;
import com.hdwa.sdk.entity.repository.RepositoryImpl;
import lombok.extern.slf4j.Slf4j;

/**
 * @author abao
 * @since 2023/9/7
 */
@Slf4j
public class AlarmJob implements Runnable {

    /**
     * 项目id
     */
    private final String projectId;

    public AlarmJob(String projectId) {
        this.projectId = projectId;
    }

    /**
     * 计算报警数据
     */
    @Override
    public void run() {
        RepositoryImpl repository = DataContainer.projectMap.get(projectId);
        if (repository == null) {
            return;
        }
        if (DataContainer.alarmArray == null || DataContainer.id2alarmList == null || DataContainer.id2alarmList.size() == 0) {
            return;
        }
        JSONObject AlarmJob = DataContainer.alarmBuffer.poll();
        if (AlarmJob == null) {
            return;
        }
        log.warn("******计算报警数据");
        AlarmUtil.calculatedAlarm(repository, AlarmJob);
    }
}
