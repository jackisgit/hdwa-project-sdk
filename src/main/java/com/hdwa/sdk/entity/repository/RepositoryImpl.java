package com.hdwa.sdk.entity.repository;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hdwa.sdk.constant.BaseDecConstant;
import com.hdwa.sdk.entity.scene.DataObject;
import com.hdwa.sdk.entity.scene.DataPrimitive;
import com.hdwa.sdk.entity.scene.DataSet;
import com.hdwa.sdk.entity.scene.DataValue;
import com.hdwa.sdk.utils.BaseApiUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 数据仓库
 * @author abao
 */
@Slf4j
public class RepositoryImpl extends RepositoryBase {

    /**
     * <p>物理世界</p>
     * <p>类型定义-全量数据</p>
     * <p>数据来源 physical_world/classArray.json</p>
     */
    public DataSet classArray = new DataSet(false);
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
    public Map<String, DataSet> infoArrayDic = new HashMap<>(16);
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
    public DataSet infoDataSource = new DataSet(false);
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
    public Map<String, DataObject> id2sdv = new HashMap<>(16);
    /**
     * <p>对象数据--全量数据</p>
     * <p>数据来源 physical_world/object/*.json</p>
     */
    public DataSet objectArrayAll = new DataSet(false);
    /**
     * <p>对象数据--全量数据</p>
     * <p>数据来源 physical_world/object/*.json</p>
     */
    public Map<String, DataValue> objectArrayDic = new HashMap<>(16);
    /**
     * <p>对象数据--objType-（id-对象数据）</p>
     * <p>objType--->（id-->sdo）</p>
     * <p>数据来源 physical_world/object/*.json</p>
     */
    public Map<String, Map<String, DataObject>> objType2id2Value = new HashMap<>(16);
    /**
     * <p>关系数据--graphCode-（relCode-关系数据）</p>
     * <p>graphCode 图例编码--->（relCode-->sds）</p>
     * <p>数据来源 physical_world/relation/*.json</p>
     */
    public Map<String, Map<String, DataSet>> relationArrayDic = new HashMap<>(16);
    /**
     * <p>关系数据--graphCode-关系数据</p>
     * <p>graphCode 图例编码--->sds</p>
     * <p>数据来源 physical_world/relation/*.json</p>
     */
    public Map<String, DataSet> graphCodeDic = new HashMap<>(16);
    /**
     * <p>关系数据--relCode-关系数据</p>
     * <p>relCode 关系编码--->sds</p>
     * <p>数据来源 physical_world/relation/*.json</p>
     */
    public Map<String, DataSet> relCodeDic = new HashMap<>(16);
    /**
     * <p>关系数据--全量数据</p>
     * <p>sds</p>
     * <p>数据来源 physical_world/relation/*.json</p>
     */
    public DataSet relationAll = new DataSet(false);
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
    public DataSet ZKTSceneArray = new DataSet(false);
    /**
     * <p>IBMS物理世界</p>
     * <p>场景编码--对象数据 (ibms类型编码->对象数据)</p>
     * <p>ibmsSceneCode--ibmsClassCode->对象数据 </p>
     * <p>数据来源 ibms_physical_world/sceneArray.json</p>
     */
    public Map<String, Map<String, DataValue>> ZKTObjectArrayDic = new HashMap<>(16);
    /**
     * <p>IBMS物理世界</p>
     * <p>类型定义数据</p>
     * <p>数据来源 ibms_physical_world/classArray.json</p>
     */
    public DataSet ZKTClassArray = new DataSet(false);
    /**
     * <p>IBMS逻辑编组</p>
     * <p>逻辑编组数据</p>
     * <p>数据来源 ibms_logical_group/ibmsLogicalGroup.json</p>
     */
    public DataSet IBMSGroupArray = new DataSet(false);
    /**
     * <p>IBMS逻辑编组</p>
     * <p>场景编码--分组数据 (ibms类型编码->分组数据)</p>
     * <p>ibmsSceneCode--ibmsClassCode->分组数据 </p>
     * <p>数据来源 ibms_logical_group/**.json</p>
     */
    public Map<String, Map<String, DataSet>> IBMSArrayDic = new HashMap<>(16);
    /**
     * <p>点位数据</p>
     * <p>点位数据</p>
     * <p>数据来源 point/point-list.json</p>
     */
    public DataSet InfoPointListArray = new DataSet(false, BaseDecConstant.INFO_POINT_LIST);
    /**
     * <p>点位数据</p>
     * <p>点位关系数据</p>
     * <p>数据来源 point/point-relation.json</p>
     */
    public DataSet InfoPointRelationArray = new DataSet(false, BaseDecConstant.INFO_POINT_RELATION);

    public Map<String, JSONObject> general_queryMap;

    public RepositoryImpl() {
        super();
    }

    /**
     * 解析数据
     *
     * @param descSet
     * @param source
     * @return
     */
    @Override
    public DataSet parseSource(JSONObject descSet, String source) {
        DataSet result = null;
        switch (source) {
            case BaseDecConstant.CLASS:
                result = classArray;
                break;
            case BaseDecConstant.RWD:
                String rwd = (descSet.get(BaseDecConstant.RWD)).toString();
                switch (rwd) {
                    case BaseDecConstant.OBJECT:
                        if (descSet.containsKey(BaseDecConstant.OBJ_TYPE)) {
                            String objType = (descSet.get(BaseDecConstant.OBJ_TYPE)).toString();
                            if (objType.equals(BaseDecConstant.EQUIPMENT) || objType.equals(BaseDecConstant.SYSTEM) || objType.equals(BaseDecConstant.SPACE)) {
                                if (descSet.containsKey(BaseDecConstant.CLASS_CODE)) {
                                    String classCode = (descSet.get(BaseDecConstant.CLASS_CODE)).toString();
                                    if (objectArrayDic.get(classCode) != null) {
                                        result = objectArrayDic.get(classCode).valueArray;
                                    }
                                } else {
                                    if (objectArrayDic.get(objType) != null) {
                                        result = objectArrayDic.get(objType).valueArray;
                                    } else {
                                        log.error("*****缺少objType数据：" + objType);
                                    }
                                }
                            } else {
                                if (objectArrayDic.get(objType) != null) {
                                    result = objectArrayDic.get(objType).valueArray;
                                } else {
                                    log.error("*****缺少objType数据：" + objType);
                                }
                            }
                        } else if (descSet.containsKey(BaseDecConstant.CLASS_CODE)) {
                            String classCode = (descSet.get(BaseDecConstant.CLASS_CODE)).toString();
                            if (objectArrayDic.get(classCode) != null) {
                                result = objectArrayDic.get(classCode).valueArray;
                            }
                        } else {
                            result = objectArrayAll;
                        }
                        break;
                    case BaseDecConstant.INFO:
                        String objType = (descSet.get(BaseDecConstant.OBJ_TYPE)).toString();
                        if (objType.equals(BaseDecConstant.EQUIPMENT) || objType.equals(BaseDecConstant.SYSTEM) || objType.equals(BaseDecConstant.SPACE)) {
                            String classCode = (descSet.get(BaseDecConstant.CLASS_CODE)).toString();
                            result = infoArrayDic.get(classCode);
                        } else {
                            result = infoArrayDic.get(objType);
                        }
                        break;
                    case BaseDecConstant.INFO_DATASOURCE:
                        result = infoDataSource;
                        break;
                    case BaseDecConstant.RELATION:
                        if (descSet.get(BaseDecConstant.GRAPH_CODE) != null && descSet.get(BaseDecConstant.REL_CODE) != null) {
                            String graphCode = (descSet.get(BaseDecConstant.GRAPH_CODE)).toString();
                            String relCode = (descSet.get(BaseDecConstant.REL_CODE)).toString();
                            if (relationArrayDic.get(graphCode) != null) {
                                result = relationArrayDic.get(graphCode).get(relCode);
                            }
                        }//图例
                        else if (descSet.get(BaseDecConstant.GRAPH_CODE) != null) {
                            String graphCode = (descSet.get(BaseDecConstant.GRAPH_CODE)).toString();
                            result = graphCodeDic.get(graphCode);
                        } //关系类型
                        else if (descSet.get(BaseDecConstant.REL_CODE) != null) {
                            String relCode = (descSet.get(BaseDecConstant.REL_CODE)).toString();
                            result = relCodeDic.get(relCode);
                        } else {
                            result = relationAll;
                        }
                        break;

                    default:
                }
                break;
            case BaseDecConstant.ZKT_CLASS:
                result = ZKTClassArray;
                break;
            case BaseDecConstant.ZKT_OBJECT: {
                String ibmsSceneCode = (descSet.get(BaseDecConstant.IBMS_SCENE_CODE)).toString();
                String ibmsClassCode = (descSet.get(BaseDecConstant.IBMS_CLASS_CODE)).toString();
                if (ZKTObjectArrayDic.get(ibmsSceneCode) != null && ZKTObjectArrayDic.get(ibmsSceneCode).get(ibmsClassCode) != null) {
                    result = ZKTObjectArrayDic.get(ibmsSceneCode).get(ibmsClassCode).valueArray;
                }
                break;
            }
            case BaseDecConstant.IBMS:
                String product = (descSet.get(BaseDecConstant.PRODUCT)).toString();
                String type = (descSet.get(BaseDecConstant.TYPE)).toString();
                if (IBMSArrayDic.get(product) != null) {
                    result = IBMSArrayDic.get(product).get(type);
                }
                break;
            case BaseDecConstant.IBMS_GROUP:
                result = IBMSGroupArray;
                break;
            case BaseDecConstant.IBMS_GROUP_OBJECT: {
                String ibmsSceneCode = (descSet.get(BaseDecConstant.IBMS_SCENE_CODE)).toString();
                String ibmsClassCode = (descSet.get(BaseDecConstant.IBMS_CLASS_CODE)).toString();
                if (!IBMSArrayDic.containsKey(ibmsSceneCode)) {
                    result = new DataSet(false);
                } else {
                    Map<String, DataSet> arrayMap = IBMSArrayDic.get(ibmsSceneCode);
                    if (!arrayMap.containsKey(ibmsClassCode)) {
                        result = new DataSet(false);
                    } else {
                        result = IBMSArrayDic.get(ibmsSceneCode).get(ibmsClassCode);
                    }
                }
                break;
            }
            case BaseDecConstant.ALARM:
                result = DataContainer.alarmArray;
                break;
            case BaseDecConstant.INFO_POINT_LIST:
                result = InfoPointListArray;
                break;
            case BaseDecConstant.INFO_POINT_RELATION:
                result = InfoPointRelationArray;
                break;
            default:
                break;
        }
        return result;
    }


    /**
     * 重新计算IOT数据
     *
     * @return
     */
    public int[] recomputeIot() {
        int[] counts = new int[2];
        int itemCount = 0;
        int affectCount = 0;
        for (String point : DataContainer.point2sdv.keySet()) {
            DataPrimitive sdv = DataContainer.point2sdv.get(point);
            if (sdv.value != null) {
                itemCount++;
                affectCount += processIot(point);
            }
        }
        for (String point : DataContainer.set2sdv.keySet()) {
            DataPrimitive sdv = DataContainer.set2sdv.get(point);
            if (sdv.value != null) {
                itemCount++;
                affectCount += processIot(point);
            }
        }
        counts[0] = itemCount;
        counts[1] = affectCount;
        return counts;
    }

    /**
     * 重新计算报警数据
     *
     * @return
     */
    public int[] recomputeAlarm() {
        int[] counts = new int[2];
        int itemCount = 0;
        int affectCount = 0;
        itemCount++;
        affectCount += addWaitCompute(DataContainer.alarmArray);
        for (String objId : DataContainer.id2alarmList.keySet()) {
            DataValue alarmList = DataContainer.id2alarmList.get(objId);
            itemCount++;
            affectCount += addWaitCompute(alarmList);
        }
        for (String objId : DataContainer.id2alarmCount.keySet()) {
            DataValue alarmCount = DataContainer.id2alarmCount.get(objId);
            itemCount++;
            affectCount += addWaitCompute(alarmCount);
        }
        counts[0] = itemCount;
        counts[1] = affectCount;
        return counts;
    }


    /**
     * 处理iot数据
     *
     * @param point
     * @return
     */
    public int processIot(String point) {
        int add_count = 0;
        if (point2ObjectInfoList.containsKey(point)) {
            List<ObjectInfo> ObjectInfoList = point2ObjectInfoList.get(point);
            for (ObjectInfo ObjectInfo : ObjectInfoList) {
                DataValue sdv = ObjectInfo.obj.get(ObjectInfo.infoCode);
                add_count += addWaitCompute(sdv);
            }
        }
        if (set2ObjectInfoList.containsKey(point)) {
            List<ObjectInfo> ObjectInfoList = set2ObjectInfoList.get(point);
            for (ObjectInfo ObjectInfo : ObjectInfoList) {
                DataValue sdv = ObjectInfo.obj.get(ObjectInfo.infoCode);
                add_count += addWaitCompute(sdv);
            }
        }
        return add_count;
    }

    /**
     * 构建依赖
     */
    public boolean refreshDependency() {
        try {
            dependency.clear();
            // 构建zkt到rwd的依赖
            refreshRwdToZkt();
            // 构建IOT到对象信息点的依赖
            refreshIotToSetColumn();
            // 构建报警数量到对象信息点的依赖
            refreshAlarmToSetColumn();
            return true;
        } catch (Exception e) {
            log.error("构建依赖出现异常", e);
            return false;
        }
    }


    private void refreshRwdToZkt() {
        for (DataObject classItem : ZKTClassArray.set) {
            String ibmsSceneCode = (String) classItem.get(BaseDecConstant.IBMS_SCENE_CODE).valuePrim.value;
            String ibmsClassCode = (String) classItem.get(BaseDecConstant.IBMS_CLASS_CODE).valuePrim.value;
            String flag = null;
            if (classItem.containsKey(BaseDecConstant.FLAG)) {
                flag = (String) classItem.get(BaseDecConstant.FLAG).valuePrim.value;
            }
            if (BaseDecConstant.REFERENCE.equals(flag)) {
                continue;
            }
            DataValue sdv = ZKTObjectArrayDic.get(ibmsSceneCode).get(ibmsClassCode);
            if (sdv != null) {
                for (DataObject obj : sdv.valueArray.set) {
                    if (obj.father != null) {
                        dependency.sdv2Children.putIfAbsent(obj.father, new CopyOnWriteArrayList<>());
                        dependency.sdv2Children.get(obj.father).add(obj);
                    }
                }
            }
        }
    }

    private void refreshIotToSetColumn() {
        for (String key : objectArrayDic.keySet()) {
            if (objTypeMap.containsKey(key)) {
                continue;
            }
            DataSet infoArray = infoArrayDic.get(key);
            DataSet objectArray = objectArrayDic.get(key).valueArray;
            for (int index_info = 0; index_info < infoArray.set.size(); index_info++) {
                DataObject info = infoArray.set.get(index_info);
                String infoCode = (String) info.get("code").valuePrim.value;
                if (BaseApiUtil.getInfoTypeByTag(info) == 1) {
                    for (int index_object = 0; index_object < objectArray.set.size(); index_object++) {
                        DataObject obj = objectArray.set.get(index_object);
                        DataValue sdv = obj.get(infoCode);
                        if (sdv != null) {
                            dependency.add_sdv2SetColumn(sdv, objectArray, infoCode);
                        }
                    }
                } else if (BaseApiUtil.getInfoTypeByTag(info) == 2) {
                    for (int index_object = 0; index_object < objectArray.set.size(); index_object++) {
                        DataObject obj = objectArray.set.get(index_object);
                        DataValue sdv = obj.get(infoCode);
                        if (sdv != null) {
                            dependency.add_sdv2SetColumn(sdv, objectArray, infoCode);
                        }
                    }
                }
            }
        }
    }

    private void refreshAlarmToSetColumn() {
        for (String classCode : objectArrayDic.keySet()) {
            if (!code2objTypeMap.containsKey(classCode)) {
                continue;
            }
            String objType = code2objTypeMap.get(classCode);
            if (!BaseDecConstant.EQUIPMENT.equals(objType) && !BaseDecConstant.SYSTEM.equals(objType) && !BaseDecConstant.SPACE.equals(objType)) {
                continue;
            }
            DataSet objectArray = objectArrayDic.get(classCode).valueArray;
            for (int i = 0; i < objectArray.set.size(); i++) {
                DataObject objectItem = objectArray.set.get(i);
                DataValue sdv = objectItem.get(BaseDecConstant.ALARM_COUNT);
                dependency.add_sdv2SetColumn(sdv, objectArray, BaseDecConstant.ALARM_COUNT);
            }
        }
    }
}
