package com.hdwa.alarm.vo;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import org.quartz.JobDataMap;

import java.util.Date;

/**
 * 报警过期，取消报警过期的消息队列
 **/
@Data
public class ExpireAlarmMessageVO {
    /**
     * 消息类型 1-过期消息  2-取消报警消息
     */
    String Type;
    /**
     * 报警过期时间
     */
    Date startTime;
    /**
     * 任务名称
     */
    String jobName;
    /**
     * 任务分组
     */
    String jobGroupName;
    /**
     * 定时任务数据
     */
    JobDataMap jobDataMap;

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}