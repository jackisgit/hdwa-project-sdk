package com.hdwa.sdk.entity.repository;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hdwa.sdk.constant.BaseDecConstant;
import com.hdwa.sdk.entity.scene.SceneDataObject;
import com.hdwa.sdk.entity.scene.SceneDataPrimitive;
import com.hdwa.sdk.entity.scene.SceneDataSet;
import com.hdwa.sdk.entity.scene.SceneDataValue;
import com.hdwa.sdk.utils.RWDUtil;
import com.hdwa.sdk.websocket.WebSocketUtil;
import lombok.extern.slf4j.Slf4j;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
public class RepositoryImpl extends RepositoryBase {

    /**
     * <p>物理世界</p>
     * <p>类型定义-全量数据</p>
     * <p>数据来源 physical_world/classArray.json</p>
     */
    public SceneDataSet classArray = new SceneDataSet(false);

    /**
     * <p>类型定义数据--类型标记</p>
     * <p>objType--->boolean </p>
     * <p>true：code=objType</p>
     * <p>false：code!=objType</p>
     * <p>数据来源 physical_world/classArray.json</p>
     */
    public Map<String, Boolean> objTypeMap = new HashMap<>(16);

    /**
     * <p>类型定义数据--code-对象类型</p>
     * <p>classCode--->objType</p>
     * <p>数据来源 physical_world/classArray.json</p>
     */
    public Map<String, String> code2objTypeMap = new HashMap<>(16);

    /**
     * <p>类型定义数据--code-类型名称</p>
     * <p>classCode--->name</p>
     * <p>数据来源 physical_world/classArray.json</p>
     */
    public Map<String, String> classCode2NameMap = new HashMap<>(16);


    /**
     * <p>点位定义数据--code-对象类型列表</p>
     * <p>classCode--->sds</p>
     * <p>数据来源 physical_world/point/*.json</p>
     */
    public Map<String, SceneDataSet> infoArrayDic = new HashMap<>(16);


    /**
     * <p>点位定义数据--code-对象类型列表</p>
     * <p>classCode--->JsonArray</p>
     * <p>数据来源 physical_world/point/*.json</p>
     */
    public Map<String, JSONArray> infoArrayJson = new HashMap<>(16);


    /**
     * <p>dataSource数据</p>
     * <p>{"classCode":"FFEACU","code":"0","name":"正常","infoCode":"orderFailAlarm"}</p>
     * <p>{"classCode":"FFEACU","code":"1","name":"报警","infoCode":"orderFailAlarm"}</p>
     * <p>这两条数据等于"dataSource":[{"code":"0","name":"正常"},{"code":"1","name":"报警"}]</p>
     * <p>数据来源 physical_world/point/*.json</p>
     */
    public SceneDataSet infoDataSource = new SceneDataSet(false);

    /**
     * <p>对象数据--id-对象</p>
     * <p>id--->JsonObject</p>
     * <p>数据来源 physical_world/object/*.json</p>
     */
    public Map<String, JSONObject> id2object = new HashMap<>(16);

    /**
     * <p>对象数据--id-sdo</p>
     * <p>id--->sdo</p>
     * <p>数据来源 physical_world/object/*.json</p>
     */
    public Map<String, SceneDataObject> id2sdv = new HashMap<>(16);

    /**
     * <p>对象数据--全量数据</p>
     * <p>数据来源 physical_world/object/*.json</p>
     */
    public SceneDataSet objectArrayAll = new SceneDataSet(false);

    /**
     * <p>对象数据--全量数据</p>
     * <p>数据来源 physical_world/object/*.json</p>
     */
    public Map<String, SceneDataValue> objectArrayDic = new HashMap<>(16);

    /**
     * <p>对象数据--objType-（id-对象数据）</p>
     * <p>objType--->（id-->sdo）</p>
     * <p>数据来源 physical_world/object/*.json</p>
     */
    public Map<String, Map<String, SceneDataObject>> objType2id2Value = new HashMap<>(16);


    /**
     * <p>关系数据--graphCode-（relCode-关系数据）</p>
     * <p>graphCode 图例编码--->（relCode-->sds）</p>
     * <p>数据来源 physical_world/relation/*.json</p>
     */
    public Map<String, Map<String, SceneDataSet>> relationArrayDic = new HashMap<>(16);

    /**
     * <p>关系数据--graphCode-关系数据</p>
     * <p>graphCode 图例编码--->sds</p>
     * <p>数据来源 physical_world/relation/*.json</p>
     */
    public Map<String, SceneDataSet> graphCodeDic = new HashMap<>(16);

    /**
     * <p>关系数据--relCode-关系数据</p>
     * <p>relCode 关系编码--->sds</p>
     * <p>数据来源 physical_world/relation/*.json</p>
     */
    public Map<String, SceneDataSet> relCodeDic = new HashMap<>(16);

    /**
     * <p>关系数据--全量数据</p>
     * <p>sds</p>
     * <p>数据来源 physical_world/relation/*.json</p>
     */
    public SceneDataSet relationAll = new SceneDataSet(false);

    /**
     * <p>对象id--点位数据-（point-value）</p>
     * <p>objId 对象数据--->（point-->value）</p>
     * <p>数据来源 physical_world/object/*.json</p>
     */
    public Map<String, HashMap<String, String>> object2info2point = new HashMap<>(16);

    /**
     * <p>运行点位值--对象信息</p>
     * <p>运行点点位值 对象数据--->对象信息</p>
     * <p>数据来源 physical_world/object/*.json</p>
     */
    public Map<String, List<ObjectInfo>> point2ObjectInfoList = new HashMap<>(16);

    /**
     * <p>设定点位值--对象信息</p>
     * <p>设定点位值 对象数据--->对象信息</p>
     * <p>数据来源 physical_world/object/*.json</p>
     */
    public Map<String, List<ObjectInfo>> set2ObjectInfoList = new HashMap<>(16);


    /**
     * <p>IBMS物理世界</p>
     * <p>场景对象</p>
     * <p>数据来源 ibms_physical_world/sceneArray.json</p>
     */
    public SceneDataSet ZKTSceneArray = new SceneDataSet(false);


    /**
     * <p>IBMS物理世界</p>
     * <p>场景编码--对象数据 (ibms类型编码->对象数据)</p>
     * <p>ibmsSceneCode--ibmsClassCode->对象数据 </p>
     * <p>数据来源 ibms_physical_world/sceneArray.json</p>
     */
    public Map<String, Map<String, SceneDataValue>> ZKTObjectArrayDic = new HashMap<>(16);

    /**
     * <p>IBMS物理世界</p>
     * <p>类型定义数据</p>
     * <p>数据来源 ibms_physical_world/classArray.json</p>
     */
    public SceneDataSet ZKTClassArray = new SceneDataSet(false);


    /**
     * <p>IBMS逻辑编组</p>
     * <p>逻辑编组数据</p>
     * <p>数据来源 ibms_logical_group/ibmsLogicalGroup.json</p>
     */
    public SceneDataSet IBMSGroupArray = new SceneDataSet(false);


    /**
     * <p>IBMS逻辑编组</p>
     * <p>场景编码--分组数据 (ibms类型编码->分组数据)</p>
     * <p>ibmsSceneCode--ibmsClassCode->分组数据 </p>
     * <p>数据来源 ibms_logical_group/**.json</p>
     */
    public Map<String, Map<String, SceneDataSet>> IBMSArrayDic = new HashMap<>(16);


    /**
     * <p>点位数据</p>
     * <p>点位数据</p>
     * <p>数据来源 point/point-list.json</p>
     */
    public SceneDataSet InfoPointListArray = new SceneDataSet(false, BaseDecConstant.INFO_POINT_LIST);

    /**
     * <p>点位数据</p>
     * <p>点位关系数据</p>
     * <p>数据来源 point/point-relation.json</p>
     */
    public SceneDataSet InfoPointRelationArray = new SceneDataSet(false, BaseDecConstant.INFO_POINT_RELATION);


    /**
     * <p>IOT采集数据</p>
     * <p>运行点位值--数据</p>
     * <p>运行点位值 对象数据--->数据</p>
     * <p>数据来源 physical_world/object/*.json</p>
     */
    public ConcurrentHashMap<String, SceneDataPrimitive> point2sdv = new ConcurrentHashMap<>(16);

    /**
     * <p>IOT采集数据</p>
     * <p>数据--运行点位值</p>
     * <p>数据 --->运行点位值</p>
     * <p>数据来源 physical_world/object/*.json</p>
     */
    public ConcurrentHashMap<SceneDataPrimitive, String> sdv2point = new ConcurrentHashMap<>(16);


    /**
     * <p>IOT设置数据</p>
     * <p>设定点位值--数据</p>
     * <p>设定点位值 对象数据--->数据</p>
     * <p>数据来源 physical_world/object/*.json</p>
     */
    public ConcurrentHashMap<String, SceneDataPrimitive> set2sdv = new ConcurrentHashMap<>(16);

    /**
     * <p>IOT设置数据</p>
     * <p>数据--设定点位值</p>
     * <p>数据 --->设定点位值</p>
     * <p>数据来源 physical_world/object/*.json</p>
     */
    public ConcurrentHashMap<SceneDataPrimitive, String> sdv2set = new ConcurrentHashMap<>(16);


    /**
     * <p>报警数据</p>
     */
    public SceneDataSet alarmArray = new SceneDataSet(false, true);

    /**
     * <p>报警数据</p>
     * <p>报警列表</p>
     * <p>objId--sdv</p>
     * <p>对象id --->报警列表</p>
     */
    public Map<String, SceneDataValue> id2alarmList = new HashMap<>(16);


    /**
     * <p>报警数据</p>
     * <p>报警数量</p>
     * <p>objId--sdv</p>
     * <p>对象id --->报警数量</p>
     */
    public Map<String, SceneDataValue> id2alarmCount = new HashMap<>(16);


    public static boolean accelerate_enable = false;
    public static long accelerate_ratio = 60 * 60 * 24;
    public static String init_timeString = "2021-01-01 00:00:00";
    public static Date init_time;
    public static Date start_time;
    public Map<String, JSONObject> general_queryMap;
    public RepositoryProject RepositoryProject;
    // to do lirong
    // 报警配置数据
    public SceneDataSet alarmConfigArray = new SceneDataSet(false);// id2alarmConfigTrigger
    public SceneDataSet alarmConfigTrigger = new SceneDataSet(false);


    public SceneDataSet ZKTAlarmTypeArray = new SceneDataSet(false);
    /**
     * @子系统连接状态
     * @数据来源 /sceneArray.json
     */
    public SceneDataSet subsystem_connect_status = new SceneDataSet(false);


    public SceneDataSet scaleplate = new SceneDataSet(false, "scaleplate");
    // 日历模式：周期性刷新，不需要拷贝
    public SceneDataSet IBMSCalendarModel = new SceneDataSet(false, true);
    public SceneDataSet IBMSCalendarBinding = new SceneDataSet(false, true);
    public SceneDataSet weather = new SceneDataSet(false, true);


    public RepositoryImpl() {
        super();
    }


    public RepositoryImpl(RepositoryProject RepositoryProject, boolean use_thread, boolean enable_factor, int thread_count,
                          long interval_between_compute) {
        super(use_thread, enable_factor, thread_count, interval_between_compute);
        this.RepositoryProject = RepositoryProject;
        this.base_value = generate_base_value();
    }

    public static Map<String, SceneDataValue> generate_base_value() {
        Map<String, SceneDataValue> base_value = new ConcurrentHashMap<String, SceneDataValue>();
        Calendar calendar = Calendar.getInstance();
        Map<String, Date> timeMap = new ConcurrentHashMap<String, Date>();
        Map<String, SimpleDateFormat> sdfMap = new ConcurrentHashMap<String, SimpleDateFormat>();
        sdfMap.put("simple", new SimpleDateFormat("yyyyMMddHHmmss"));
        sdfMap.put("normal", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        sdfMap.put("T", new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"));

        Date currTime = new Date();
        if (accelerate_enable) {
            if (init_time == null) {
                try {
                    init_time = sdfMap.get("normal").parse(init_timeString);
                } catch (ParseException e) {
                }
            }

            if (start_time == null) {
                start_time = currTime;
            }

            currTime = new Date(init_time.getTime() + (currTime.getTime() - start_time.getTime()) * accelerate_ratio);
        }
        Date today = new Date((currTime.getTime() + (1000L * 60 * 60 * 8)) / (1000L * 60 * 60 * 24) * (1000L * 60 * 60 * 24) - (1000L * 60 * 60 * 8));
        {
            calendar.setTime(today);
            timeMap.put("curr_day", calendar.getTime());
            calendar.add(Calendar.DAY_OF_MONTH, -1);
            timeMap.put("last_day", calendar.getTime());
            calendar.add(Calendar.DAY_OF_MONTH, +1);
            calendar.add(Calendar.DAY_OF_MONTH, +1);
            timeMap.put("next_day", calendar.getTime());
        }
        {
            calendar.setTime(today);
            calendar.set(Calendar.DAY_OF_WEEK, 1);
            timeMap.put("curr_week", calendar.getTime());
            calendar.add(Calendar.WEEK_OF_MONTH, -1);
            timeMap.put("last_week", calendar.getTime());
            calendar.add(Calendar.WEEK_OF_MONTH, +1);
            calendar.add(Calendar.WEEK_OF_MONTH, +1);
            timeMap.put("next_week", calendar.getTime());
        }
        {
            calendar.setTime(today);
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            timeMap.put("curr_month", calendar.getTime());
            calendar.add(Calendar.MONTH, -1);
            timeMap.put("last_month", calendar.getTime());
            calendar.add(Calendar.MONTH, +1);
            calendar.add(Calendar.MONTH, +1);
            timeMap.put("next_month", calendar.getTime());
        }
        {
            calendar.setTime(today);
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            calendar.set(Calendar.MONTH, 0);
            timeMap.put("curr_year", calendar.getTime());
            calendar.add(Calendar.YEAR, -1);
            timeMap.put("last_year", calendar.getTime());
            calendar.add(Calendar.YEAR, +1);
            calendar.add(Calendar.YEAR, +1);
            timeMap.put("next_year", calendar.getTime());
        }

        for (String key : timeMap.keySet()) {
            for (String sdfKey : sdfMap.keySet()) {
                SimpleDateFormat sdf = sdfMap.get(sdfKey);
                SceneDataValue SceneDataValue = new SceneDataValue(null, null, null, null);
                SceneDataValue.value_prim = new SceneDataPrimitive();
                SceneDataValue.value_prim.change = true;
                SceneDataValue.value_prim.value = sdf.format(timeMap.get(key));
                base_value.put(key + "_" + sdfKey, SceneDataValue);
            }
        }
        return base_value;
    }

    public void copyFromRepositoryContainer() {
        this.alarmConfigArray = RepositoryContainer.instance.alarmConfigArray;
        this.alarmConfigTrigger = RepositoryContainer.instance.alarmConfigTrigger;

        this.objTypeMap = RepositoryContainer.instance.objTypeMap;
        this.classCode2NameMap = RepositoryContainer.instance.classCode2NameMap;
        this.code2objTypeMap = RepositoryContainer.instance.code2objTypeMap;
        this.classArray = RepositoryContainer.instance.classArray;
        this.infoArrayDic = RepositoryContainer.instance.infoArrayDic;
        this.infoArrayJson = RepositoryContainer.instance.infoArrayJson;
        this.infoDataSource = RepositoryContainer.instance.infoDataSource;
        this.objectArrayDic = RepositoryContainer.instance.objectArrayDic;
        this.objectArrayAll = RepositoryContainer.instance.objectArrayAll;
        this.object2info2point = RepositoryContainer.instance.object2info2point;
        this.point2ObjectInfoList = RepositoryContainer.instance.point2ObjectInfoList;
        this.set2ObjectInfoList = RepositoryContainer.instance.set2ObjectInfoList;
        this.objType2id2Value = RepositoryContainer.instance.objType2id2Value;
        this.relationArrayDic = RepositoryContainer.instance.relationArrayDic;
        this.graphCodeDic = RepositoryContainer.instance.graphCodeDic;
        this.relCodeDic = RepositoryContainer.instance.relCodeDic;
        this.relationAll = RepositoryContainer.instance.relationAll;
        this.id2object = RepositoryContainer.instance.id2object;
        this.id2sdv = RepositoryContainer.instance.id2sdv;

        this.ZKTSceneArray = RepositoryContainer.instance.ZKTSceneArray;
        this.ZKTClassArray = RepositoryContainer.instance.ZKTClassArray;
        this.ZKTObjectArrayDic = RepositoryContainer.instance.ZKTObjectArrayDic;
        this.ZKTAlarmTypeArray = RepositoryContainer.instance.ZKTAlarmTypeArray;
        this.subsystem_connect_status = RepositoryContainer.instance.subsystem_connect_status;

        this.IBMSGroupArray = RepositoryContainer.instance.IBMSGroupArray;
        this.IBMSArrayDic = RepositoryContainer.instance.IBMSArrayDic;

        this.InfoPointListArray = RepositoryContainer.instance.InfoPointListArray;
        this.InfoPointRelationArray = RepositoryContainer.instance.InfoPointRelationArray;
        this.scaleplate = RepositoryContainer.instance.scaleplate;
    }

    //querySql，Source内容解析，Target\": {\r\n    \"Source\": \"rwd\",\r\n    \"rwd\": \"info\",\r\n    \"objType\": \"equipment\",\r\n    \"classCode\": \"ACATFU
    public SceneDataSet ParseSource(JSONObject descSet, String Source) {
        SceneDataSet result = null;
        if (Source.equals("class")) {//类型定义数据
            result = this.classArray;
        } else if (Source.equals("rwd")) {
            String rwd = (descSet.get("rwd")).toString();
            if (rwd.equals("object")) {//对象静态数据
                if (descSet.containsKey("objType")) {//大对象类型
                    String objType = (descSet.get("objType")).toString();
                    if (objType.equals("equipment") || objType.equals("system") || objType.equals("space")) {//三种大类型
                        if (descSet.containsKey("classCode")) {//类型code
                            String classCode = (descSet.get("classCode")).toString();
                            if (this.objectArrayDic.get(classCode) != null) {
                                result = this.objectArrayDic.get(classCode).value_array;
                            }
                        } else {
                            result = this.objectArrayDic.get(objType).value_array;
                        }
                    } else {
                        result = this.objectArrayDic.get(objType).value_array;
                    }
                } else if (descSet.containsKey("classCode")) {
                    String classCode = (descSet.get("classCode")).toString();
                    result = this.objectArrayDic.get(classCode).value_array;
                } else {
                    result = this.objectArrayAll;
                }
            } else if (rwd.equals("info")) {//类型点位数据
                String objType = (descSet.get("objType")).toString();
                if (objType.equals("equipment") || objType.equals("system") || objType.equals("space")) {
                    String classCode = (descSet.get("classCode")).toString();
                    result = this.infoArrayDic.get(classCode);
                } else {
                    result = this.infoArrayDic.get(objType);
                }
            } else if (rwd.equals("info_dataSource")) {//类型点位中dataSource数据
                result = this.infoDataSource;
            } else if (rwd.equals("relation")) {//关系数据
                if (descSet.get("graphCode") != null && descSet.get("relCode") != null) {
                    String graphCode = (descSet.get("graphCode")).toString();
                    String relCode = (descSet.get("relCode")).toString();
                    if (this.relationArrayDic.get(graphCode) != null) {
                        result = this.relationArrayDic.get(graphCode).get(relCode);
                    }
                } else if (descSet.get("graphCode") != null) {//图例
                    String graphCode = (descSet.get("graphCode")).toString();
                    result = this.graphCodeDic.get(graphCode);
                } else if (descSet.get("relCode") != null) {//关系类型
                    String relCode = (descSet.get("relCode")).toString();
                    result = this.relCodeDic.get(relCode);
                } else {
                    result = this.relationAll;
                }
            }
            // result = new SceneDataSet(false);
            // result.setRowChange(false);
        } else if (Source.equals("zkt-class")) {//zkt类型定义数据
            result = this.ZKTClassArray;
        } else if (Source.equals("zkt-object")) { //zkt下级类型数据
            String ibmsSceneCode = (descSet.get("ibmsSceneCode")).toString();
            String ibmsClassCode = (descSet.get("ibmsClassCode")).toString();
            if (this.ZKTObjectArrayDic.get(ibmsSceneCode).get(ibmsClassCode) != null) {
                result = this.ZKTObjectArrayDic.get(ibmsSceneCode).get(ibmsClassCode).value_array;
            }
        } else if (Source.equals("ibms")) {
            String product = (descSet.get("product")).toString();
            String type = (descSet.get("type")).toString();
            result = this.IBMSArrayDic.get(product).get(type);
        } else if (Source.equals("ibms-group")) {
            result = this.IBMSGroupArray;
        } else if (Source.equals("ibms-group-object")) {
            String ibmsSceneCode = (descSet.get("ibmsSceneCode")).toString();
            String ibmsClassCode = (descSet.get("ibmsClassCode")).toString();
            if (!this.IBMSArrayDic.containsKey(ibmsSceneCode)) {
                result = new SceneDataSet(false);
            } else {
                Map<String, SceneDataSet> arrayMap = this.IBMSArrayDic.get(ibmsSceneCode);
                if (!arrayMap.containsKey(ibmsClassCode)) {
                    result = new SceneDataSet(false);
                } else {
                    result = this.IBMSArrayDic.get(ibmsSceneCode).get(ibmsClassCode);
                }
            }
        } else if (Source.equals("alarmConfig")) {
            String alarmConfig = (descSet.get("alarmConfig")).toString();
            if (alarmConfig.equals("configTrigger")) {
                result = this.alarmConfigTrigger;
            } else {
                result = this.alarmConfigArray;
            }
        } else if (Source.equals("ibms-model")) {
            result = this.IBMSCalendarModel;
        } else if (Source.equals("ibms-calendar")) {
            result = this.IBMSCalendarBinding;
        } else if (Source.equals("ibms-alarm-type")) {
            result = this.ZKTAlarmTypeArray;
        } else if (Source.equals("weather")) {
            result = this.weather;
        } else if (Source.equals("scaleplate")) {
            result = this.scaleplate;
        } else if (Source.equals("alarm")) {
            result = this.alarmArray;
        } else if (Source.equals("info-point-list")) {
            result = this.InfoPointListArray;
        } else if (Source.equals("info-point-relation")) {
            result = this.InfoPointRelationArray;
        } else if (Source.equals("subsystem-connect-status")) {
            result = this.subsystem_connect_status;
        }
        return result;
    }

    private void refresh_rwd2zkt() {
        for (SceneDataObject classItem : this.ZKTClassArray.set) {
            String ibmsSceneCode = (String) classItem.get("ibmsSceneCode").value_prim.value;
            String ibmsClassCode = (String) classItem.get("ibmsClassCode").value_prim.value;
            String flag = null;
            if (classItem.containsKey("flag")) {
                flag = (String) classItem.get("flag").value_prim.value;
            }
            if (flag != null && flag.equals("reference")) {
                continue;
            }
            SceneDataValue sdv = this.ZKTObjectArrayDic.get(ibmsSceneCode).get(ibmsClassCode);
            for (SceneDataObject obj : sdv.value_array.set) {
                if (obj.father != null) {
                    this.dependency.sdv2Children.putIfAbsent(obj.father, new CopyOnWriteArrayList<SceneDataObject>());
                    this.dependency.sdv2Children.get(obj.father).add(obj);
                }
            }
        }
    }

    private void refresh_iot2SetColumn() {
        for (String key : this.objectArrayDic.keySet()) {
            if (this.objTypeMap.containsKey(key)) {
                continue;
            }
            String classCode = key;
            SceneDataSet infoArray = this.infoArrayDic.get(classCode);
            SceneDataSet objectArray = this.objectArrayDic.get(key).value_array;
            for (int index_info = 0; index_info < infoArray.set.size(); index_info++) {
                SceneDataObject info = infoArray.set.get(index_info);
                String infoCode = (String) info.get("code").value_prim.value;
                if (RWDUtil.getInfoType(info) == 1) {
                    for (int index_object = 0; index_object < objectArray.set.size(); index_object++) {
                        SceneDataObject obj = objectArray.set.get(index_object);
                        String Key = infoCode;
                        SceneDataValue sdv = obj.get(Key);
                        if (sdv != null) {
                            this.dependency.add_sdv2SetColumn(sdv, objectArray, Key);
                        }
                    }
                } else if (RWDUtil.getInfoType(info) == 2) {
                    for (int index_object = 0; index_object < objectArray.set.size(); index_object++) {
                        SceneDataObject obj = objectArray.set.get(index_object);
                        String Key = infoCode;
                        SceneDataValue sdv = obj.get(Key);
                        if (sdv != null) {
                            this.dependency.add_sdv2SetColumn(sdv, objectArray, Key);
                        }
                    }
                }
            }
        }
    }

    private void refresh_alarm2SetColumn() {
        for (String classCode : this.objectArrayDic.keySet()) {
            if (!this.code2objTypeMap.containsKey(classCode)) {
                continue;
            }
            String objType = this.code2objTypeMap.get(classCode);
            if (!objType.equals("equipment") && !objType.equals("system") && !objType.equals("space")) {
                continue;
            }
            SceneDataSet objectArray = this.objectArrayDic.get(classCode).value_array;
            for (int i = 0; i < objectArray.set.size(); i++) {
                SceneDataObject objectItem = objectArray.set.get(i);
                SceneDataValue sdv = objectItem.get("报警数量");
                this.dependency.add_sdv2SetColumn(sdv, objectArray, "报警数量");
            }
        }
    }

    public void refresh_dependency() {
        this.dependency.clear();
        if (this.enable_factor) {
            // 构建zkt到rwd的依赖
            this.refresh_rwd2zkt();
            // 构建IOT到对象信息点的依赖
            this.refresh_iot2SetColumn();
            // 构建报警数量到对象信息点的依赖
            this.refresh_alarm2SetColumn();
        }
    }

    public void recompute() {
        {
            int[] counts = this.recompute_IOT();
            log.warn("********************************" + "\t" + "recompute_IOT item: " + counts[0] + " affect: " + counts[1]);
        }
        {
            int[] counts = this.recompute_Alarm();
            log.warn("********************************" + "\t" + "recompute_Alarm item: " + counts[0] + " affect: " + counts[1]);
        }
        // {
        // int[] counts = this.recompute_weather();
        // log.warn("********************************" + "\t" + "recompute_weather item: " + counts[0] + " affect: " + counts[1]);
        // }
        // if (Constant.scaleplate_enable) {
        // int[] counts = this.recompute_scaleplate();
        // log.warn("********************************" + "\t" + "recompute_scaleplate item: " + counts[0] + " affect: " + counts[1]);
        // }
    }

    // private int[] recompute_weather() {
    // int[] counts = new int[2];
    // int item_count = 0;
    // int affect_count = 0;
    // // 加入计算队列
    // if (this.enable_factor) {
    // item_count++;
    // affect_count += this.addWaitCompute(this.weather);
    // }
    // counts[0] = item_count;
    // counts[1] = affect_count;
    // return counts;
    // }

    // private int[] recompute_scaleplate() {
    // int[] counts = new int[2];
    // int item_count = 0;
    // int affect_count = 0;
    // // 加入计算队列
    // if (this.enable_factor) {
    // item_count++;
    // affect_count += this.addWaitCompute(this.scaleplate);
    // }
    // counts[0] = item_count;
    // counts[1] = affect_count;
    // return counts;
    // }

    private int[] recompute_IOT() {
        int[] counts = new int[2];
        int item_count = 0;
        int affect_count = 0;
        // 加入计算队列
        if (this.enable_factor) {
            for (String point : this.point2sdv.keySet()) {
                SceneDataPrimitive sdv = this.point2sdv.get(point);
                if (sdv.value != null) {
                    item_count++;
                    affect_count += this.ProcessIOT(point);
                }
            }
            for (String point : this.set2sdv.keySet()) {
                SceneDataPrimitive sdv = this.set2sdv.get(point);
                if (sdv.value != null) {
                    item_count++;
                    affect_count += this.ProcessIOT(point);
                }
            }
        }
        counts[0] = item_count;
        counts[1] = affect_count;
        return counts;
    }

    public int[] recompute_Alarm() {
        int[] counts = new int[2];
        int item_count = 0;
        int affect_count = 0;
        // 加入计算队列
        if (this.enable_factor) {
            item_count++;
            affect_count += this.addWaitCompute(this.alarmArray);
            for (String objId : this.id2alarmList.keySet()) {
                SceneDataValue alarmList = this.id2alarmList.get(objId);
                item_count++;
                affect_count += this.addWaitCompute(alarmList);
            }
            for (String objId : this.id2alarmCount.keySet()) {
                SceneDataValue alarmCount = this.id2alarmCount.get(objId);
                item_count++;
                affect_count += this.addWaitCompute(alarmCount);
            }
        }
        counts[0] = item_count;
        counts[1] = affect_count;
        return counts;
    }

    public int ProcessIOT(String point) {
        int add_count = 0;
        if (this.enable_factor) {
            if (this.point2ObjectInfoList.containsKey(point)) {
                List<ObjectInfo> ObjectInfoList = this.point2ObjectInfoList.get(point);
                for (ObjectInfo ObjectInfo : ObjectInfoList) {
                    SceneDataValue sdv = ObjectInfo.obj.get(ObjectInfo.infoCode);
                    // this.ComputeOccur(sdv);
                    add_count += this.addWaitCompute(sdv);
                }
            }
            if (this.set2ObjectInfoList.containsKey(point)) {
                List<ObjectInfo> ObjectInfoList = this.set2ObjectInfoList.get(point);
                for (ObjectInfo ObjectInfo : ObjectInfoList) {
                    SceneDataValue sdv = ObjectInfo.obj.get(ObjectInfo.infoCode);
                    // this.ComputeOccur(sdv);
                    add_count += this.addWaitCompute(sdv);
                }
            }
        }
        return add_count;
    }

    @Override
    public void ComputeOccur(SceneDataValue sdv) {
        WebSocketUtil.ProcessComputeOccur(sdv);
    }

    @Override
    public void log_step_count(int step_count) {
        this.RepositoryProject.LogOfRun.step_count = step_count;
    }

    @Override
    public void log_step_begin(int step, int property_count, int value_count) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        JSONObject stepJSON = new JSONObject();
        stepJSON.put("property_count", property_count);
        stepJSON.put("value_count", value_count);
        stepJSON.put("beginTime", sdf.format(new Date()));
        this.RepositoryProject.LogOfRun.stepList.add(stepJSON);
    }

    @Override
    public void log_step_end(int step, int finish_count) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        JSONObject stepJSON = this.RepositoryProject.LogOfRun.stepList.get(this.RepositoryProject.LogOfRun.stepList.size() - 1);
        stepJSON.put("finish_count", finish_count);
        stepJSON.put("endTime", sdf.format(new Date()));
    }

    @Override
    public void log_error(String path, String message) {
        this.RepositoryProject.LogOfRun.error(path, message);
    }
}
