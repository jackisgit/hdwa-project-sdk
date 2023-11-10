package com.hdwa.sdk.entity.repository;

import com.alibaba.fastjson.JSONObject;
import com.hdwa.sdk.entity.scene.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 仓库基础
 */
public class RepositoryBase {

    /**
     * <p>接口数据--全量接口json</p>
     * <p>数据来源config.json</p>
     */
    public JSONObject sceneJSON = new JSONObject();

    /**
     * <p>接口数据--全量接口对象</p>
     * <p>数据来源config.json</p>
     */
    public DataObjectBase dataObjectBase = new DataObjectBase();

    /**
     * <p>接口数据--属性sdv是否启用</p>
     */
    public boolean property2SDV_enable = true;

    /**
     * <p>接口数据--属性-sdv</p>
     * <p>sp--list-sdv</p>
     * <p>数据来源config.json</p>
     */
    public Map<DataProperty, CopyOnWriteArrayList<DataValue>> property2SDV = new HashMap<>(16);

    /**
     * <p>接口数据--自定义--上级属性</p>
     * <p>自定义custom_object包含的对象-->上级属性</p>
     * <p>数据来源config.json</p>
     */
    public Map<DataObjectBase, DataProperty> customobject2host = new HashMap<>(16);

    /**
     * <p>接口数据--静态--同级属性</p>
     * <p>静态static_array包含的对象-->上级属性</p>
     * <p>数据来源config.json</p>
     */
    public Map<DataObjectBase, DataProperty> staticobject2host = new HashMap<>(16);

    /**
     * <p>接口数据--静态--所在下标</p>
     * <p>静态static_array包含的对象-->下标值0开始</p>
     * <p>数据来源config.json</p>
     */
    public Map<DataObjectBase, Integer> staticobject2index = new HashMap<>(16);

    /**
     * <p>接口数据--static_array下的属性--静态对象列表</p>
     * <p>静态static_array下的单个属性-->同级所有静态对象元素</p>
     * <p>数据来源config.json</p>
     */
    public Map<DataProperty, DataObjectBase> property2staticobject = new HashMap<>(16);

    /**
     * <p>接口数据--custom_object下的属性--同级对象列表</p>
     * <p>custom_object下的单个属性-->同级所有元素对象</p>
     * <p>数据来源config.json</p>
     */
    public Map<DataProperty, DataObjectBase> property2customobject = new HashMap<>(16);

    /**
     * <p>接口数据--单个附加查询属性--上级属性</p>
     * <p>单个附加查询属性-->上级属性</p>
     * <p>数据来源config.json</p>
     */
    public Map<DataProperty, DataProperty> attachproperty2host = new HashMap<>(16);

    /**
     * <p>接口数据--属性--属性列表</p>
     * <p>属性-->属性</p>
     * <p>数据来源config.json</p>
     */
    public Map<DataProperty, List<DataProperty>> beforeDic = new HashMap<>(16);

    /**
     * 查询结果数据
     */
    public DataObject objectData;


    public Map<DataProperty, Map<String, Boolean>> p2varDict = new HashMap<>(16);
    public Map<DataProperty, Map<String, Boolean>> p2varStringDict = new HashMap<>(16);
    public Map<DataProperty, WalkerWrapper> p2walker1 = new HashMap<>(16);
    public Map<DataProperty, WalkerList> p2walker2 = new HashMap<>(16);


    public Map<String, DataValue> base_value = new HashMap<>(16);

    public RepositoryDependency dependency = new RepositoryDependency();

    public WaitComputeQueue WaitCompute = new WaitComputeQueue();

    public boolean enable_factor = true;

    public RepositoryBase() {
    }

    public ConcurrentHashMap<DataPrimitive, String> sdv2point() {
        return new ConcurrentHashMap<>();
    }

    public ConcurrentHashMap<DataPrimitive, String> sdv2set() {
        return new ConcurrentHashMap<>();
    }


    public DataSet parseSource(JSONObject descSet, String Source) {
        return null;
    }

    public int addWaitCompute(DataSet set) {
        int add_count = 0;
        try {
            List<DataValue> sdvAffectList = new CopyOnWriteArrayList<DataValue>();
            JSONObject result = new JSONObject();
            this.dependency.get_after_value_array(set, sdvAffectList, result);
            for (DataValue sdvAffect : sdvAffectList) {
                this.WaitCompute.offer(new WaitItem(sdvAffect, new Date()));
                add_count++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return add_count;
    }

    public void addWaitCompute(DataSet value_array, String col) {
        if (this.dependency.SetColumn2sdv.containsKey(value_array)) {
            Map<String, Map<DataValue, Boolean>> Column2sdv = this.dependency.SetColumn2sdv.get(value_array);
            if (Column2sdv.containsKey(col)) {
                Map<DataValue, Boolean> afterList = Column2sdv.get(col);
                for (DataValue key : afterList.keySet()) {
                    this.WaitCompute.offer(new WaitItem(key, new Date()));
                }
            }
        }
    }

    public int addWaitCompute(DataValue sdv) {
        int add_count = 0;
        try {
            List<DataValue> sdvAffectList = new CopyOnWriteArrayList<>();
            this.dependency.get_after(this, sdv, sdvAffectList);
            for (DataValue sdvAffect : sdvAffectList) {
                this.WaitCompute.offer(new WaitItem(sdvAffect, new Date()));
                add_count++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return add_count;
    }

}
