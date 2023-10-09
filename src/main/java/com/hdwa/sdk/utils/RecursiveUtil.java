package com.hdwa.sdk.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hdwa.sdk.entity.repository.RepositoryBase;
import com.hdwa.sdk.entity.scene.SceneDataObject;
import com.hdwa.sdk.entity.scene.SceneDataValue;
import com.hdwa.sdk.entity.scene.SceneProperty;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
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
        if (tmpData instanceof SceneDataValue) {
            SceneDataValue currData = (SceneDataValue) tmpData;
            int read_level = currData.rel_property == null ? 1 : Integer.parseInt(currData.rel_property.read_level);
            read_level = read_level == 0 ? -1 : read_level;
            refresh(Repository, currData, read_level, currTime, false);
        } else if (tmpData instanceof SceneDataObject) {
            SceneDataObject currData = (SceneDataObject) tmpData;
            int read_level = (currData.parentArrayData != null && currData.parentArrayData.rel_property != null)
                    ? Integer.parseInt(currData.parentArrayData.rel_property.read_level) : 1;
            read_level = read_level == 0 ? -1 : read_level;
            refresh(Repository, currData, read_level, currTime);
        }
    }

    private static void refresh(RepositoryBase Repository, SceneDataObject sdo, int depth, Date currTime) throws Exception {
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

    private static void refresh(RepositoryBase Repository, SceneDataValue sdv, int depth, Date currTime, boolean use_offset_level) throws Exception {
        if (sdv == null) {
            return;
        }
        // String svPath = RecursiveUtil.getDataPath(sdv);
        // log.info("refreshValue" + "\t" + svPath);

        int curr_depth = depth;
        if (use_offset_level && sdv.rel_property != null && curr_depth != -1) {
            curr_depth -= Integer.parseInt(sdv.rel_property.offset_level);
            if (curr_depth < 0) {
                return;
            }
        }

        // 计算当前节点
        compute(Repository, sdv);
        // 计算下级节点
        if (depth > 0 || depth == -1) {
            if (sdv.value_object != null) {
                refresh(Repository, sdv.value_object, curr_depth, currTime);
            } else if (sdv.value_array != null) {
                if (!sdv.value_array.isSingleValueSet) {
                    for (int i = 0; i < sdv.value_array.set.size(); i++) {
                        SceneDataObject sdb = sdv.value_array.set.get(i);
                        if (sdb != null) {
                            refresh(Repository, (SceneDataObject) sdb, curr_depth, currTime);
                        }
                    }
                }
            }
        }
    }

    // 计算节点
    private static void compute(RepositoryBase Repository, SceneDataValue sdv) throws Exception {
        if (sdv == null) {
            return;
        }

        if (sdv.rel_property != null && sdv.rel_property.propertyValueType.equals("query")) {
            if (sdv.rel_property.propertyValueSchema.equals("JSONObject") || sdv.rel_property.propertyValueSchema.equals("JSONArray")) {
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
    private static void computeInner(RepositoryBase Repository, SceneDataValue sv) throws Exception {
        SceneProperty sceneProperty = sv.rel_property;
        JSONObject sql_json = JSON.parseObject(sceneProperty.query_sql);
        Map<String, Map<String, Boolean>> refList = new ConcurrentHashMap<String, Map<String, Boolean>>();
        CheckUtil.query(sql_json, refList);
        List<SceneDataValue> svListAll = new CopyOnWriteArrayList<SceneDataValue>();
        for (String refString : refList.keySet()) {
            String[] splits = refString.split("'");
            Object parentData;
            int splits_index;
            if (splits[0].startsWith("ancestor_")) {
                int generate = Integer.parseInt(splits[0].substring("ancestor_".length()));
                Object tmp = sv;
                while (generate > 0) {
                    if (tmp instanceof SceneDataValue) {
                        SceneDataValue tmpData = (SceneDataValue) tmp;
                        tmp = tmpData.parentObjectData;
                    } else if (tmp != null) {
                        SceneDataObject tmpData = (SceneDataObject) tmp;
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
            List<SceneDataValue> svList = new CopyOnWriteArrayList<SceneDataValue>();
            if (parentData instanceof SceneDataValue) {
                SceneDataValue tmpData = (SceneDataValue) parentData;
                svList.add(tmpData);
            } else if (parentData != null) {
                SceneDataObject tmpData = (SceneDataObject) parentData;
                SceneDataValue svWrapper = new SceneDataValue(null, null, null, null);
                svWrapper.value_object = tmpData;
                svList.add(svWrapper);
            }
            for (int i = splits_index; i < splits.length; i++) {
                List<SceneDataValue> svListInner = new CopyOnWriteArrayList<SceneDataValue>();
                String split = splits[i];
                int index_ = split.indexOf('=');
                if (index_ != -1) {
                    String propertyName = split.substring(0, index_);
                    String propertyValue = split.substring(index_ + 1);
                    for (SceneDataValue svInner : svList) {
                        if (svInner.value_object != null) {
                            SceneDataObject sod = svInner.value_object;
                            if (sod.containsKey(propertyName) && propertyValue.equals(sod.get(propertyName).value_prim.value)) {
                                SceneDataValue svWrapper = new SceneDataValue(null, sod, propertyName, null);
                                svWrapper.value_object = sod;
                                svListInner.add(svWrapper);
                            }
                        } else if (svInner.value_array != null) {
                            for (SceneDataObject sod : svInner.value_array.set) {
                                if (sod.containsKey(propertyName) && propertyValue.equals(sod.get(propertyName).value_prim.value)) {
                                    SceneDataValue svWrapper = new SceneDataValue(null, sod, propertyName, null);
                                    svWrapper.value_object = sod;
                                    svListInner.add(svWrapper);
                                }
                            }
                        }
                    }
                } else {
                    for (SceneDataValue svInner : svList) {
                        if (svInner.value_object != null) {
                            svListInner.add(svInner.value_object.get(split));
                        } else if (svInner.value_array != null) {
                            for (SceneDataObject sdb : svInner.value_array.set) {
                                if (sdb != null) {
                                    SceneDataObject sod = (SceneDataObject) sdb;
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

        for (SceneDataValue sdv : svListAll) {
            compute(Repository, sdv);
        }

        if (sv.rel_property != null && sv.rel_property.propertyValueType.equals("query")) {
            if (sv.value_object != null && sv.value_object.getRowChange() || sv.value_array != null && sv.value_array.getRowChange()
                    || sv.value_prim != null && sv.value_prim.change) {
                ReentrantLock lock = sv.lock;
                try {
                    lock.lock();
                    Date currTime = new Date();
                    if (sv.last_compute_time == null || currTime.getTime() - sv.last_compute_time.getTime() > 1000L) {
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

    public static void print_variable_set(String path, SceneDataObject sdo) {
        if (sdo == null) {
            return;
        }
        if (sdo.rel_object == null && sdo.parentArrayData == null) {
            return;
        }

        for (String key : sdo.keySet()) {
            if (AttributeFilteringUtil.containsKey(key)) {
                continue;
            }
            print_variable_set(path + "-" + key, sdo.get(key));
        }
    }

    public static void print_variable_set(String path, SceneDataValue sdv) {
        if (sdv == null) {
            return;
        }
        if (sdv.rel_property == null) {
            return;
        }

        if (sdv.rel_property.propertyValueSchema.equals("JSONObject")) {
            if (sdv.rel_property.propertyValueType.equals("query")) {
                log.debug(
                        path + "\t" + "variable set object" + "\t" + sdv.rel_property.propertyValueType + "\t" + sdv.value_object.change.toString());
            }
            print_variable_set(path, sdv.value_object);
        } else if (sdv.rel_property.propertyValueSchema.equals("JSONArray")) {
            if (sdv.rel_property.propertyValueType.equals("query")) {
                log.debug(path + "\t" + "variable set array" + "\t" + sdv.rel_property.propertyValueType + "\t" + sdv.value_array.change.toString());
            }
            for (int i = 0; i < sdv.value_array.set.size(); i++) {
                SceneDataObject sdb = sdv.value_array.set.get(i);
                if (sdb != null) {
                    print_variable_set(path + "[" + i + "]", sdb);
                }
            }
        } else {
            if (sdv.rel_property.propertyValueType.equals("query")) {
                {
                    log.debug(path + "\t" + "variable value" + "\t" + sdv.value_prim.change);
                }
            }
        }
    }
}
