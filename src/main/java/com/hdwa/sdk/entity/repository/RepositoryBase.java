package com.hdwa.sdk.entity.repository;

import com.alibaba.fastjson.JSONObject;
import com.hdwa.sdk.entity.scene.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class RepositoryBase {
    public boolean use_thread;
    public boolean enable_factor;

    public boolean check_static_value_basic = false;
    public boolean check_Criteria_useless_item = false;// expression quote trend curve

    /**
     * <p>接口数据--全量接口json</p>
     * <p>数据来源config.json</p>
     */
    public JSONObject sceneJSON = new JSONObject();

    /**
     * <p>接口数据--全量接口对象</p>
     * <p>数据来源config.json</p>
     */
    public SceneObject sceneObject = new SceneObject();

    /**
     * <p>接口数据--属性sdv是否启用</p>
     */
    public boolean property2SDV_enable = true;

    /**
     * <p>接口数据--属性-sdv</p>
     * <p>sp--list-sdv</p>
     * <p>数据来源config.json</p>
     */
    public Map<SceneProperty, CopyOnWriteArrayList<SceneDataValue>> property2SDV = new HashMap<>(16);

    /**
     * <p>接口数据--自定义--上级属性</p>
     * <p>自定义custom_object包含的对象-->上级属性</p>
     * <p>数据来源config.json</p>
     */
    public Map<SceneObject, SceneProperty> customobject2host = new HashMap<>(16);

    /**
     * <p>接口数据--静态--同级属性</p>
     * <p>静态static_array包含的对象-->上级属性</p>
     * <p>数据来源config.json</p>
     */
    public Map<SceneObject, SceneProperty> staticobject2host = new HashMap<>(16);

    /**
     * <p>接口数据--静态--所在下标</p>
     * <p>静态static_array包含的对象-->下标值0开始</p>
     * <p>数据来源config.json</p>
     */
    public Map<SceneObject, Integer> staticobject2index = new HashMap<>(16);

    /**
     * <p>接口数据--static_array下的属性--静态对象列表</p>
     * <p>静态static_array下的单个属性-->同级所有静态对象元素</p>
     * <p>数据来源config.json</p>
     */
    public Map<SceneProperty, SceneObject> property2staticobject = new HashMap<>(16);

    /**
     * <p>接口数据--custom_object下的属性--同级对象列表</p>
     * <p>custom_object下的单个属性-->同级所有元素对象</p>
     * <p>数据来源config.json</p>
     */
    public Map<SceneProperty, SceneObject> property2customobject = new HashMap<>(16);

    /**
     * <p>接口数据--单个附加查询属性--上级属性</p>
     * <p>单个附加查询属性-->上级属性</p>
     * <p>数据来源config.json</p>
     */
    public Map<SceneProperty, SceneProperty> attachproperty2host = new HashMap<>(16);

    /**
     * <p>接口数据--属性--属性列表</p>
     * <p>属性-->属性</p>
     * <p>数据来源config.json</p>
     */
    public Map<SceneProperty, List<SceneProperty>> beforeDic = new HashMap<>(16);


    // deamon任务到点位清单
    public Map<SceneDataValue, List<String>> deamon_sdv2pointList = new HashMap<>(16);

    public Map<SceneProperty, Map<String, Boolean>> p2varDict = new HashMap<>(16);
    public Map<SceneProperty, Map<String, Boolean>> p2varStringDict = new HashMap<>(16);
    public Map<SceneProperty, WalkerWrapper> p2walker1 = new HashMap<>(16);
    public Map<SceneProperty, WalkerList> p2walker2 = new HashMap<>(16);

    // 结果数据
    public SceneDataObject objectData;
    public Map<String, SceneDataValue> base_value = new HashMap<>(16);


    public RepositoryDependency dependency = new RepositoryDependency();

    public int thread_count;
    public List<RepositoryComputeThread> threadList = new CopyOnWriteArrayList<>();
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

    public RepositoryBase() {

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
        return new ConcurrentHashMap<>();
    }

    public ConcurrentHashMap<SceneDataPrimitive, String> sdv2set() {
        return new ConcurrentHashMap<>();
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
}
