package com.hdwa.sdk.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hdwa.sdk.constant.BaseDecConstant;
import com.hdwa.sdk.entity.exception.ExceptionItem;
import com.hdwa.sdk.entity.repository.RepositoryBase;
import com.hdwa.sdk.entity.scene.*;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
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
    public static List<List<DataProperty>> calculateProperty(RepositoryBase repositoryBase) throws Exception {
        // 排序
        List<DataProperty> properties = BaseApiUtil.getPropertyListBy(repositoryBase.dataObjectBase);
        // 声明一个异常集合
        List<ExceptionItem> exceptionList = new CopyOnWriteArrayList<>();

        //检查sql
        properties.stream()
                .filter(property -> property.getPropertyValueType().equals(BaseDecConstant.QUERY))
                .forEach(property -> {
                    //这里如果有异常就代表json格式错误
                    JSONObject sqlJson = null;
                    try {
                        sqlJson = JSON.parseObject(property.querySql);
                    } catch (Exception e) {
                        ExceptionItem exceptionItem = null;
                        try {
                            exceptionItem = new ExceptionItem(PathUtil.getPropertyPath(repositoryBase, property), "Json格式错误", property.querySql);
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
                        JSON.parseObject(property.querySql);
                    } catch (Exception e) {
                        ExceptionItem exceptionItem = null;
                        try {
                            exceptionItem = new ExceptionItem(PathUtil.getPropertyPath(repositoryBase, property), "Json格式错误", property.querySql);
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
                .filter(property -> property.staticValue == null || property.staticValue.length() == 0)
                .forEach(property -> {
                    ExceptionItem exceptionItem = null;
                    try {
                        exceptionItem = new ExceptionItem(PathUtil.getPropertyPath(repositoryBase, property), "静态属性错误", property.staticValue);
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
                List<DataProperty> beforeList = CheckUtil.getPropertyBefore(repositoryBase, property);
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

        Map<DataProperty, Boolean> processedDic = new HashMap<>(16);
        List<List<DataProperty>> propertyList = new CopyOnWriteArrayList<>();

        while (true) {
            int count = 0;
            List<DataProperty> spInnerList = new CopyOnWriteArrayList<>();
            for (DataProperty spInner : properties) {
                if (processedDic.containsKey(spInner)) {
                    continue;
                }
                List<DataProperty> beforeList = repositoryBase.beforeDic.get(spInner);
                boolean allFinish = true;
                for (DataProperty property : beforeList) {
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
            for (DataProperty spInner : spInnerList) {
                processedDic.put(spInner, true);
            }
        }

        for (List<DataProperty> spInnerList : propertyList) {
            for (DataProperty spInner2 : spInnerList) {
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
    public static List<List<DataProperty>> notCheckCalculateProperty(RepositoryBase repositoryBase) {
        // 排序
        List<DataProperty> properties = BaseApiUtil.getPropertyListBy(repositoryBase.dataObjectBase);

        //所有属性
        properties.forEach(property -> {
            try {
                List<DataProperty> beforeList = CheckUtil.getPropertyBefore(repositoryBase, property);
                repositoryBase.beforeDic.put(property, beforeList);
            } catch (Exception e) {
                log.error("无检查计算属性异常", e);
            }
        });

        Map<DataProperty, Boolean> processedDic = new HashMap<>(16);
        List<List<DataProperty>> propertyList = new CopyOnWriteArrayList<>();

        while (true) {
            int count = 0;
            List<DataProperty> spInnerList = new CopyOnWriteArrayList<>();
            for (DataProperty spInner : properties) {
                if (processedDic.containsKey(spInner)) {
                    continue;
                }
                List<DataProperty> beforeList = repositoryBase.beforeDic.get(spInner);
                boolean allFinish = true;
                for (DataProperty property : beforeList) {
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
            for (DataProperty spInner : spInnerList) {
                processedDic.put(spInner, true);
            }
        }

        for (List<DataProperty> spInnerList : propertyList) {
            for (DataProperty spInner2 : spInnerList) {
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
    public static void calculateAll(RepositoryBase repositoryBase, List<List<DataProperty>> propertyList) throws Exception {
        repositoryBase.objectData = new DataObject(repositoryBase, null, null, null, repositoryBase.dataObjectBase, null, null);

        for (List<DataProperty> spInnerList : propertyList) {
            for (DataProperty spInner2 : spInnerList) {
                List<DataValue> sdvList = repositoryBase.property2SDV.get(spInner2);
                // 打印路径
                 /*   String path = PathUtil.getPropertyPath(repositoryBase, spInner2);
                    log.info("ComputeOnce:" + path);*/
                for (DataValue sdv : sdvList) {
                    try {
                        calculateProperty(repositoryBase, sdv);
                    } catch (Exception e) {
                        String pathInner = PathUtil.getPropertyPath(repositoryBase, sdv.relProperty);
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
    public static boolean calculateProperty(RepositoryBase repositoryBase, DataValue sv) {
        boolean computeValueChanged = false;
        try {
            DataObject objectData = sv.parentObjectData;
            DataProperty dataProperty = sv.relProperty;
            switch (dataProperty.propertyValueType) {
                case BaseDecConstant.STATIC:
                    if (sv.relProperty.propertyValueSchema.equals(BaseDecConstant.JSONOBJECT)) {
                        sv.finish = true;
                    } else if (sv.relProperty.propertyValueSchema.equals(BaseDecConstant.JSONARRAY)) {
                        boolean finish = true;
                        for (DataObject sdbInner : sv.valueArray.set) {
                            if (sdbInner != null) {
                                for (String temp : sdbInner.keySet()) {
                                    DataValue sdv = sdbInner.get(temp);
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
                        sv.valuePrim = new DataPrimitive();
                        sv.valuePrim.value = QueryUtil.parse_static(dataProperty.propertyValueSchema, dataProperty.staticValue);
                        sv.valuePrim.change = false;
                        sv.finish = true;
                    }
                    break;
                case BaseDecConstant.QUERY:
                    sv.lock.lock();
                    try {
                        computeValueChanged = calculatePropertyQuery(repositoryBase, dataProperty, sv);
                    } finally {
                        sv.lock.unlock();
                    }
                    break;
                case BaseDecConstant.CUSTOM:
                    if (sv.valueObject == null) {
                        sv.valueObject = new DataObject(repositoryBase, objectData, dataProperty.propertyName, null, dataProperty.customObject, null, null);
                    }
                    break;
                case BaseDecConstant.DEAMON:
                    JSONObject sqlJson = (JSONObject) JSON.parse(dataProperty.querySql);
                    String queryType = (String) sqlJson.get(BaseDecConstant.QUERY_TYPE);
                    if (queryType.endsWith(BaseDecConstant.TREND)) {
                        if (sv.valuePrim == null) {
                            sv.valuePrim = new DataPrimitive();
                            sv.valuePrim.value = 0;
                            sv.valuePrim.change = true;
                        }
                    } else if (queryType.endsWith(BaseDecConstant.CURVE)) {
                        if (sv.valueArray == null) {
                            sv.valueArray = new DataSet(false);
                            sv.valueArray.setRowChange(true);
                        }
                    }
               /* JSONObject criteria = (JSONObject) sqlJson.get(BaseDecConstant.CRITERIA);
                List<String> pointList = new CopyOnWriteArrayList<>();
                for (String key : criteria.keySet()) {
                    JSONObject criteriaItemValue = (JSONObject) criteria.get(key);
                    String refString = (String) criteriaItemValue.get(BaseDecConstant.REF);
                    DataSet sdvList = QueryUtil.parseSetRef(repositoryBase, sv, refString, new QueryAssist(), false, true);
                    for (DataValue sdvInner : sdvList.singleValueSet) {
                        HashMap<DataPrimitive, String> sdv2point = repositoryBase.sdv2point();
                        if (sdvInner != null && sdv2point.containsKey(sdvInner.valuePrim)) {
                            String point = sdv2point.get(sdvInner.valuePrim);
                            pointList.add(point);
                        }
                    }
                }*/
                    break;

                default:
            }

        } catch (Exception e) {
            log.error("计算属性异常{}", e.getMessage());
        }

        sv.lastComputeTime = new Date();
        return computeValueChanged;

    }

    /**
     * 计算属性查询
     *
     * @param repositoryBase
     * @param dataProperty
     * @param sv
     * @return
     * @throws Exception
     */
    private static boolean calculatePropertyQuery(RepositoryBase repositoryBase, DataProperty dataProperty, DataValue sv) throws Exception {
        DataObject objectData = sv.parentObjectData;
        boolean computeValueChanged = false;
        Object valueBeforeCompute = null;
        if (repositoryBase.enable_factor) {
            valueBeforeCompute = sv.toJSON(true, 1);
        }
        JSONObject sqlJson = (JSONObject) JSON.parse(dataProperty.querySql);
        QueryAssist queryAssist = new QueryAssist(true);
        Object queryResult = QueryUtil.query(repositoryBase, sv, sqlJson, queryAssist);

        sv.rowFactor = queryAssist.rowFactor;
        sv.colFactorMap = queryAssist.colFactorMap;
        repositoryBase.dependency.add_compute(sv);

        if (dataProperty.propertyValueSchema.equals(BaseDecConstant.JSONOBJECT)) {
            DataObject queryResultObject = null;
            if (queryResult instanceof DataObject) {
                queryResultObject = (DataObject) queryResult;
            } else if (queryResult instanceof DataSet) {
                DataSet queryResultArray = (DataSet) queryResult;
                if (queryResultArray.set.size() == 1) {
                    queryResultObject = queryResultArray.set.get(0);
                }
            }
            if (queryResultObject != null) {
                DataObject arrayItemTmp = queryResultObject;
                Map<String, Boolean> fatherReturnColumnMap = new HashMap<>(16);
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
                DataObject sod = new DataObject(repositoryBase, objectData, dataProperty.propertyName, null, null, dataProperty.queryAttached, arrayItemTmp);
                if (fatherReturnColumnMap.size() > 0) {
                    sod.fatherReturnColumnMap = fatherReturnColumnMap;
                }
                repositoryBase.dependency.sdv2Children.putIfAbsent(arrayItemTmp, new CopyOnWriteArrayList<>());
                repositoryBase.dependency.sdv2Children.get(arrayItemTmp).add(sod);
                sv.valueObject = sod;
            } else {
                sv.valueObject = null;
            }
            if (sv.valueObject != null) {
                if (queryResult instanceof DataObject) {
                    sv.valueObject.setRowChange(queryResultObject.getRowChange());
                    sv.valueObject.setColChange(queryResultObject.getColChange());
                } else {
                    DataSet queryResultArray = (DataSet) queryResult;
                    sv.valueObject.setRowChange(queryResultArray.getRowChange());
                    sv.valueObject.setColChange(queryResultArray.getColChange());
                }
            }
        } else if (dataProperty.propertyValueSchema.equals(BaseDecConstant.JSONARRAY)) {
            DataSet array = (DataSet) queryResult;
            if (array != null) {
                if (array.isSingleValueSet) {
                    if (sv.valueArray == null) {
                        sv.valueArray = new DataSet(true);
                    }
                    sv.valueArray.singleValueSet.clear();
                    for (int i = 0; i < array.singleValueSet.size(); i++) {
                        DataValue arrayItem = array.singleValueSet.get(i);
                        DataValue sod = new DataValue(repositoryBase, null, null, null);
                        sod.valuePrim = new DataPrimitive();
                        if (arrayItem == null || arrayItem.valuePrim == null) {
                            sod.valuePrim.value = null;
                        } else {
                            sod.valuePrim.value = arrayItem.valuePrim.value;
                        }
                        sv.valueArray.singleValueSet.add(sod);
                    }
                } else {
                    if (sv.valueArray == null) {
                        sv.valueArray = new DataSet(false);
                    }
                    if (sv.valueArray.set != null) {
                        sv.valueArray.set.clear();
                    }
                    for (int i = 0; i < array.set.size(); i++) {
                        DataObject arrayItemTmp = array.set.get(i);
                        Map<String, Boolean> fatherReturnColumnMap = new HashMap<>(16);
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
                        DataObject sod = new DataObject(repositoryBase, null, null, sv, null, dataProperty.queryAttached, arrayItemTmp);
                        if (fatherReturnColumnMap.size() > 0) {
                            sod.fatherReturnColumnMap = fatherReturnColumnMap;
                        }
                        repositoryBase.dependency.sdv2Children.putIfAbsent(arrayItemTmp, new CopyOnWriteArrayList<DataObject>());
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
                            DataValue keySsv = new DataValue(repositoryBase, sod, keyDefault, null);
                            keySsv.valuePrim = new DataPrimitive();
                            keySsv.valuePrim.value = UUID.randomUUID().toString().replaceAll("-", "");
                            keySsv.valuePrim.change = false;
                            sod.put(keyDefault, keySsv);
                        }
                        sv.valueArray.set.add(sod);
                    }
                }
            }
            if (sv.valueArray != null) {
                if (array != null) {
                    sv.valueArray.setRowChange(array.getRowChange());
                }
            }
            if (sv.valueArray != null) {
                if (array != null) {
                    sv.valueArray.setColChange(array.getColChange());
                }
            }
            if (sv.valueArray != null) {
                sv.valueArray.path = BaseDecConstant.REF + ":" + PathUtil.getDataPath(sv);
            }
        } else {
            if (sv.valuePrim == null) {
                sv.valuePrim = new DataPrimitive();
            }
            if (queryResult instanceof DataSet) {
                DataSet sdvListInner = (DataSet) queryResult;
                if (sdvListInner.singleValueSet.size() == 1) {
                    if (sdvListInner.singleValueSet.get(0) != null) {
                        sv.valuePrim.value = sdvListInner.singleValueSet.get(0).valuePrim.value;
                    }
                }
                sv.valuePrim.change = sdvListInner.getRowChange();
            } else {
                DataPrimitive sdp = (DataPrimitive) queryResult;
                if (sdp != null) {
                    sv.valuePrim.value = sdp.value;
                }
                if (sdp != null) {
                    sv.valuePrim.change = sdp.change;
                }
            }
            if (sv.valuePrim.change) {
                if (sv.parentObjectData.parentArrayData != null) {
                    if (!sv.parentObjectData.parentArrayData.valueArray.getRowChange()) {
                        sv.parentObjectData.parentArrayData.valueArray.setColChange(sv.myPropertyName);
                    }
                } else {
                    if (!sv.parentObjectData.getRowChange()) {
                        sv.parentObjectData.setColChange(sv.myPropertyName);
                    }
                }
            }
            if (dataProperty.propertyValueSchema.equals(BaseDecConstant.INT)) {
                if (sv.valuePrim.value != null) {
                    Object jt = sv.valuePrim.value;
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
                    sv.valuePrim.value = jtValue;
                }
            } else if (dataProperty.propertyValueSchema.equals(BaseDecConstant.DOUBLE)) {
                if (sv.valuePrim.value != null) {
                    Object jt = sv.valuePrim.value;
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
                    sv.valuePrim.value = jtValue;
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
            if (tmpData instanceof DataValue) {
                DataValue currData = (DataValue) tmpData;
                currData = currData.valueObject.get(valuePath.getString(index));
                tmpData = currData;
                if (currData == null) {
                    continue;
                }
                if (currData.valueArray != null) {
                    if (index < valuePath.size() - 1) {
                        index++;
                        DataObject matchItem = null;
                        for (String keyName : AttributeFilteringUtil.keyProperty) {
                            for (DataObject sdbInner : currData.valueArray.set) {
                                if (sdbInner.containsKey(keyName) && sdbInner.get(keyName).valuePrim.value.equals(valuePath.getString(index))) {
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
                DataObject sdo = (DataObject) tmpData;
                DataValue currData = sdo.get(valuePath.getString(index));
                tmpData = currData;
                if (currData.valueArray != null) {
                    if (index < valuePath.size() - 1) {
                        index++;
                        DataObject matchItem = null;
                        for (String keyName : AttributeFilteringUtil.keyProperty) {
                            for (DataObject sdbInner : currData.valueArray.set) {
                                if (sdbInner.containsKey(keyName) && sdbInner.get(keyName).valuePrim.value.equals(valuePath.getString(index))) {
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

        if (tmpData instanceof DataValue) {
            DataValue currData = (DataValue) tmpData;
            int level = currData.relProperty == null ? 1 : Integer.parseInt(currData.relProperty.readLevel);
            result = currData.toJSON(true, level == 0 ? -1 : level);
        } else {
            DataObject currData = (DataObject) tmpData;
            int level = (currData.parentArrayData != null && currData.parentArrayData.relProperty != null) ? Integer.parseInt(currData.parentArrayData.relProperty.readLevel) : 1;
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
        if (tmpData instanceof DataValue) {
            DataValue currData = (DataValue) tmpData;
            result = currData.toJSON(true, level == 0 ? -1 : level, change);
        } else {
            DataObject currData = (DataObject) tmpData;
            result = currData.toJSON(level == 0 ? -1 : level, change);
        }

        return result;
    }
}
