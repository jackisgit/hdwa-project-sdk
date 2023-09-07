package com.hdwa.sdk.entity.repository;

import com.alibaba.fastjson.JSONObject;
import com.hdwa.sdk.entity.scene.SceneDataObject;
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


    public boolean compute_finish = false;

    public Map<String, String> resourceMap_try = new ConcurrentHashMap<String, String>();
    public Map<String, String> resourceMap_valid = new ConcurrentHashMap<String, String>();


    public ConcurrentHashMap<String, List<SceneDataObject>> point2curve = new ConcurrentHashMap<String, List<SceneDataObject>>();

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
