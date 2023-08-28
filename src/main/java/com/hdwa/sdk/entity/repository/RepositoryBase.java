package com.hdwa.sdk.entity.repository;

import com.alibaba.fastjson.JSONObject;
import com.hdwa.sdk.entity.scene.*;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class RepositoryBase {
    public boolean use_thread;
    public boolean enable_factor;

    public boolean check_static_value_basic = false;
    public boolean check_Criteria_useless_item = false;// expression quote trend curve

    // deamon任务到点位清单
    public Map<SceneDataValue, List<String>> deamon_sdv2pointList = new ConcurrentHashMap<SceneDataValue, List<String>>();

    // 属性计算先后关系
    public Map<SceneProperty, List<SceneProperty>> beforeDic = new ConcurrentHashMap<SceneProperty, List<SceneProperty>>();

    // 场景对象
    public JSONObject sceneJSON = new JSONObject();
    public SceneObject sceneObject = new SceneObject();
    public Map<SceneProperty, SceneObject> property2customobject = new ConcurrentHashMap<SceneProperty, SceneObject>();// custom_object
    public Map<SceneObject, SceneProperty> customobject2host = new ConcurrentHashMap<SceneObject, SceneProperty>();// custom_object
    public Map<SceneProperty, SceneObject> property2staticobject = new ConcurrentHashMap<SceneProperty, SceneObject>();// static_array
    public Map<SceneObject, SceneProperty> staticobject2host = new ConcurrentHashMap<SceneObject, SceneProperty>();// static_array
    public Map<SceneObject, Integer> staticobject2index = new ConcurrentHashMap<SceneObject, Integer>();// static_array
    public Map<SceneProperty, SceneProperty> attachproperty2host = new ConcurrentHashMap<SceneProperty, SceneProperty>();// attached
    public Map<SceneProperty, Map<String, Boolean>> p2varDict = new ConcurrentHashMap<SceneProperty, Map<String, Boolean>>();
    public Map<SceneProperty, Map<String, Boolean>> p2varStringDict = new ConcurrentHashMap<SceneProperty, Map<String, Boolean>>();
    public Map<SceneProperty, WalkerWrapper> p2walker1 = new ConcurrentHashMap<SceneProperty, WalkerWrapper>();
    public Map<SceneProperty, WalkerList> p2walker2 = new ConcurrentHashMap<SceneProperty, WalkerList>();

    // 结果数据
    public SceneDataObject objectData;
    public Map<String, SceneDataValue> base_value = new ConcurrentHashMap<String, SceneDataValue>();

    // 属性到sdv清单
    public boolean property2SDV_enable = true;
    public Map<SceneProperty, CopyOnWriteArrayList<SceneDataValue>> property2SDV = new ConcurrentHashMap<SceneProperty, CopyOnWriteArrayList<SceneDataValue>>();

    public RepositoryDependency dependency = new RepositoryDependency();

    public int thread_count;
    public List<RepositoryComputeThread> threadList = new CopyOnWriteArrayList<RepositoryComputeThread>();
    public WaitComputeQueue WaitCompute = new WaitComputeQueue();

    public RepositoryBase(boolean use_thread, boolean enable_factor, int thread_count, long interval_between_compute) {
        this.use_thread = use_thread;
        this.enable_factor = enable_factor;
        this.thread_count = thread_count;
        for (int i = 0; i < this.thread_count; i++) {
            RepositoryComputeThread thread = new RepositoryComputeThread(this, interval_between_compute);
            threadList.add(thread);
        }
    }

    public RepositoryBase(){

    }

    public void threadStart() {
        for (RepositoryComputeThread thread : this.threadList) {
            thread.start();
        }
    }

    public void threadStop() {
        for (RepositoryComputeThread thread : this.threadList) {
            thread.requestStop();
        }
    }

    public ConcurrentHashMap<SceneDataPrimitive, String> sdv2point() {
        return new ConcurrentHashMap<SceneDataPrimitive, String>();
    }

    public ConcurrentHashMap<SceneDataPrimitive, String> sdv2set() {
        return new ConcurrentHashMap<SceneDataPrimitive, String>();
    }

    public SceneDataSet ParseSource(JSONObject descSet, String Source) {
        return null;
    }

    public int addWaitCompute(SceneDataSet set) {
        int add_count = 0;
        try {
            List<SceneDataValue> sdvAffectList = new CopyOnWriteArrayList<SceneDataValue>();
            JSONObject result = new JSONObject();
            this.dependency.get_after_value_array(set, sdvAffectList, result);
            for (SceneDataValue sdvAffect : sdvAffectList) {
                this.WaitCompute.offer(new WaitItem(sdvAffect, new Date()));
                add_count++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return add_count;
    }

    public void addWaitCompute(SceneDataSet value_array, String col) {
        if (this.dependency.SetColumn2sdv.containsKey(value_array)) {
            Map<String, Map<SceneDataValue, Boolean>> Column2sdv = this.dependency.SetColumn2sdv.get(value_array);
            if (Column2sdv.containsKey(col)) {
                Map<SceneDataValue, Boolean> afterList = Column2sdv.get(col);
                for (SceneDataValue key : afterList.keySet()) {
                    this.WaitCompute.offer(new WaitItem(key, new Date()));
                }
            }
        }
    }

    public int addWaitCompute(SceneDataValue sdv) {
        int add_count = 0;
        try {
            List<SceneDataValue> sdvAffectList = new CopyOnWriteArrayList<SceneDataValue>();
            this.dependency.get_after(this, sdv, sdvAffectList);
            for (SceneDataValue sdvAffect : sdvAffectList) {
                this.WaitCompute.offer(new WaitItem(sdvAffect, new Date()));
                add_count++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return add_count;
    }

    public void ComputeOccur(SceneDataValue sdv) {

    }

    public void log_step_count(int step_count) {

    }

    public void log_step_begin(int step, int property_count, int value_count) {

    }

    public void log_step_end(int step, int finish_count) {

    }

    public void log_error(String path, String message) {

    }
}
