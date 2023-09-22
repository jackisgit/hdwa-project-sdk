package com.hdwa.alarm.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 获取此服务的所有配置
 */
@Component
public class CommonConst {
	
	/** 系统标识，用于创建人 */
    public static final String IOT_PONINTSET_TYPE = "pointset";
    
    /** 系统标识，用于创建人 */
    public static final String IOT_IOT_TYPE = "iot";
    
	/** 项目根路径*/
	public static final String SERVER_ROOT_PATH = System.getProperty("user.dir");

    /** persagy-zkt-dmp 服务地址 */
    public static String zktDMP;
	
    /** 系统标识，用于创建人 */
    public static String systemId;
    
    /** 是否启用kafka推送 */
    public static boolean kafkaEnable;
    
    /** 集团编码 */
    public static String groupCode;

    /** 项目ID */
    public static String projectId;

    /** iot中所使用的项目ID，切记不带Pj */
    public static String building;
    
    /** 报警服务的服务地址 */
    public static String alarmServerUrl;

    /** 报警服务地址 */
    public static String alarmHost;
    
    /** 报警服务netty端口 */
    public static int alarmPort;

    /** 是否开启压缩 */
    public static boolean compress;

    /** iot-collect服务，是否启用 */
    public static boolean collectEnable;
    
    /** iot-collect服务的IP地址 */
    public static String collectHost;

    /** iot-collect服务的收数端口 */
    public static int collectPort;
    
    /** iot-project服务的webSocket地址 */
    public static String websocket;

    @Value("${system.id}")
    public void setSystemId(String value) {
        CommonConst.systemId = value;
    }
    
    @Value("${spring.kafka.enable:true}")
    public void setKafkaEnable(Boolean value) {
        CommonConst.kafkaEnable = value;
    }
    
    @Value("${config.group-code}")
    public void setGroupCode(String value) {
        CommonConst.groupCode = value;
    }
    
    @Value("${config.project-id}")
    public void setProjectId(String value) {
        CommonConst.projectId = value;
    }
    
    @Value("${config.building}")
    public void setBuilding(String value) {
        CommonConst.building = value;
    }
    
    @Value("${remote.ibms-alarm.server-url}")
    public void setAlarmServerUrl(String value) {
        CommonConst.alarmServerUrl = value;
    }
    
    @Value("${remote.ibms-alarm.host}")
    public void setAlarmHost(String value) {
        CommonConst.alarmHost = value;
    }
    
    @Value("${remote.ibms-alarm.port}")
    public void setAlarmPort(Integer value) {
        CommonConst.alarmPort = value;
    }
    
    @Value("${remote.ibms-alarm.compress:false}")
    public void setCompress(Boolean value) {
        CommonConst.compress = value;
    }
    
    @Value("${remote.iot.collect.enable:false}")
    public void setCollectEnable(Boolean value) {
        CommonConst.collectEnable = value;
    }
    
    @Value("${remote.iot.collect.host}")
    public void setCollectHost(String value) {
        CommonConst.collectHost = value;
    }
    
    @Value("${remote.iot.collect.port}")
    public void setCollectPort(Integer value) {
        CommonConst.collectPort = value;
    }
    
    @Value("${remote.iot.project.websocket}")
    public void setWebsocket(String value) {
        CommonConst.websocket = value;
    }
    
    @Value("${remote.persagy.zkt.dmp:}")
    public void setZktDMP(String value) {
        CommonConst.zktDMP = value;
    }
}