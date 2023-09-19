package com.hdwa.sdk.service;

import com.hdwa.sdk.constant.BaseDecConstant;
import com.hdwa.sdk.utils.FileUtil;
import com.hdwa.sdk.websocket.AlarmWebSocketClient;
import com.hdwa.sdk.websocket.IotWebSocketClient;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.WebSocket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.net.URI;
import java.nio.file.Files;

/**
 * @author abao
 * @since 2023/8/9
 * 初始目录
 */
@Slf4j
@Component
public class InitialDataService implements CommandLineRunner {

    @Value("${project.groupCode}")
    private String groupCode;

    @Value("${dirName.config}")
    private String config;

    @Value("${dirName.point}")
    private String point;

    @Value("${dirName.physicalWorld}")
    private String physicalWorld;

    @Value("${dirName.ibmsPhysicalWorld}")
    private String ibmsPhysicalWorld;

    @Value("${dirName.ibmsLogicalGroup}")
    private String ibmsLogicalGroup;

    @Value("${dirName.temp}")
    private String temp;

    @Value("${url.iotWebSocket}")
    private String iotWebSocketUrl;

    @Value("${url.alarmWebSocket}")
    private String alarmWebSocketUrl;

    @Value("${url.alarmUrl}")
    private String alarmUrl;

    @Autowired
    private LoadDataMainService loadDataMainService;

    @Override
    public void run(String... args) {
        initDir();
        downloadData();
        loadDataMainService.loadDataMain();
        initIotWebsocket();
        initAlarmWebsocket();
    }

    /**
     * 如果本地没有数据就下载数据
     */
    private void downloadData() {
        log.warn("*****检查本地已下载的文件");
        String root = groupCode + File.separator + BaseDecConstant.CURRENT_PROJECT_ID + File.separator;
        File physicalWorldDir = FileUtil.getMaxDir(new File(root + physicalWorld));
        File ibmsPhysicalWorldDir = FileUtil.getMaxDir(new File(root + ibmsPhysicalWorld));
        File ibmsLogicalGroupDir = FileUtil.getMaxDir(new File(root + ibmsLogicalGroup));
        File pointDir = FileUtil.getMaxDir(new File(root + point));
        File configDir = FileUtil.getMaxDir(new File(root + config));
        boolean flag = false;
        if (physicalWorldDir == null) {
            flag = true;
        }
        if (ibmsPhysicalWorldDir == null) {
            flag = true;
        }
        if (ibmsLogicalGroupDir == null) {
            flag = true;
        }
        if (pointDir == null) {
            flag = true;
        }
        if (configDir == null) {
            flag = true;
        }
        if (flag) {
            loadDataMainService.downLoadDataMain();
        }
    }

    /**
     * 初始文件夹
     */
    private void initDir() {
        log.warn("*****初始数据文件夹");
        try {
            // 创建根目录文件夹
            File root = new File(groupCode + File.separator + BaseDecConstant.CURRENT_PROJECT_ID);
            if (!root.exists()) {
                log.warn(root.getPath());
                Files.createDirectories(root.toPath());
            }

            //json接口配置文件夹
            File configPath = new File(root.getPath() + File.separator + config);
            if (!configPath.exists()) {
                log.warn(configPath.getPath());
                Files.createDirectories(configPath.toPath());
            }

            //点位数据文件夹
            File pointPath = new File(root.getPath() + File.separator + point);
            if (!pointPath.exists()) {
                log.warn(pointPath.getPath());
                Files.createDirectories(pointPath.toPath());
            }

            //物理世界数据文件夹
            File physicalWorldPath = new File(root.getPath() + File.separator + physicalWorld);
            if (!physicalWorldPath.exists()) {
                log.warn(physicalWorldPath.getPath());
                Files.createDirectories(physicalWorldPath.toPath());
            }

            //ibms物理世界数据文件夹
            File ibmsPhysicalWorldPath = new File(root.getPath() + File.separator + ibmsPhysicalWorld);
            if (!ibmsPhysicalWorldPath.exists()) {
                log.warn(ibmsPhysicalWorldPath.getPath());
                Files.createDirectories(ibmsPhysicalWorldPath.toPath());
            }

            //ibms逻辑分组数据文件夹
            File ibmsLogicalGroupPath = new File(root.getPath() + File.separator + ibmsLogicalGroup);
            if (!ibmsLogicalGroupPath.exists()) {
                log.warn(ibmsLogicalGroupPath.getPath());
                Files.createDirectories(ibmsLogicalGroupPath.toPath());
            }

            //临时数据文件夹
            File tempPath = new File(root.getPath() + File.separator + temp);
            if (!tempPath.exists()) {
                log.warn(tempPath.getPath());
                Files.createDirectories(tempPath.toPath());
            }

        } catch (Exception e) {
            log.error("初始化文件异常");
        }

    }


    /**
     * iotWebSocket连接
     */
    private IotWebSocketClient iotClient;
    private void initIotWebsocket() {
        try {
            log.warn("************初始化iotWebSocket");
            String url = iotWebSocketUrl + "?projectId=" + BaseDecConstant.CURRENT_PROJECT_ID.substring(2) + "&type=iot,text,pointset";
            iotClient = new IotWebSocketClient(new URI(url), BaseDecConstant.CURRENT_PROJECT_ID);
            iotClient.connect();
        } catch (Exception e) {
            log.error("*****建立iotWebsocket异常", e);
        }
    }


    /**
     * alarmWebSocket连接
     */
    private AlarmWebSocketClient alarmClient;
    private void initAlarmWebsocket() {
        try {
            log.warn("************初始化alarmWebSocket");
            String url = alarmWebSocketUrl + "/" + BaseDecConstant.CURRENT_PROJECT_ID;
            alarmClient = new AlarmWebSocketClient(new URI(url), alarmUrl, BaseDecConstant.CURRENT_PROJECT_ID, groupCode);
            alarmClient.connect();
        } catch (Exception e) {
            log.error("*****建立alarmWebsocket异常", e);
        }
    }

    /**
     * webSocket重新连接
     */
    @Scheduled(initialDelay = 1000 * 60, fixedDelay = 1000 * 30)
    public void resConnection() {
        //iot重连接
        try {
            if (!iotClient.isOpen()) {
                log.error("************iotWebSocket连接已断开，正在重新连接，当前状态为[{}]", iotClient.getReadyState());
                if (iotClient.getReadyState().equals(WebSocket.READYSTATE.CLOSING)
                        || iotClient.getReadyState().equals(WebSocket.READYSTATE.CLOSED)
                        || iotClient.getReadyState().equals(WebSocket.READYSTATE.NOT_YET_CONNECTED)) {
                    iotClient.reconnect();
                }
            }
        } catch (Exception e) {
            log.error("************iotWebSocket连接异常，尝试重新连接：" + e.getMessage());
            //关闭异常的连接
            if (!iotClient.isOpen()) {
                iotClient.close();
            }
            //重新连接
            iotClient.connect();
        }

        //报警重连
        try {
            if (!alarmClient.isOpen()) {
                log.error("************alarmWebSocket连接已断开，正在重新连接，当前状态为[{}]", alarmClient.getReadyState());
                if (alarmClient.getReadyState().equals(WebSocket.READYSTATE.CLOSING)
                        || alarmClient.getReadyState().equals(WebSocket.READYSTATE.CLOSED)
                        || alarmClient.getReadyState().equals(WebSocket.READYSTATE.NOT_YET_CONNECTED)) {
                    alarmClient.reconnect();
                }
            }
        } catch (Exception e) {
            log.error("************alarmWebSocket连接异常，尝试重新连接：" + e.getMessage());
            //关闭异常的连接
            if (!alarmClient.isOpen()) {
                alarmClient.close();
            }
            //重新连接
            alarmClient.connect();
        }
    }

}
