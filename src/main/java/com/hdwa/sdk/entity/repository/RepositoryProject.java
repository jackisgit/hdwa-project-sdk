package com.hdwa.sdk.entity.repository;

import com.alibaba.fastjson.JSONObject;
import com.hdwa.sdk.entity.scene.SceneDataObject;
import com.hdwa.sdk.entity.scene.SceneDataPrimitive;
import com.hdwa.sdk.entity.scene.SceneDataSet;
import com.hdwa.sdk.entity.scene.SceneDataValue;
import com.hdwa.sdk.utils.LogOfDownload;
import com.hdwa.sdk.utils.LogOfRun;
import com.hdwa.sdk.utils.PacketBuffer;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class RepositoryProject {
    public String groupCode;
    public String projectId;

    // IOT采集数据
    /**
     * <p>运行点位值--数据</p>
     * <p>运行点位值 对象数据--->数据</p>
     * <p>数据来源 physical_world/object/*.json</p>
     */
    public ConcurrentHashMap<String, SceneDataPrimitive> point2sdv = new ConcurrentHashMap<>(16);

    /**
     * <p>数据--运行点位值</p>
     * <p>数据 --->运行点位值</p>
     * <p>数据来源 physical_world/object/*.json</p>
     */
    public ConcurrentHashMap<SceneDataPrimitive, String> sdv2point = new ConcurrentHashMap<>(16);


    // IOT设置数据
    /**
     * <p>设定点位值--数据</p>
     * <p>设定点位值 对象数据--->数据</p>
     * <p>数据来源 physical_world/object/*.json</p>
     */
    public ConcurrentHashMap<String, SceneDataPrimitive> set2sdv = new ConcurrentHashMap<>(16);

    /**
     * <p>数据--设定点位值</p>
     * <p>数据 --->设定点位值</p>
     * <p>数据来源 physical_world/object/*.json</p>
     */
    public ConcurrentHashMap<SceneDataPrimitive, String> sdv2set = new ConcurrentHashMap<>(16);

    public boolean compute_finish = false;

    public Map<String, String> resourceMap_try = new ConcurrentHashMap<String, String>();
    public Map<String, String> resourceMap_valid = new ConcurrentHashMap<String, String>();


    public ConcurrentHashMap<String, List<SceneDataObject>> point2curve = new ConcurrentHashMap<String, List<SceneDataObject>>();

    // 报警数据
    public SceneDataSet alarmArray = new SceneDataSet(false, true);
    public ConcurrentHashMap<String, SceneDataValue> id2alarmList = new ConcurrentHashMap<String, SceneDataValue>();
    public ConcurrentHashMap<String, SceneDataValue> id2alarmCount = new ConcurrentHashMap<String, SceneDataValue>();
    public PacketBuffer<JSONObject> alarmBuffer = new PacketBuffer<JSONObject>();

    // 指定路径的控制值
    public ConcurrentHashMap<String, JSONObject> controlValueMap = new ConcurrentHashMap<String, JSONObject>();

    public List<JSONObject> LogOfAlarmList = new CopyOnWriteArrayList<JSONObject>();

    public LogOfRun LogOfRun;
    public List<com.hdwa.sdk.utils.LogOfRun> LogOfRunList = new CopyOnWriteArrayList<LogOfRun>();
    public List<LogOfRun> SuccessLogOfRunList = new CopyOnWriteArrayList<LogOfRun>();

    public Map<String, List<LogOfDownload>> LogOfDownloadListMap = new ConcurrentHashMap<String, List<LogOfDownload>>();
    public Map<String, List<LogOfDownload>> SuccessLogOfDownloadListMap = new ConcurrentHashMap<String, List<LogOfDownload>>();

    public void AddLogOfRun(LogOfRun LogOfRun) {
        this.LogOfRun = LogOfRun;
        this.LogOfRunList.add(0, LogOfRun);
        for (int i = this.LogOfRunList.size() - 1; i > 32; i--) {
            this.LogOfRunList.remove(i);
        }
    }

    public void AddSuccessLogOfRun(LogOfRun LogOfRun) {
        this.SuccessLogOfRunList.add(0, LogOfRun);
        for (int i = this.SuccessLogOfRunList.size() - 1; i > 4; i--) {
            this.SuccessLogOfRunList.remove(i);
        }
    }

    public void AddLogOfAlarm(JSONObject LogOfRun) {
        this.LogOfAlarmList.add(0, LogOfRun);
        for (int i = this.LogOfAlarmList.size() - 1; i > 1024; i--) {
            this.LogOfAlarmList.remove(i);
        }
    }

    public void AddLogOfDownload(String name, LogOfDownload LogOfDownload) {
        List<LogOfDownload> list = this.LogOfDownloadListMap.get(name);
        list.add(0, LogOfDownload);
        for (int i = list.size() - 1; i > 32; i--) {
            list.remove(i);
        }
    }

    public void AddSuccessLogOfDownload(String name, LogOfDownload LogOfDownload) {
        List<LogOfDownload> list = this.SuccessLogOfDownloadListMap.get(name);
        list.add(0, LogOfDownload);
        for (int i = list.size() - 1; i > 4; i--) {
            list.remove(i);
        }
    }
}
