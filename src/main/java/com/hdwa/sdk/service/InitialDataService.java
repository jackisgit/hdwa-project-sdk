package com.hdwa.sdk.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hdwa.sdk.constant.BaseDecConstant;
import com.hdwa.sdk.entity.ExcelSheetEntity;
import com.hdwa.sdk.entity.repository.DataContainer;
import com.hdwa.sdk.entity.repository.RepositoryImpl;
import com.hdwa.sdk.utils.AlarmUtil;
import com.hdwa.sdk.utils.ComputeThread;
import com.hdwa.sdk.utils.ExcelUtil;
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
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author abao
 * @since 2023/8/9
 * 初始数据
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

    @Autowired
    private PointService pointService;

    @Autowired
    private ConfigApiService configApiService;

    private final ThreadPoolExecutor variableThreadPool = new ThreadPoolExecutor(5, 10, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<>());


    @Override
    public void run(String... args) {
        initDir();
        downloadData();
        loadDataMainService.loadDataMain();
        initIotWebsocket();
        initAlarmWebsocket();
        startRefreshData();
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
    @Scheduled(initialDelay = 1000 * 60, fixedDelay = 1000 * 60)
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

    /**
     * 刷新报警数据
     */
    //@Scheduled(initialDelay = 1000 * 60, fixedDelay = 1000 * 60)
    public void loadAlarmData() {
        try {
            RepositoryImpl repository = DataContainer.projectMap.get(BaseDecConstant.CURRENT_PROJECT_ID);
            JSONArray content = AlarmUtil.alarmRefresh(BaseDecConstant.CURRENT_PROJECT_ID, groupCode, alarmUrl, repository);
            if (content.size() != 0) {
                log.warn("****定时刷新报警数据数量：" + content.size());
                JSONObject AlarmJob = new JSONObject();
                AlarmJob.put(BaseDecConstant.TYPE, BaseDecConstant.REFRESH);
                AlarmJob.put(BaseDecConstant.CONTENT, content);
                DataContainer.alarmBuffer.offer(AlarmJob, 16384);
            }
        } catch (Exception e) {
            log.error("****刷新报警数据出现异常", e);
        }
    }

    /**
     * 检查point过滤文件是否修改
     */
    @Scheduled(initialDelay = 1000 * 60, fixedDelay = 1000 * 60)
    public void resPointExile() throws Exception {
        boolean flag = false;
        Map<String, ExcelSheetEntity> map2 = ExcelUtil.readExcel(pointService.readPointXlsx());

        if (DataContainer.pointMap.size() != map2.size()) {
            flag = true;
        } else {
            for (Map.Entry<String, ExcelSheetEntity> entry : DataContainer.pointMap.entrySet()) {
                String key = entry.getKey();
                ExcelSheetEntity value1 = entry.getValue();
                ExcelSheetEntity value2 = map2.get(key);
                if (!value1.equals(value2)) {
                    flag = true;
                }
            }
        }
        if (flag) {
            log.warn("*****开始更新点位过滤数据");
            loadDataMainService.updatePoint(DataContainer.projectMap.get(BaseDecConstant.CURRENT_PROJECT_ID));
        }
    }

    /**
     * 刷新数据
     */
    //@Scheduled(initialDelay = 1000 * 60, fixedDelay = 1000 * 60)
    public void refreshData() {
        // 重算iot，alarm等
        RepositoryImpl repository = DataContainer.projectMap.get(BaseDecConstant.CURRENT_PROJECT_ID);
        int[] count = repository.recomputeIot();
        log.warn("iot数据：" + Arrays.toString(count));
        count = repository.recomputeAlarm();
        log.warn("alarm数据：" + Arrays.toString(count));
    }


    /**
     * 刷新改变数据
     */
    public void startRefreshData() {
        RepositoryImpl repository = DataContainer.projectMap.get(BaseDecConstant.CURRENT_PROJECT_ID);
        for (int i = 0; i < 5; i++) {
            Runnable thread = new ComputeThread(repository, 10);
            variableThreadPool.execute(thread);
        }
    }
}
