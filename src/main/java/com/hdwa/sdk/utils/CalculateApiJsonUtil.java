package com.hdwa.sdk.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hdwa.sdk.constant.BaseDecConstant;
import com.hdwa.sdk.entity.exception.ExceptionItem;
import com.hdwa.sdk.entity.repository.RepositoryBase;
import com.hdwa.sdk.entity.scene.*;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author abao
 * @since 2023/8/1
 * 计算接口对象和属性
 */
@Slf4j
public class CalculateApiJsonUtil {

    /**
     * 计算属性
     *
     * @param repositoryBase
     * @return
     * @throws Exception
     */
    public static List<List<SceneProperty>> calculateProperty(RepositoryBase repositoryBase) throws Exception {
        // 排序
        List<SceneProperty> properties = BaseApiUtil.getPropertyListBy(repositoryBase.sceneObject);
        // 声明一个异常集合
        List<ExceptionItem> exceptionList = new CopyOnWriteArrayList<>();

        //检查sql
        properties.stream()
                .filter(property -> property.getPropertyValueType().equals(BaseDecConstant.QUERY))
                .forEach(property -> {
                    //这里如果有异常就代表json格式错误
                    JSONObject sqlJson = null;
                    try {
                        sqlJson = JSON.parseObject(property.query_sql);
                    } catch (Exception e) {
                        ExceptionItem exceptionItem = null;
                        try {
                            exceptionItem = new ExceptionItem(PathUtil.getPropertyPath(repositoryBase, property), "Json格式错误", property.query_sql);
                        } catch (Exception e2) {
                            log.error("检查：" + BaseDecConstant.QUERY + "---出现异常");
                        }
                        exceptionList.add(exceptionItem);
                    }

                    //检查表达式格式
                    if (sqlJson != null) {
                        String queryType = (String) sqlJson.get(BaseDecConstant.QUERY_TYPE);
                        if (queryType != null) {
                            if (queryType.equals(BaseDecConstant.EXPRESSION)) {
                                String expression = (String) sqlJson.get(BaseDecConstant.EXPRESSION);
                                JSONObject criteriaObject = (JSONObject) sqlJson.get(BaseDecConstant.CRITERIA);
                                try {
                                    List<ExceptionItem> exceptionListInner = ExpressionUtil.buildAndPut(repositoryBase, property, expression, criteriaObject);
                                    exceptionList.addAll(exceptionListInner);
                                } catch (Exception e) {
                                    ExceptionItem exceptionItem = null;
                                    try {
                                        exceptionItem = new ExceptionItem(PathUtil.getPropertyPath(repositoryBase, property), "表达式格式错误", expression);
                                    } catch (Exception e2) {
                                        log.error("检查：" + BaseDecConstant.EXPRESSION + "---出现异常");
                                    }
                                    exceptionList.add(exceptionItem);
                                }
                            }
                        }
                    }
                });

        //检查deamon
        properties.stream()
                .filter(property -> property.getPropertyValueType().equals(BaseDecConstant.DEAMON))
                .forEach(property -> {
                    //转换失败代表格式错误
                    try {
                        JSON.parseObject(property.query_sql);
                    } catch (Exception e) {
                        ExceptionItem exceptionItem = null;
                        try {
                            exceptionItem = new ExceptionItem(PathUtil.getPropertyPath(repositoryBase, property), "Json格式错误", property.query_sql);
                        } catch (Exception e2) {
                            log.error("检查：" + BaseDecConstant.DEAMON + "---出现异常");
                        }
                        exceptionList.add(exceptionItem);
                    }
                });


        //检查static
     /*   properties.stream()
                .filter(property -> property.getPropertyValueType().equals(BaseDecConstant.STATIC))
                .filter(property -> !property.propertyValueSchema.equals(BaseDecConstant.JSONARRAY) && !property.propertyValueSchema.equals(BaseDecConstant.JSONOBJECT))
                .filter(property -> property.static_value == null || property.static_value.length() == 0)
                .forEach(property -> {
                    ExceptionItem exceptionItem = null;
                    try {
                        exceptionItem = new ExceptionItem(PathUtil.getPropertyPath(repositoryBase, property), "静态属性错误", property.static_value);
                    } catch (Exception e) {
                        log.error("检查：" + BaseDecConstant.STATIC + "---出现异常");
                    }
                    exceptionList.add(exceptionItem);
                });*/


        //有异常
        if (exceptionList.size() > 0) {
            throw new ExceptionWrapper(exceptionList);
        }

        //所有属性
        properties.forEach(property -> {
            try {
                //全部路径
                // TODO: 2023/8/1 优化
                //log.warn(PathUtil.getPropertyPath(repositoryBase, property));
                // TODO: 2023/8/1 优化
                List<SceneProperty> beforeList = CheckUtil.getPropertyBefore(repositoryBase, property);
                repositoryBase.beforeDic.put(property, beforeList);

                // TODO: 2023/8/1 打印
               /* for (SceneProperty temp : beforeList) {
                    log.warn(PathUtil.getPropertyPath(repositoryBase, temp));
                }*/

            } catch (ExceptionItem e) {
                exceptionList.add(e);
            } catch (ExceptionWrapper e) {
                exceptionList.addAll(e.itemList);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                try {
                    exceptionList.add(new ExceptionItem(PathUtil.getPropertyPath(repositoryBase, property), e.getMessage(), null));
                } catch (ExceptionItem ex) {
                    ex.printStackTrace();
                }
            }
        });

        //有异常
        if (exceptionList.size() > 0) {
            throw new ExceptionWrapper(exceptionList);
        }

        Map<SceneProperty, Boolean> processedDic = new ConcurrentHashMap<>(16);
        List<List<SceneProperty>> propertyList = new CopyOnWriteArrayList<>();

        while (true) {
            int count = 0;
            List<SceneProperty> spInnerList = new CopyOnWriteArrayList<>();
            for (SceneProperty spInner : properties) {
                if (processedDic.containsKey(spInner)) {
                    continue;
                }
                List<SceneProperty> beforeList = repositoryBase.beforeDic.get(spInner);
                boolean allFinish = true;
                for (SceneProperty property : beforeList) {
                    if (!processedDic.containsKey(property)) {
                        allFinish = false;
                        break;
                    }
                }
                if (allFinish) {
                    count++;
                    spInnerList.add(spInner);
                }
            }
            if (count == 0) {
                break;
            }
            propertyList.add(spInnerList);
            for (SceneProperty spInner : spInnerList) {
                processedDic.put(spInner, true);
            }
        }

        for (List<SceneProperty> spInnerList : propertyList) {
            for (SceneProperty spInner2 : spInnerList) {
                // TODO: 2023/8/1 打印
                //log.warn(PathUtil.getPropertyPath(repositoryBase, spInner2));
                if (!repositoryBase.property2SDV.containsKey(spInner2)) {
                    repositoryBase.property2SDV.put(spInner2, new CopyOnWriteArrayList<>());
                }
            }
        }

        return propertyList;
    }


    /**
     * 无检查计算属性
     *
     * @param repositoryBase
     * @return
     * @throws Exception
     */
    public static List<List<SceneProperty>> notCheckCalculateProperty(RepositoryBase repositoryBase) {
        // 排序
        List<SceneProperty> properties = BaseApiUtil.getPropertyListBy(repositoryBase.sceneObject);

        //所有属性
        properties.forEach(property -> {
            try {
                List<SceneProperty> beforeList = CheckUtil.getPropertyBefore(repositoryBase, property);
                repositoryBase.beforeDic.put(property, beforeList);
            } catch (Exception e) {
                log.error("无检查计算属性异常", e);
            }
        });

        Map<SceneProperty, Boolean> processedDic = new ConcurrentHashMap<>(16);
        List<List<SceneProperty>> propertyList = new CopyOnWriteArrayList<>();

        while (true) {
            int count = 0;
            List<SceneProperty> spInnerList = new CopyOnWriteArrayList<>();
            for (SceneProperty spInner : properties) {
                if (processedDic.containsKey(spInner)) {
                    continue;
                }
                List<SceneProperty> beforeList = repositoryBase.beforeDic.get(spInner);
                boolean allFinish = true;
                for (SceneProperty property : beforeList) {
                    if (!processedDic.containsKey(property)) {
                        allFinish = false;
                        break;
                    }
                }
                if (allFinish) {
                    count++;
                    spInnerList.add(spInner);
                }
            }
            if (count == 0) {
                break;
            }
            propertyList.add(spInnerList);
            for (SceneProperty spInner : spInnerList) {
                processedDic.put(spInner, true);
            }
        }

        for (List<SceneProperty> spInnerList : propertyList) {
            for (SceneProperty spInner2 : spInnerList) {
                if (!repositoryBase.property2SDV.containsKey(spInner2)) {
                    repositoryBase.property2SDV.put(spInner2, new CopyOnWriteArrayList<>());
                }
            }
        }
        return propertyList;
    }


    /**
     * 计算全部
     *
     * @param repositoryBase
     * @param propertyList
     * @throws Exception
     */
    public static void calculateAll(RepositoryBase repositoryBase, List<List<SceneProperty>> propertyList) throws Exception {
        repositoryBase.objectData = new SceneDataObject(repositoryBase, null, null, null, repositoryBase.sceneObject, null, null);

        for (List<SceneProperty> spInnerList : propertyList) {
            for (SceneProperty spInner2 : spInnerList) {
                List<SceneDataValue> sdvList = repositoryBase.property2SDV.get(spInner2);
                // 打印路径
                 /*   String path = PathUtil.getPropertyPath(repositoryBase, spInner2);
                    log.info("ComputeOnce:" + path);*/
                for (SceneDataValue sdv : sdvList) {
                    try {
                        calculateProperty(repositoryBase, sdv);
                    } catch (Exception e) {
                        String pathInner = PathUtil.getPropertyPath(repositoryBase, sdv.rel_property);
                        log.error(pathInner + " " + e.getMessage(), e);
                        log.error("计算接口出现异常", e);
                    }
                }
            }
        }

    }

    /**
     * 计算属性
     *
     * @param repositoryBase
     * @param sv
     * @throws Exception
     */
    public static void calculateProperty(RepositoryBase repositoryBase, SceneDataValue sv) throws Exception {
        SceneDataObject objectData = sv.parentObjectData;
        SceneProperty sceneProperty = sv.rel_property;
        switch (sceneProperty.propertyValueType) {
            case BaseDecConstant.STATIC:
                if (sv.rel_property.propertyValueSchema.equals(BaseDecConstant.JSONOBJECT)) {
                    sv.finish = true;
                } else if (sv.rel_property.propertyValueSchema.equals(BaseDecConstant.JSONARRAY)) {
                    boolean finish = true;
                    for (SceneDataObject sdbInner : sv.value_array.set) {
                        if (sdbInner != null) {
                            for (String temp : sdbInner.keySet()) {
                                SceneDataValue sdv = sdbInner.get(temp);
                                if (!sdv.finish) {
                                    finish = false;
                                    break;
                                }
                            }
                            if (!finish) {
                                break;
                            }
                        }
                    }
                    if (finish) {
                        sv.finish = true;
                    }
                } else {
                    sv.value_prim = new SceneDataPrimitive();
                    sv.value_prim.value = QueryUtil.parse_static(sceneProperty.propertyValueSchema, sceneProperty.static_value);
                    sv.value_prim.change = false;
                    sv.finish = true;
                }
                break;
            case BaseDecConstant.QUERY:
                sv.lock.lock();
                try {
                    calculatePropertyQuery(repositoryBase, sceneProperty, sv);
                } finally {
                    sv.lock.unlock();
                }
                break;
            case BaseDecConstant.CUSTOM:
                if (sv.value_object == null) {
                    sv.value_object = new SceneDataObject(repositoryBase, objectData, sceneProperty.propertyName, null, sceneProperty.custom_object, null, null);
                }
                break;
            case BaseDecConstant.DEAMON:
                JSONObject sqlJson = (JSONObject) JSON.parse(sceneProperty.query_sql);
                String queryType = (String) sqlJson.get(BaseDecConstant.QUERY_TYPE);
                if (queryType.endsWith(BaseDecConstant.TREND)) {
                    if (sv.value_prim == null) {
                        sv.value_prim = new SceneDataPrimitive();
                        sv.value_prim.value = 0;
                        sv.value_prim.change = true;
                    }
                } else if (queryType.endsWith(BaseDecConstant.CURVE)) {
                    if (sv.value_array == null) {
                        sv.value_array = new SceneDataSet(false);
                        sv.value_array.setRowChange(true);
                    }
                }
                JSONObject criteria = (JSONObject) sqlJson.get(BaseDecConstant.CRITERIA);
                List<String> pointList = new CopyOnWriteArrayList<>();
                for (String key : criteria.keySet()) {
                    JSONObject criteriaItemValue = (JSONObject) criteria.get(key);
                    String refString = (String) criteriaItemValue.get(BaseDecConstant.REF);
                    SceneDataSet sdvList = QueryUtil.parseSetRef(repositoryBase, sv, refString, new QueryAssist(), false, true);
                    for (SceneDataValue sdvInner : sdvList.singleValueSet) {
                        ConcurrentHashMap<SceneDataPrimitive, String> sdv2point = repositoryBase.sdv2point();
                        if (sdvInner != null && sdv2point.containsKey(sdvInner.value_prim)) {
                            String point = sdv2point.get(sdvInner.value_prim);
                            pointList.add(point);
                        }
                    }
                }
                break;

            default:
        }
        sv.last_compute_time = new Date();
    }

    /**
     * 计算属性查询
     *
     * @param repositoryBase
     * @param sceneProperty
     * @param sv
     * @return
     * @throws Exception
     */
    private static boolean calculatePropertyQuery(RepositoryBase repositoryBase, SceneProperty sceneProperty, SceneDataValue sv) throws Exception {
        SceneDataObject objectData = sv.parentObjectData;
        boolean computeValueChanged = false;
        Object valueBeforeCompute = null;
        if (repositoryBase.enable_factor) {
            valueBeforeCompute = sv.toJSON(true, 1);
        }
        JSONObject sqlJson = (JSONObject) JSON.parse(sceneProperty.query_sql);
        QueryAssist queryAssist = new QueryAssist(true);
        Object queryResult = QueryUtil.query(repositoryBase, sv, sqlJson, queryAssist);

            sv.rowFactor = queryAssist.rowFactor;
            sv.colFactorMap = queryAssist.colFactorMap;
            repositoryBase.dependency.add_compute(sv);

        if (sceneProperty.propertyValueSchema.equals(BaseDecConstant.JSONOBJECT)) {
            SceneDataObject queryResultObject = null;
            if (queryResult instanceof SceneDataObject) {
                queryResultObject = (SceneDataObject) queryResult;
            } else if (queryResult instanceof SceneDataSet) {
                SceneDataSet queryResultArray = (SceneDataSet) queryResult;
                if (queryResultArray.set.size() == 1) {
                    queryResultObject = queryResultArray.set.get(0);
                }
            }
            if (queryResultObject != null) {
                SceneDataObject arrayItemTmp = queryResultObject;
                Map<String, Boolean> fatherReturnColumnMap = new ConcurrentHashMap<>(16);
                while (true) {
                    if (arrayItemTmp.parentArrayData != null || arrayItemTmp.parentObjectData != null) {
                        break;
                    }
                    if (arrayItemTmp.father == null) {
                        break;
                    }

                    if (arrayItemTmp.fatherReturnColumnMap != null) {
                        for (String frc : arrayItemTmp.fatherReturnColumnMap.keySet()) {
                            fatherReturnColumnMap.put(frc, true);
                        }
                    }
                    arrayItemTmp = arrayItemTmp.father;
                }
                SceneDataObject sod = new SceneDataObject(repositoryBase, objectData, sceneProperty.propertyName, null, null, sceneProperty.query_attached, arrayItemTmp);
                if (fatherReturnColumnMap.size() > 0) {
                    sod.fatherReturnColumnMap = fatherReturnColumnMap;
                }
                repositoryBase.dependency.sdv2Children.putIfAbsent(arrayItemTmp, new CopyOnWriteArrayList<>());
                repositoryBase.dependency.sdv2Children.get(arrayItemTmp).add(sod);
                sv.value_object = sod;
            } else {
                sv.value_object = null;
            }
            if (sv.value_object != null) {
                if (queryResult instanceof SceneDataObject) {
                    sv.value_object.setRowChange(queryResultObject.getRowChange());
                    sv.value_object.setColChange(queryResultObject.getColChange());
                } else {
                    SceneDataSet queryResultArray = (SceneDataSet) queryResult;
                    sv.value_object.setRowChange(queryResultArray.getRowChange());
                    sv.value_object.setColChange(queryResultArray.getColChange());
                }
            }
        } else if (sceneProperty.propertyValueSchema.equals(BaseDecConstant.JSONARRAY)) {
            SceneDataSet array = (SceneDataSet) queryResult;
            if (array != null) {
                if (array.isSingleValueSet) {
                    if (sv.value_array == null) {
                        sv.value_array = new SceneDataSet(true);
                    }
                    sv.value_array.singleValueSet.clear();
                    for (int i = 0; i < array.singleValueSet.size(); i++) {
                        SceneDataValue arrayItem = array.singleValueSet.get(i);
                        SceneDataValue sod = new SceneDataValue(repositoryBase, null, null, null);
                        sod.value_prim = new SceneDataPrimitive();
                        if (arrayItem == null || arrayItem.value_prim == null) {
                            sod.value_prim.value = null;
                        } else {
                            sod.value_prim.value = arrayItem.value_prim.value;
                        }
                        sv.value_array.singleValueSet.add(sod);
                    }
                } else {
                    if (sv.value_array == null) {
                        sv.value_array = new SceneDataSet(false);
                    }
                    if (sv.value_array.set != null) {
                        sv.value_array.set.clear();
                    }
                    for (int i = 0; i < array.set.size(); i++) {
                        SceneDataObject arrayItemTmp = array.set.get(i);
                        Map<String, Boolean> fatherReturnColumnMap = new ConcurrentHashMap<String, Boolean>();
                        while (true) {
                            if (arrayItemTmp.parentArrayData != null || arrayItemTmp.parentObjectData != null) {
                                break;
                            }
                            if (arrayItemTmp.father == null) {
                                break;
                            }

                            if (arrayItemTmp.fatherReturnColumnMap != null) {
                                for (String frc : arrayItemTmp.fatherReturnColumnMap.keySet()) {
                                    fatherReturnColumnMap.put(frc, true);
                                }
                            }
                            arrayItemTmp = arrayItemTmp.father;
                        }
                        SceneDataObject sod = new SceneDataObject(repositoryBase, null, null, sv, null, sceneProperty.query_attached, arrayItemTmp);
                        if (fatherReturnColumnMap.size() > 0) {
                            sod.fatherReturnColumnMap = fatherReturnColumnMap;
                        }
                        repositoryBase.dependency.sdv2Children.putIfAbsent(arrayItemTmp, new CopyOnWriteArrayList<SceneDataObject>());
                        repositoryBase.dependency.sdv2Children.get(arrayItemTmp).add(sod);
                        boolean existKey = false;
                        for (String keyName : AttributeFilteringUtil.keyProperty) {
                            if (sod.containsKey(keyName)) {
                                existKey = true;
                                break;
                            }
                        }
                        if (!existKey) {
                            String keyDefault = AttributeFilteringUtil.keyDefault();
                            SceneDataValue keySsv = new SceneDataValue(repositoryBase, sod, keyDefault, null);
                            keySsv.value_prim = new SceneDataPrimitive();
                            keySsv.value_prim.value = UUID.randomUUID().toString().replaceAll("-", "");
                            keySsv.value_prim.change = false;
                            sod.put(keyDefault, keySsv);
                        }
                        sv.value_array.set.add(sod);
                    }
                }
            }
            if (sv.value_array != null) {
                if (array != null) {
                    sv.value_array.setRowChange(array.getRowChange());
                }
            }
            if (sv.value_array != null) {
                if (array != null) {
                    sv.value_array.setColChange(array.getColChange());
                }
            }
            if (sv.value_array != null) {
                sv.value_array.path = BaseDecConstant.REF + ":" + PathUtil.getDataPath(sv);
            }
        } else {
            if (sv.value_prim == null) {
                sv.value_prim = new SceneDataPrimitive();
            }
            if (queryResult instanceof SceneDataSet) {
                SceneDataSet sdvListInner = (SceneDataSet) queryResult;
                if (sdvListInner.singleValueSet.size() == 1) {
                    if (sdvListInner.singleValueSet.get(0) != null) {
                        sv.value_prim.value = sdvListInner.singleValueSet.get(0).value_prim.value;
                    }
                }
                sv.value_prim.change = sdvListInner.getRowChange();
            } else {
                SceneDataPrimitive sdp = (SceneDataPrimitive) queryResult;
                if (sdp != null) {
                    sv.value_prim.value = sdp.value;
                }
                if (sdp != null) {
                    sv.value_prim.change = sdp.change;
                }
            }
            if (sv.value_prim.change) {
                if (sv.parentObjectData.parentArrayData != null) {
                    if (!sv.parentObjectData.parentArrayData.value_array.getRowChange()) {
                        sv.parentObjectData.parentArrayData.value_array.setColChange(sv.myPropertyName);
                    }
                } else {
                    if (!sv.parentObjectData.getRowChange()) {
                        sv.parentObjectData.setColChange(sv.myPropertyName);
                    }
                }
            }
            if (sceneProperty.propertyValueSchema.equals(BaseDecConstant.INT)) {
                if (sv.value_prim.value != null) {
                    Object jt = sv.value_prim.value;
                    int jtValue;
                    if (jt instanceof Integer) {
                        jtValue = (Integer) jt;
                    } else if (jt instanceof Long) {
                        jtValue = ((Long) jt).intValue();
                    } else if (jt instanceof Float) {
                        jtValue = ((Float) jt).intValue();
                    } else {
                        jtValue = ((Double) jt).intValue();
                    }
                    sv.value_prim.value = jtValue;
                }
            } else if (sceneProperty.propertyValueSchema.equals(BaseDecConstant.DOUBLE)) {
                if (sv.value_prim.value != null) {
                    Object jt = sv.value_prim.value;
                    double jtValue;
                    if (jt instanceof Integer) {
                        jtValue = ((Integer) jt).doubleValue();
                    } else if (jt instanceof Long) {
                        jtValue = ((Long) jt).doubleValue();
                    } else if (jt instanceof Float) {
                        jtValue = ((Float) jt).doubleValue();
                    } else {
                        jtValue = (Double) jt;
                    }
                    sv.value_prim.value = jtValue;
                }
            }
        }
        sv.finish = true;
        Object valueAfterCompute = null;
        if (repositoryBase.enable_factor) {
            valueAfterCompute = sv.toJSON(true, 1);
            computeValueChanged = !FastJsonCompareUtil.Instance().CompareObject(valueBeforeCompute, valueAfterCompute, true);
        }
        return computeValueChanged;
    }

    /**
     * 获取数据
     *
     * @param repositoryBase
     * @param valuePath
     * @return
     */
    public static Object getValueObject(RepositoryBase repositoryBase, JSONArray valuePath) {
        int index = 0;
        Object tmpData = repositoryBase.objectData.get(valuePath.getString(index));
        index++;
        while (index < valuePath.size()) {
            if (tmpData instanceof SceneDataValue) {
                SceneDataValue currData = (SceneDataValue) tmpData;
                currData = currData.value_object.get(valuePath.getString(index));
                tmpData = currData;
                if (currData == null) {
                    continue;
                }
                if (currData.value_array != null) {
                    if (index < valuePath.size() - 1) {
                        index++;
                        SceneDataObject matchItem = null;
                        for (String keyName : AttributeFilteringUtil.keyProperty) {
                            for (SceneDataObject sdbInner : currData.value_array.set) {
                                if (sdbInner.containsKey(keyName) && sdbInner.get(keyName).value_prim.value.equals(valuePath.getString(index))) {
                                    matchItem = sdbInner;
                                    break;
                                }
                            }
                            if (matchItem != null) {
                                break;
                            }
                        }
                        tmpData = matchItem;
                    }
                }
            } else if (tmpData != null) {
                SceneDataObject sdo = (SceneDataObject) tmpData;
                SceneDataValue currData = sdo.get(valuePath.getString(index));
                tmpData = currData;
                if (currData.value_array != null) {
                    if (index < valuePath.size() - 1) {
                        index++;
                        SceneDataObject matchItem = null;
                        for (String keyName : AttributeFilteringUtil.keyProperty) {
                            for (SceneDataObject sdbInner : currData.value_array.set) {
                                if (sdbInner.containsKey(keyName) && sdbInner.get(keyName).value_prim.value.equals(valuePath.getString(index))) {
                                    matchItem = sdbInner;
                                    break;
                                }
                            }
                            if (matchItem != null) {
                                break;
                            }
                        }
                        tmpData = matchItem;
                    }
                }
            } else {
                break;
            }

            index++;
        }
        return tmpData;
    }

    /**
     * 读取数据
     *
     * @param tmpData
     * @return
     */
    public static Object getValueJson(Object tmpData) {
        Object result;
        if (tmpData == null) {
            return null;
        }

        if (tmpData instanceof SceneDataValue) {
            SceneDataValue currData = (SceneDataValue) tmpData;
            int level = currData.rel_property == null ? 1 : Integer.parseInt(currData.rel_property.read_level);
            result = currData.toJSON(true, level == 0 ? -1 : level);
        } else {
            SceneDataObject currData = (SceneDataObject) tmpData;
            int level = (currData.parentArrayData != null && currData.parentArrayData.rel_property != null) ? Integer.parseInt(currData.parentArrayData.rel_property.read_level) : 1;
            result = currData.toJSON(level == 0 ? -1 : level);
        }

        return result;
    }

    /**
     * 读取数据
     *
     * @param tmpData
     * @return
     */
    public static Object getValueJson(Object tmpData, int level) {
        return getValueJson(tmpData, level, false);
    }

    /**
     * 读取数据
     *
     * @param tmpData
     * @return
     */
    public static Object getValueJson(Object tmpData, int level, boolean change) {
        Object result;
        if (tmpData instanceof SceneDataValue) {
            SceneDataValue currData = (SceneDataValue) tmpData;
            result = currData.toJSON(true, level == 0 ? -1 : level, change);
        } else {
            SceneDataObject currData = (SceneDataObject) tmpData;
            result = currData.toJSON(level == 0 ? -1 : level, change);
        }

        return result;
    }
}
