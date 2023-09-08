package com.hdwa.sdk.service;

import com.hdwa.sdk.entity.repository.RepositoryImpl;
import com.hdwa.sdk.utils.FileUtil;
import com.hdwa.sdk.websocket.AlarmWebSocketClient;
import com.hdwa.sdk.websocket.IotWebSocketClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
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

    @Value("${project.id}")
    private String projectId;

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
        //RepositoryImpl repository = loadDataMainService.loadDataMain();
        //initIotWebsocket(repository);
        //initAlarmWebsocket();
    }

    /**
     * 如果本地没有数据就下载数据
     */
    private void downloadData() {
        log.warn("*****检查本地已下载的文件");
        String root = groupCode + File.separator + projectId + File.separator;
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
            File root = new File(groupCode + File.separator + projectId);
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
    private void initIotWebsocket(RepositoryImpl repository) {
        try {
            log.warn("************初始化iotWebSocket");
            String url = iotWebSocketUrl + "?projectId=" + projectId.substring(2) + "&type=iot,text,pointset";
            IotWebSocketClient client = new IotWebSocketClient(new URI(url), projectId, repository);
            client.connect();
        } catch (Exception e) {
            log.error("*****建立iotWebsocket异常", e);
        }
    }


    /**
     * alarmWebSocket连接
     */
    private void initAlarmWebsocket() {
        try {
            log.warn("************初始化alarmWebSocket");
            String url = alarmWebSocketUrl + "/" + projectId;
            AlarmWebSocketClient client = new AlarmWebSocketClient(new URI(url), alarmUrl, projectId, groupCode);
            client.connect();
        } catch (Exception e) {
            log.error("*****建立alarmWebsocket异常", e);
        }
    }
}
