package com.hdwa.sdk.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hdwa.sdk.entity.exception.ExceptionItem;
import com.hdwa.sdk.entity.repository.RepositoryBase;
import com.hdwa.sdk.entity.repository.WalkerList;
import com.hdwa.sdk.entity.repository.WalkerWrapper;
import com.hdwa.sdk.entity.scene.*;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;

@Slf4j
public class ComputeUtil {
    public static ExecutorService fixedThreadPool = Executors.newFixedThreadPool(16);

    public static void RefreshRepository(RepositoryBase Repository) {
        Repository.customobject2host = new ConcurrentHashMap<SceneObject, SceneProperty>();
        Repository.attachproperty2host = new ConcurrentHashMap<SceneProperty, SceneProperty>();
        Repository.property2customobject = new ConcurrentHashMap<SceneProperty, SceneObject>();
        Repository.p2walker1 = new ConcurrentHashMap<SceneProperty, WalkerWrapper>();
        Repository.p2walker2 = new ConcurrentHashMap<SceneProperty, WalkerList>();
        RefreshObject(Repository, null, Repository.sceneObject, false, -1);
    }

    private static void RefreshObject(RepositoryBase Repository, SceneProperty parentSP, SceneObject so, boolean so_is_sai, int soIndex) {
        if (parentSP != null) {
            if (so_is_sai) {
                Repository.staticobject2host.put(so, parentSP);
                Repository.staticobject2index.put(so, soIndex);
            } else {
                Repository.customobject2host.put(so, parentSP);
            }
        }
        for (SceneProperty sp : so.propertyList) {
            RefreshProperty(Repository, so, null, sp, so_is_sai);
        }
    }

    private static void RefreshProperty(RepositoryBase Repository, SceneObject parentSO, SceneProperty parentSP, SceneProperty sp,
                                        boolean so_is_sai) {
        if (parentSO != null) {
            if (so_is_sai) {
                Repository.property2staticobject.put(sp, parentSO);
            } else {
                Repository.property2customobject.put(sp, parentSO);
            }
        }
        if (parentSP != null) {
            Repository.attachproperty2host.put(sp, parentSP);
        }
        if (sp.query_attached != null) {
            for (SceneProperty spInner : sp.query_attached) {
                RefreshProperty(Repository, null, sp, spInner, false);
            }
        }
        if (sp.custom_object != null) {
            RefreshObject(Repository, sp, sp.custom_object, false, -1);
        }
        if (sp.static_array != null) {
            for (int soIndex = 0; soIndex < sp.static_array.length; soIndex++) {
                SceneObject SO = sp.static_array[soIndex];
                RefreshObject(Repository, sp, SO, true, soIndex);
            }
        }
    }

    public static List<List<SceneProperty>> computePrepare(RepositoryBase Repository) throws Exception {
        // 先整理顺序
        List<SceneProperty> spList = getAll(Repository.sceneObject);
        // 语法检查、处理expression
        List<ExceptionItem> exceptionList = new CopyOnWriteArrayList<ExceptionItem>();
        for (SceneProperty spInner : spList) {
            if (spInner.propertyValueType.equals("query")) {
                JSONObject sql_json = null;
                try {
                    sql_json = JSON.parseObject(spInner.query_sql);
                } catch (Exception e) {
                    ExceptionItem MyException = new ExceptionItem(PathUtil.getPropertyPath(Repository, spInner), "json format error",
                            spInner.query_sql);
                    exceptionList.add(MyException);
                }
                if (sql_json != null) {
                    String QueryType = (String) sql_json.get("QueryType");
                    if (QueryType != null) {
                        if (QueryType.equals("expression")) {
                            String expression = (String) sql_json.get("expression");
                            JSONObject CriteriaObject = (JSONObject) sql_json.get("Criteria");
                            try {
                                List<ExceptionItem> exceptionListInner = ExpressionUtil.buildAndPut(Repository, spInner, expression, CriteriaObject);
                                exceptionList.addAll(exceptionListInner);
                            } catch (Exception e) {
                                ExceptionItem MyException = new ExceptionItem(PathUtil.getPropertyPath(Repository, spInner),
                                        "expression format error", expression);
                                exceptionList.add(MyException);
                            }
                        }
                    }
                }
            } else if (spInner.propertyValueType.equals("deamon")) {
                JSONObject sql_json = null;
                try {
                    sql_json = JSON.parseObject(spInner.query_sql);
                } catch (Exception e) {
                    ExceptionItem MyException = new ExceptionItem(PathUtil.getPropertyPath(Repository, spInner), "json format error",
                            spInner.query_sql);
                    exceptionList.add(MyException);
                }
                if (sql_json != null) {
                }
            } else if (spInner.propertyValueType.equals("static")) {
                if (spInner.propertyValueSchema.equals("JSONObject")) {
                } else if (spInner.propertyValueSchema.equals("JSONArray")) {
                } else {
                    if (Repository.check_static_value_basic && (spInner.static_value == null || spInner.static_value.length() == 0)) {
                        ExceptionItem MyException = new ExceptionItem(PathUtil.getPropertyPath(Repository, spInner), "static_value error",
                                spInner.static_value);
                        exceptionList.add(MyException);
                    }
                }
            }
        }
        if (exceptionList.size() > 0) {
            throw new ExceptionWrapper(exceptionList);
        }
        log.debug("****************");
        for (SceneProperty spInner : spList) {
            // 打印路径
            {
                String path = PathUtil.getPropertyPath(Repository, spInner);
                log.debug("getPropertyBefore:" + path);
                // if (path.equals("基础对象-设备[2]-设备类型-状态统计[0]-数量")) {
                // System.out.println();
                // }
            }
            try {
                List<SceneProperty> beforeList = CheckUtil.getPropertyBefore(Repository, spInner);
                Repository.beforeDic.put(spInner, beforeList);
                for (SceneProperty spInner2 : beforeList) {
                    log.debug(PathUtil.getPropertyPath(Repository, spInner2));
                }
            } catch (ExceptionItem e) {
                exceptionList.add(e);
            } catch (ExceptionWrapper e) {
                exceptionList.addAll(e.itemList);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                exceptionList.add(new ExceptionItem(PathUtil.getPropertyPath(Repository, spInner), e.getMessage(), null));
            }
        }
        if (exceptionList.size() > 0) {
            throw new ExceptionWrapper(exceptionList);
        }

        Map<SceneProperty, Boolean> processedDic = new ConcurrentHashMap<SceneProperty, Boolean>();
        List<List<SceneProperty>> spListList = new CopyOnWriteArrayList<List<SceneProperty>>();
        while (true) {
            int count_curr = 0;
            List<SceneProperty> spInnerList = new CopyOnWriteArrayList<SceneProperty>();
            for (SceneProperty spInner : spList) {
                if (processedDic.containsKey(spInner)) {
                    continue;
                }
                List<SceneProperty> beforeList = Repository.beforeDic.get(spInner);
                boolean all_finish = true;
                for (SceneProperty sp_before : beforeList) {
                    if (!processedDic.containsKey(sp_before)) {
                        all_finish = false;
                        break;
                    }
                }
                if (all_finish) {
                    count_curr++;
                    spInnerList.add(spInner);
                }
            }
            if (count_curr == 0) {
                break;
            }
            spListList.add(spInnerList);
            for (SceneProperty spInner : spInnerList) {
                processedDic.put(spInner, true);
            }
        }
        log.debug("****************************************************************");
        for (List<SceneProperty> spInnerList : spListList) {
            log.debug("********************************");
            for (SceneProperty spInner2 : spInnerList) {
                log.debug(PathUtil.getPropertyPath(Repository, spInner2));
                if (!Repository.property2SDV.containsKey(spInner2)) {
                    Repository.property2SDV.put(spInner2, new CopyOnWriteArrayList<SceneDataValue>());
                }
            }
            log.debug("********************************");
        }
        log.debug("****************************************************************");

        return spListList;
    }

    // 获取下级所有非custom类型的SceneProperty
    private static List<SceneProperty> getAll(SceneObject so) {
        List<SceneProperty> result = new CopyOnWriteArrayList<SceneProperty>();
        for (SceneProperty sp : so.propertyList) {
            List<SceneProperty> resultinner = getAll(sp);
            result.addAll(resultinner);
        }
        return result;
    }

    // 获取下级所有非custom类型的SceneProperty
    private static List<SceneProperty> getAll(SceneProperty sp) {
        List<SceneProperty> result = new CopyOnWriteArrayList<SceneProperty>();
        if (sp.propertyValueType.equals("static")) {
            result.add(sp);
            if (sp.propertyValueSchema.equals("JSONArray")) {
                if (sp.static_array == null) {
                    sp.static_array = new SceneObject[0];
                }
                for (SceneObject SceneObject : sp.static_array) {
                    List<SceneProperty> resultinner = getAll(SceneObject);
                    result.addAll(resultinner);
                }
                if (sp.query_attached != null) {
                    for (SceneProperty spInner : sp.query_attached) {
                        List<SceneProperty> resultinner = getAll(spInner);
                        result.addAll(resultinner);
                    }
                }
            }
        } else if (sp.propertyValueType.equals("query")) {
            result.add(sp);
            if (sp.query_attached != null) {
                for (SceneProperty spInner : sp.query_attached) {
                    List<SceneProperty> resultinner = getAll(spInner);
                    result.addAll(resultinner);
                }
            }
        } else if (sp.propertyValueType.equals("custom")) {
            if (sp.custom_object == null) {
                sp.custom_object = new SceneObject();
            }
            result = getAll(sp.custom_object);
        } else if (sp.propertyValueType.equals("deamon")) {
            result.add(sp);
        }
        return result;
    }

    public static void computeAll(RepositoryBase Repository, List<List<SceneProperty>> spListList) throws Exception {
        // 开始计算
        Repository.objectData = new SceneDataObject(Repository, null, null, null, Repository.sceneObject, null, null);
        Repository.log_step_count(spListList.size());
        int step = 1;
        for (List<SceneProperty> spInnerList : spListList) {
            List<SceneDataValue> itemList = new CopyOnWriteArrayList<SceneDataValue>();
            for (SceneProperty spInner2 : spInnerList) {
                List<SceneDataValue> sdvList = Repository.property2SDV.get(spInner2);
                if (Repository.use_thread) {
                    itemList.addAll(sdvList);
                } else {
                    // 打印路径
                    String path = PathUtil.getPropertyPath(Repository, spInner2);
                    log.info("ComputeOnce:" + path);
                    // if (path.equals("检查-正确-对象-筛选列[0]-清单")) {
                    // System.out.println();
                    // }
                    boolean use_threadOne = false;
                    if (use_threadOne) {
                        List<SceneDataValue> itemListInner = new CopyOnWriteArrayList<SceneDataValue>();
                        itemListInner.addAll(sdvList);
                        CountDownLatch cdl = new CountDownLatch(itemListInner.size());
                        for (SceneDataValue sdv : itemListInner) {
                            fixedThreadPool.execute(new ComputeThread(Repository, sdv, cdl));
                        }
                        cdl.await();
                    } else {
                        for (SceneDataValue sdv : sdvList) {
                            try {
                                computeProperty(Repository, sdv);
                            } catch (Exception e) {
                                try {
                                    String pathInner = PathUtil.getPropertyPath(Repository, sdv.rel_property);
                                    log.error(pathInner + " " + e.getMessage(), e);
                                } catch (Exception e1) {
                                    log.error(e.getMessage(), e);
                                }
                                throw e;
                            }
                        }
                    }
                }
            }
            if (Repository.use_thread) {
                Repository.log_step_begin(step, spInnerList.size(), itemList.size());
                log.info("ComputeStep:" + "\t" + (step++) + "\t" + spInnerList.size() + "\t" + itemList.size());
                CountDownLatch cdl = new CountDownLatch(itemList.size());
                for (SceneDataValue sdv : itemList) {
                    fixedThreadPool.execute(new ComputeThread(Repository, sdv, cdl));
                }
                cdl.await();
                Thread.sleep(1000L);
                int finish_count = 0;
                for (SceneDataValue sdv : itemList) {
                    if (sdv.last_compute_time != null) {
                        finish_count++;
                    }
                }
                Repository.log_step_end(step, finish_count);
            }
        }
    }

    public static boolean computeProperty(RepositoryBase Repository, SceneDataValue sv) throws Exception {
        boolean computeValueChanged = false;
        String svPath = PathUtil.getDataPath(sv);
        log.debug("computeProperty: " + svPath);
        // if (svPath.equals("基础.产品模块.[ktmd].管理对象.[xinfengji].清单")) {
        // System.out.println();
        // }
        SceneDataObject objectData = sv.parentObjectData;
        SceneProperty sceneProperty = sv.rel_property;
        if (sceneProperty.propertyValueType.equals("static")) {
            if (sv.rel_property.propertyValueSchema.equals("JSONObject")) {
                sv.finish = true;
            } else if (sv.rel_property.propertyValueSchema.equals("JSONArray")) {
                boolean finish = true;
                for (SceneDataObject sdbInner : sv.value_array.set) {
                    if (sdbInner instanceof SceneDataObject) {
                        SceneDataObject SODInner = (SceneDataObject) sdbInner;
                        for (String keySOD : SODInner.keySet()) {
                            SceneDataValue SVInner = SODInner.get(keySOD);
                            if (!SVInner.finish) {
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
        } else if (sceneProperty.propertyValueType.equals("query")) {
            Object valueBeforeCompute = null;
            if (Repository.enable_factor) {
                valueBeforeCompute = sv.toJSON(true, 1);
            }
            JSONObject sql_json = (JSONObject) JSON.parse(sceneProperty.query_sql);
            QueryAssist QueryAssist = new QueryAssist(Repository.enable_factor);
            Object queryResult = QueryUtil.query(Repository, sv, sql_json, QueryAssist);
            if (QueryAssist.rowChangeNeed) {
                sv.rowFactor = QueryAssist.rowFactor;
                sv.colFactorMap = QueryAssist.colFactorMap;
                Repository.dependency.add_compute(sv);
            }
            if (sceneProperty.propertyValueSchema.equals("JSONObject")) {
                // 结果集的聚合选择等等
                SceneDataObject queryResultObject = null;
                if (queryResult instanceof SceneDataObject) {
                    queryResultObject = (SceneDataObject) queryResult;
                } else if (queryResult instanceof SceneDataSet) {
                    SceneDataSet queryResultArray = (SceneDataSet) queryResult;
                    if (queryResultArray.set.size() == 0) {
                        // log.error(svPath + "\t" + "返回对象数量" + queryResultArray.set.size());
                    } else if (queryResultArray.set.size() == 1) {
                        queryResultObject = queryResultArray.set.get(0);
                    } else {
                        // log.error(svPath + "\t" + "返回对象数量" + queryResultArray.set.size() + "太多");
                    }
                }
                if (queryResultObject != null) {
                    // 往前找祖先
                    SceneDataObject arrayItemTmp = queryResultObject;
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
                    SceneDataObject sod = new SceneDataObject(Repository, objectData, sceneProperty.propertyName, null, null,
                            sceneProperty.query_attached, arrayItemTmp);
                    if (fatherReturnColumnMap.size() > 0) {
                        sod.fatherReturnColumnMap = fatherReturnColumnMap;
                    }
                    Repository.dependency.sdv2Children.putIfAbsent(arrayItemTmp, new CopyOnWriteArrayList<SceneDataObject>());
                    Repository.dependency.sdv2Children.get(arrayItemTmp).add(sod);
                    sv.value_object = sod;
                } else {
                    sv.value_object = null;
                }
                if (sv.value_object != null) {
                    if (queryResult instanceof SceneDataObject) {
                        queryResultObject = (SceneDataObject) queryResult;
                        sv.value_object.setRowChange(queryResultObject.getRowChange());
                        sv.value_object.setColChange(queryResultObject.getColChange());
                    } else if (queryResult instanceof SceneDataSet) {
                        SceneDataSet queryResultArray = (SceneDataSet) queryResult;
                        sv.value_object.setRowChange(queryResultArray.getRowChange());
                        sv.value_object.setColChange(queryResultArray.getColChange());
                    }
                }
            } else if (sceneProperty.propertyValueSchema.equals("JSONArray")) {
                SceneDataSet array = (SceneDataSet) queryResult;
                if (array.isSingleValueSet) {
                    if (sv.value_array == null) {
                        sv.value_array = new SceneDataSet(true);
                    }
                    sv.value_array.singleValueSet.clear();
                    for (int i = 0; i < array.singleValueSet.size(); i++) {
                        SceneDataValue arrayItem = array.singleValueSet.get(i);
                        SceneDataValue sod = new SceneDataValue(Repository, null, null, null);
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
                    sv.value_array.set.clear();
                    for (int i = 0; i < array.set.size(); i++) {
                        SceneDataObject arrayItem = array.set.get(i);
                        // 往前找祖先
                        SceneDataObject arrayItemTmp = arrayItem;
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
                        SceneDataObject sod = new SceneDataObject(Repository, null, null, sv, null, sceneProperty.query_attached, arrayItemTmp);
                        if (fatherReturnColumnMap.size() > 0) {
                            sod.fatherReturnColumnMap = fatherReturnColumnMap;
                        }
                        Repository.dependency.sdv2Children.putIfAbsent(arrayItemTmp, new CopyOnWriteArrayList<SceneDataObject>());
                        Repository.dependency.sdv2Children.get(arrayItemTmp).add(sod);
                        boolean exist_key = false;
                        for (String keyName : AttributeFilteringUtil.keyProperty) {
                            if (sod.containsKey(keyName)) {
                                exist_key = true;
                                break;
                            }
                        }
                        // 自动生成键
                        if (!exist_key) {
                            String keyDefault = AttributeFilteringUtil.keyDefault();
                            SceneDataValue keySDV = new SceneDataValue(Repository, sod, keyDefault, null);
                            keySDV.value_prim = new SceneDataPrimitive();
                            keySDV.value_prim.value = UUID.randomUUID().toString().replaceAll("-", "");
                            keySDV.value_prim.change = false;
                            sod.put(keyDefault, keySDV);
                        }
                        sv.value_array.set.add(sod);
                    }
                }
                sv.value_array.setRowChange(array.getRowChange());
                sv.value_array.setColChange(array.getColChange());
                //
                sv.value_array.path = "ref:" + PathUtil.getDataPath(sv);
            } else {
                if (sv.value_prim == null) {
                    sv.value_prim = new SceneDataPrimitive();
                }
                // 计算并放入objectData中
                if (queryResult instanceof SceneDataSet) {
                    SceneDataSet sdvListInner = (SceneDataSet) queryResult;
                    if (sdvListInner.singleValueSet.size() == 0) {
                    } else if (sdvListInner.singleValueSet.size() == 1) {
                        sv.value_prim.value = sdvListInner.singleValueSet.get(0).value_prim.value;
                    } else {
                    }
                    sv.value_prim.change = sdvListInner.getRowChange();
                } else {
                    SceneDataPrimitive sdp = (SceneDataPrimitive) queryResult;
                    sv.value_prim.value = sdp.value;
                    sv.value_prim.change = sdp.change;
                }
                if (sv.value_prim.change) {
                    // 需要向上影响ColChange
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
                if (sceneProperty.propertyValueSchema.equals("int")) {
                    if (sv.value_prim.value != null) {
                        Object jt = sv.value_prim.value;
                        int jtValue;
                        if (jt instanceof Integer) {
                            jtValue = ((Integer) jt).intValue();
                        } else if (jt instanceof Long) {
                            jtValue = ((Long) jt).intValue();
                        } else if (jt instanceof Float) {
                            jtValue = ((Float) jt).intValue();
                        } else {
                            jtValue = ((Double) jt).intValue();
                        }
                        sv.value_prim.value = jtValue;
                    }
                } else if (sceneProperty.propertyValueSchema.equals("double")) {
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
                            jtValue = ((Double) jt).doubleValue();
                        }
                        sv.value_prim.value = jtValue;
                    }
                }
            }
            sv.finish = true;
            Object valueAfterCompute = null;
            if (Repository.enable_factor) {
                valueAfterCompute = sv.toJSON(true, 1);
                computeValueChanged = !FastJsonCompareUtil.Instance().CompareObject(valueBeforeCompute, valueAfterCompute, true);
            }
        } else if (sceneProperty.propertyValueType.equals("custom")) {
            if (sv.value_object == null) {
                sv.value_object = new SceneDataObject(Repository, objectData, sceneProperty.propertyName, null, sceneProperty.custom_object, null,
                        null);
            }
        } else if (sceneProperty.propertyValueType.equals("deamon")) {
            JSONObject sql_json = (JSONObject) JSON.parse(sceneProperty.query_sql);
            String QueryType = (String) sql_json.get("QueryType");
            if (QueryType.endsWith("trend")) {
                if (sv.value_prim == null) {
                    sv.value_prim = new SceneDataPrimitive();
                    sv.value_prim.value = 0;
                    sv.value_prim.change = true;
                }
            } else if (QueryType.endsWith("curve")) {
                if (sv.value_array == null) {
                    sv.value_array = new SceneDataSet(false);
                    sv.value_array.setRowChange(true);
                }
            }
            JSONObject Criteria = (JSONObject) sql_json.get("Criteria");
            List<String> pointList = new CopyOnWriteArrayList<String>();
            for (String key : Criteria.keySet()) {
                JSONObject CriteriaItemValue = (JSONObject) Criteria.get(key);
                String refString = (String) CriteriaItemValue.get("ref");
                SceneDataSet sdvList = QueryUtil.parseSetRef(Repository, sv, refString, new QueryAssist(), false, true);
                for (SceneDataValue sdvInner : sdvList.singleValueSet) {
                    ConcurrentHashMap<SceneDataPrimitive, String> sdv2point = Repository.sdv2point();
                    if (sdvInner != null && sdv2point.containsKey(sdvInner.value_prim)) {
                        String point = sdv2point.get(sdvInner.value_prim);
                        pointList.add(point);
                    }
                }
            }
            Repository.deamon_sdv2pointList.put(sv, pointList);
        }
        sv.last_compute_time = new Date();

        // 检查可变性与附加属性
        boolean change = false;
        if (sv.value_array == null && sv.value_object == null && sv.value_prim == null) {
        } else if (sv.value_array != null && sv.value_object == null && sv.value_prim == null) {
            change = sv.value_array.getRowChange();
        } else if (sv.value_array == null && sv.value_object != null && sv.value_prim == null) {
            change = sv.value_object.getRowChange();
        } else if (sv.value_array == null && sv.value_object == null && sv.value_prim != null) {
            change = sv.value_prim.change;
        } else {
            throw new Exception(svPath + "\t" + "存在多种值");
        }
        if (change) {
            // 记录依赖项

            if (sv.rel_property.query_attached != null && sv.rel_property.query_attached.length > 0) {
                throw new Exception(svPath + "\t" + "是可变集合，不应该有附加属性");
            }
        }

        return computeValueChanged;
    }

    public static Object getValueObject(RepositoryBase Repository, JSONArray valuePath) {
        int index = 0;
        Object tmpData = Repository.objectData.get(valuePath.getString(index));
        index++;
        while (index < valuePath.size()) {
            if (tmpData instanceof SceneDataValue) {
                SceneDataValue currData = (SceneDataValue) tmpData;
                currData = currData.value_object.get(valuePath.getString(index));
                tmpData = currData;
                if (currData.value_array != null) {
                    if (index < valuePath.size() - 1) {
                        index++;
                        SceneDataObject matchItem = null;
                        for (String keyName : AttributeFilteringUtil.keyProperty) {
                            for (SceneDataObject sdbInner : currData.value_array.set) {
                                SceneDataObject sodInner = (SceneDataObject) sdbInner;
                                if (sodInner.containsKey(keyName) && sodInner.get(keyName).value_prim.value.equals(valuePath.getString(index))) {
                                    matchItem = sodInner;
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
            } else if (tmpData instanceof SceneDataObject) {
                SceneDataObject currSOD = (SceneDataObject) tmpData;
                SceneDataValue currData = currSOD.get(valuePath.getString(index));
                tmpData = currData;
                if (currData.value_array != null) {
                    if (index < valuePath.size() - 1) {
                        index++;
                        SceneDataObject matchItem = null;
                        for (String keyName : AttributeFilteringUtil.keyProperty) {
                            for (SceneDataObject sdbInner : currData.value_array.set) {
                                SceneDataObject sodInner = (SceneDataObject) sdbInner;
                                if (sodInner.containsKey(keyName) && sodInner.get(keyName).value_prim.value.equals(valuePath.getString(index))) {
                                    matchItem = sodInner;
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

    public static Object getValueJSON(Object tmpData) {
        Object result;
        if (tmpData == null) {
            return null;
        }

        if (tmpData instanceof SceneDataValue) {
            SceneDataValue currData = (SceneDataValue) tmpData;
            int read_level = currData.rel_property == null ? 1 : Integer.parseInt(currData.rel_property.read_level);
            result = currData.toJSON(true, read_level == 0 ? -1 : read_level);
        } else {
            SceneDataObject currData = (SceneDataObject) tmpData;
            int read_level = (currData.parentArrayData != null && currData.parentArrayData.rel_property != null)
                    ? Integer.parseInt(currData.parentArrayData.rel_property.read_level) : 1;
            result = currData.toJSON(read_level == 0 ? -1 : read_level);
        }

        return result;
    }

    public static Object getValueJSON(Object tmpData, int read_level) {
        Object result = getValueJSON(tmpData, read_level, false);
        return result;
    }

    public static Object getValueJSON(Object tmpData, int read_level, boolean with_change) {
        Object result;
        if (tmpData instanceof SceneDataValue) {
            SceneDataValue currData = (SceneDataValue) tmpData;
            result = currData.toJSON(true, read_level == 0 ? -1 : read_level, with_change);
        } else {
            SceneDataObject currData = (SceneDataObject) tmpData;
            result = currData.toJSON(read_level == 0 ? -1 : read_level, with_change);
        }

        return result;
    }
}
