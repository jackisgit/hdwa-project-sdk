package com.hdwa.sdk.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hdwa.sdk.entity.repository.RepositoryBase;
import com.hdwa.sdk.entity.scene.DataObject;
import com.hdwa.sdk.entity.scene.DataValue;
import com.hdwa.sdk.entity.scene.DataProperty;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
public class RecursiveUtil {

    /**
     * 刷新数据节点，根据read_level
     *
     * @param tmpData
     * @throws Exception
     */
    public static Object fillTimeValueObject(Object tmpData, Map<String, JSONArray> Point2datas, Map<String, IntWrapper> Point2index, String time) {
        Object result = null;
        if (tmpData instanceof JSONObject) {
            JSONObject currData = (JSONObject) tmpData;
            result = fillTimeValue(currData, Point2datas, Point2index, time);
        } else if (tmpData instanceof JSONArray) {
            JSONArray currData = (JSONArray) tmpData;
            result = fillTimeValue(currData, Point2datas, Point2index, time);
        }
        return result;
    }

    private static Object fillTimeValueAssist(JSONArray datas, IntWrapper intWrapper, String time) {
        Object result = null;
        while (intWrapper.value < datas.size()) {
            JSONObject data = (JSONObject) datas.get(intWrapper.value);
            String timeInner = (String) data.get("data_time");
            if (timeInner.compareTo(time) < 0) {
                intWrapper.value++;
            } else {
                break;
            }
        }
        if (intWrapper.value < datas.size()) {
            JSONObject data = (JSONObject) datas.get(intWrapper.value);
            result = data.get("data_value");
        }
        return result;
    }

    private static JSONObject fillTimeValue(JSONObject sdo, Map<String, JSONArray> Point2datas, Map<String, IntWrapper> Point2index, String time) {
        if (sdo == null) {
            return null;
        }

        if (sdo.containsKey("id")) {
            JSONObject result = new JSONObject();
            String id = (String) sdo.get("id");
            result.put("id", id);
            for (String Key : sdo.keySet()) {
                String infoValue = (String) sdo.get(Key);
                if (infoValue.startsWith("point:")) {
                    String point = infoValue.substring("point:".length());
                    JSONArray datas = Point2datas.get(point);
                    IntWrapper intWrapper = Point2index.get(point);
                    Object data_value = fillTimeValueAssist(datas, intWrapper, time);
                    result.put(Key, data_value);
                }
            }
            return result;
        } else {
            JSONObject result = new JSONObject();
            for (String Key : sdo.keySet()) {
                Object value = sdo.get(Key);
                if (value instanceof String) {
                    result.put(Key, value);
                } else {
                    result.put(Key, fillTimeValueObject(value, Point2datas, Point2index, time));
                }
            }
            return result;
        }
    }

    private static Object fillTimeValue(JSONArray sdv, Map<String, JSONArray> Point2datas, Map<String, IntWrapper> Point2index, String time) {
        if (sdv == null) {
            return null;
        }

        JSONArray result = new JSONArray();
        for (Object sdb : sdv) {
            if (sdb instanceof JSONObject) {
                JSONObject sdbObject = (JSONObject) sdb;
                result.add(fillTimeValue(sdbObject, Point2datas, Point2index, time));
            } else {
                result.add(fillTimeValueObject(sdb, Point2datas, Point2index, time));
            }
        }
        return result;
    }

    /**
     * 刷新数据节点，根据read_level
     *
     * @param tmpData
     * @throws Exception
     */
    public static void refreshObject(RepositoryBase Repository, Object tmpData) throws Exception {
        Date currTime = new Date();
        if (tmpData instanceof DataValue) {
            DataValue currData = (DataValue) tmpData;
            int read_level = currData.relProperty == null ? 1 : Integer.parseInt(currData.relProperty.readLevel);
            read_level = read_level == 0 ? -1 : read_level;
            refresh(Repository, currData, read_level, currTime, false);
        } else if (tmpData instanceof DataObject) {
            DataObject currData = (DataObject) tmpData;
            int read_level = (currData.parentArrayData != null && currData.parentArrayData.relProperty != null)
                    ? Integer.parseInt(currData.parentArrayData.relProperty.readLevel) : 1;
            read_level = read_level == 0 ? -1 : read_level;
            refresh(Repository, currData, read_level, currTime);
        }
    }

    private static void refresh(RepositoryBase Repository, DataObject sdo, int depth, Date currTime) throws Exception {
        if (sdo == null) {
            return;
        }
        // String svPath = RecursiveUtil.getDataPath(sdo);
        // log.info("refreshObject" + "\t" + svPath);

        int depthInner = depth == -1 ? -1 : (depth > 0 ? depth - 1 : 0);
        for (String key : sdo.keySet()) {
            if (AttributeFilteringUtil.containsKey(key)) {
                continue;
            }

            refresh(Repository, sdo.get(key), depthInner, currTime, true);
        }
    }

    private static void refresh(RepositoryBase Repository, DataValue sdv, int depth, Date currTime, boolean use_offset_level) throws Exception {
        if (sdv == null) {
            return;
        }
        // String svPath = RecursiveUtil.getDataPath(sdv);
        // log.info("refreshValue" + "\t" + svPath);

        int curr_depth = depth;
        if (use_offset_level && sdv.relProperty != null && curr_depth != -1) {
            curr_depth -= Integer.parseInt(sdv.relProperty.offsetLevel);
            if (curr_depth < 0) {
                return;
            }
        }

        // 计算当前节点
        compute(Repository, sdv);
        // 计算下级节点
        if (depth > 0 || depth == -1) {
            if (sdv.valueObject != null) {
                refresh(Repository, sdv.valueObject, curr_depth, currTime);
            } else if (sdv.valueArray != null) {
                if (!sdv.valueArray.isSingleValueSet) {
                    for (int i = 0; i < sdv.valueArray.set.size(); i++) {
                        DataObject sdb = sdv.valueArray.set.get(i);
                        if (sdb != null) {
                            refresh(Repository, (DataObject) sdb, curr_depth, currTime);
                        }
                    }
                }
            }
        }
    }

    // 计算节点
    private static void compute(RepositoryBase Repository, DataValue sdv) throws Exception {
        if (sdv == null) {
            return;
        }

        if (sdv.relProperty != null && sdv.relProperty.propertyValueType.equals("query")) {
            if (sdv.relProperty.propertyValueSchema.equals("JSONObject") || sdv.relProperty.propertyValueSchema.equals("JSONArray")) {
                {
                    // 递归计算当前节点及下级
                    computeInner(Repository, sdv);
                }
            } else {
                // 计算
                computeInner(Repository, sdv);
            }
        }
    }

    /**
     * 先计算依赖项，再计算自身
     *
     * @param sv
     * @throws Exception
     */
    private static void computeInner(RepositoryBase Repository, DataValue sv) throws Exception {
        DataProperty dataProperty = sv.relProperty;
        JSONObject sql_json = JSON.parseObject(dataProperty.querySql);
        Map<String, Map<String, Boolean>> refList = new HashMap<>(16);
        CheckUtil.query(sql_json, refList);
        List<DataValue> svListAll = new CopyOnWriteArrayList<DataValue>();
        for (String refString : refList.keySet()) {
            String[] splits = refString.split("'");
            Object parentData;
            int splits_index;
            if (splits[0].startsWith("ancestor_")) {
                int generate = Integer.parseInt(splits[0].substring("ancestor_".length()));
                Object tmp = sv;
                while (generate > 0) {
                    if (tmp instanceof DataValue) {
                        DataValue tmpData = (DataValue) tmp;
                        tmp = tmpData.parentObjectData;
                    } else if (tmp != null) {
                        DataObject tmpData = (DataObject) tmp;
                        tmp = tmpData.parentObjectData != null ? tmpData.parentObjectData : tmpData.parentArrayData;
                    }
                    generate--;
                }
                parentData = tmp;
                splits_index = 1;
            } else {
                parentData = Repository.objectData;
                splits_index = 0;
            }

            // 查询目标可以是value_object或者value_array
            List<DataValue> svList = new CopyOnWriteArrayList<DataValue>();
            if (parentData instanceof DataValue) {
                DataValue tmpData = (DataValue) parentData;
                svList.add(tmpData);
            } else if (parentData != null) {
                DataObject tmpData = (DataObject) parentData;
                DataValue svWrapper = new DataValue(null, null, null, null);
                svWrapper.valueObject = tmpData;
                svList.add(svWrapper);
            }
            for (int i = splits_index; i < splits.length; i++) {
                List<DataValue> svListInner = new CopyOnWriteArrayList<DataValue>();
                String split = splits[i];
                int index_ = split.indexOf('=');
                if (index_ != -1) {
                    String propertyName = split.substring(0, index_);
                    String propertyValue = split.substring(index_ + 1);
                    for (DataValue svInner : svList) {
                        if (svInner.valueObject != null) {
                            DataObject sod = svInner.valueObject;
                            if (sod.containsKey(propertyName) && propertyValue.equals(sod.get(propertyName).valuePrim.value)) {
                                DataValue svWrapper = new DataValue(null, sod, propertyName, null);
                                svWrapper.valueObject = sod;
                                svListInner.add(svWrapper);
                            }
                        } else if (svInner.valueArray != null) {
                            for (DataObject sod : svInner.valueArray.set) {
                                if (sod.containsKey(propertyName) && propertyValue.equals(sod.get(propertyName).valuePrim.value)) {
                                    DataValue svWrapper = new DataValue(null, sod, propertyName, null);
                                    svWrapper.valueObject = sod;
                                    svListInner.add(svWrapper);
                                }
                            }
                        }
                    }
                } else {
                    for (DataValue svInner : svList) {
                        if (svInner.valueObject != null) {
                            svListInner.add(svInner.valueObject.get(split));
                        } else if (svInner.valueArray != null) {
                            for (DataObject sdb : svInner.valueArray.set) {
                                if (sdb != null) {
                                    DataObject sod = (DataObject) sdb;
                                    svListInner.add(sod.get(split));
                                }
                            }
                        }
                    }
                }
                svList = svListInner;
            }
            svListAll.addAll(svList);
        }

        for (DataValue sdv : svListAll) {
            compute(Repository, sdv);
        }

        if (sv.relProperty != null && sv.relProperty.propertyValueType.equals("query")) {
            if (sv.valueObject != null && sv.valueObject.getRowChange() || sv.valueArray != null && sv.valueArray.getRowChange()
                    || sv.valuePrim != null && sv.valuePrim.change) {
                ReentrantLock lock = sv.lock;
                try {
                    lock.lock();
                    Date currTime = new Date();
                    if (sv.lastComputeTime == null || currTime.getTime() - sv.lastComputeTime.getTime() > 1000L) {
                        // String svPath = RecursiveUtil.getDataPath(sv);
                        // log.info("computeProperty" + "\t" + svPath);
                        CalculateApiJsonUtil.calculateProperty(Repository, sv);
                    }
                } finally {
                    lock.unlock();
                }
            }
        }
    }

    public static void print_variable_set(String path, DataObject sdo) {
        if (sdo == null) {
            return;
        }
        if (sdo.relObject == null && sdo.parentArrayData == null) {
            return;
        }

        for (String key : sdo.keySet()) {
            if (AttributeFilteringUtil.containsKey(key)) {
                continue;
            }
            print_variable_set(path + "-" + key, sdo.get(key));
        }
    }

    public static void print_variable_set(String path, DataValue sdv) {
        if (sdv == null) {
            return;
        }
        if (sdv.relProperty == null) {
            return;
        }

        if (sdv.relProperty.propertyValueSchema.equals("JSONObject")) {
            if (sdv.relProperty.propertyValueType.equals("query")) {
                log.debug(
                        path + "\t" + "variable set object" + "\t" + sdv.relProperty.propertyValueType + "\t" + sdv.valueObject.dataChange.toString());
            }
            print_variable_set(path, sdv.valueObject);
        } else if (sdv.relProperty.propertyValueSchema.equals("JSONArray")) {
            if (sdv.relProperty.propertyValueType.equals("query")) {
                log.debug(path + "\t" + "variable set array" + "\t" + sdv.relProperty.propertyValueType + "\t" + sdv.valueArray.dataChange.toString());
            }
            for (int i = 0; i < sdv.valueArray.set.size(); i++) {
                DataObject sdb = sdv.valueArray.set.get(i);
                if (sdb != null) {
                    print_variable_set(path + "[" + i + "]", sdb);
                }
            }
        } else {
            if (sdv.relProperty.propertyValueType.equals("query")) {
                {
                    log.debug(path + "\t" + "variable value" + "\t" + sdv.valuePrim.change);
                }
            }
        }
    }
}
