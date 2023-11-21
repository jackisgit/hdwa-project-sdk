package com.hdwa.alarm.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 获取此服务的所有配置
 */
@Component
public class CommonConst {

    /**
     * 系统标识，用于创建人
     */
    public static String systemId;

    /**
     * 集团编码
     */
    public static String groupCode = "WD";

    /**
     * 项目ID
     */
    public static String projectId = System.getProperty("projectId");

    /**
     * 报警服务的服务地址
     */
    public static String alarmServerUrl;

    @Value("${systemId}")
    public void setSystemId(String value) {
        CommonConst.systemId = value;
    }

    @Value("${url.alarmUrl}")
    public void setAlarmServerUrl(String value) {
        CommonConst.alarmServerUrl = value;
    }

    public void setProjectId(String value) {
        CommonConst.systemId = value;
    }

}