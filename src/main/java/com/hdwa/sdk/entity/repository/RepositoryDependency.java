package com.hdwa.sdk.entity.repository;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hdwa.sdk.entity.scene.SceneDataObject;
import com.hdwa.sdk.entity.scene.SceneDataPrimitive;
import com.hdwa.sdk.entity.scene.SceneDataSet;
import com.hdwa.sdk.entity.scene.SceneDataValue;
import com.hdwa.sdk.utils.PathUtil;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class RepositoryDependency {
    public Map<SceneDataValue, Map<SceneDataValue, Boolean>> sdv2sdv = new ConcurrentHashMap<>(16);
    public Map<SceneDataValue, Map<SceneDataSet, Map<String, Boolean>>> sdv2SetColumn = new ConcurrentHashMap<>(16);
    public Map<SceneDataSet, Map<SceneDataValue, Boolean>> SetRow2sdv = new ConcurrentHashMap<>();
    public Map<SceneDataSet, Map<String, Map<SceneDataValue, Boolean>>> SetColumn2sdv = new ConcurrentHashMap<>(16);

    public Map<SceneDataObject, CopyOnWriteArrayList<SceneDataObject>> sdv2Children = new ConcurrentHashMap<>(16);

    public void clear() {
        sdv2sdv = new ConcurrentHashMap<>();
        sdv2SetColumn = new ConcurrentHashMap<>();
        SetRow2sdv = new ConcurrentHashMap<>();
        SetColumn2sdv = new ConcurrentHashMap<>();

        sdv2Children.clear();
    }

    public void add_sdv2SetColumn(SceneDataValue point_value, SceneDataSet objectArray, String Column) {
        sdv2SetColumn.putIfAbsent(point_value, new ConcurrentHashMap<>());
        sdv2SetColumn.get(point_value).putIfAbsent(objectArray, new ConcurrentHashMap<>());
        sdv2SetColumn.get(point_value).get(objectArray).putIfAbsent(Column, true);
    }

    public void add_compute(SceneDataValue sdv) {
        InfluenceFactor other = sdv.rowFactor;
        for (SceneDataSet key : other.rowChange.keySet()) {
            SetRow2sdv.putIfAbsent(key, new ConcurrentHashMap<>());
            SetRow2sdv.get(key).putIfAbsent(sdv, true);
        }
        for (SceneDataSet key : other.colChange.keySet()) {
            SetColumn2sdv.putIfAbsent(key, new ConcurrentHashMap<>());
            for (String col : other.colChange.get(key).keySet()) {
                SetColumn2sdv.get(key).putIfAbsent(col, new ConcurrentHashMap<>());
                SetColumn2sdv.get(key).get(col).putIfAbsent(sdv, true);
            }
        }
        for (SceneDataValue key : other.valueChange.keySet()) {
            sdv2sdv.putIfAbsent(key, new ConcurrentHashMap<SceneDataValue, Boolean>());
            sdv2sdv.get(key).putIfAbsent(sdv, true);
        }
        // 考虑附加属性
        if (sdv.parentObjectData.parentArrayData != null && sdv.parentObjectData.parentArrayData.rel_property.propertyValueType.equals("query")) {
            sdv2SetColumn.putIfAbsent(sdv, new ConcurrentHashMap<>());
            sdv2SetColumn.get(sdv).putIfAbsent(sdv.parentObjectData.parentArrayData.value_array, new ConcurrentHashMap<>());
            sdv2SetColumn.get(sdv).get(sdv.parentObjectData.parentArrayData.value_array).putIfAbsent(sdv.myPropertyName, true);
        }
    }

    public JSONObject get_before(RepositoryBase Repository, SceneDataValue sdv) throws Exception {
        JSONObject result = new JSONObject();
        InfluenceFactor other = sdv.rowFactor;
        {
            JSONArray resultItem = new JSONArray();
            for (SceneDataSet key : other.rowChange.keySet()) {
                resultItem.add(key.path);
            }
            result.put("RowAffect", resultItem);
        }
        {
            JSONArray resultItem = new JSONArray();
            for (SceneDataSet key : other.colChange.keySet()) {
                JSONObject itemInner = new JSONObject();
                itemInner.put("path", key.path);
                JSONArray arrayInner = new JSONArray();
                arrayInner.addAll(other.colChange.get(key).keySet());
                itemInner.put("cols", arrayInner);
                resultItem.add(itemInner);
            }
            result.put("ColumnAffect", resultItem);
        }
        {
            JSONArray resultItem = new JSONArray();
            for (SceneDataValue key : other.valueChange.keySet()) {
                String path = PathUtil.getDataPath(key);
                if (path.length() == 0) {
                    ConcurrentHashMap<SceneDataPrimitive, String> sdv2point = Repository.sdv2point();
                    ConcurrentHashMap<SceneDataPrimitive, String> sdv2set = Repository.sdv2set();
                    if (sdv2point.containsKey(key.value_prim)) {
                        resultItem.add("point: " + sdv2point.get(key.value_prim));
                    } else if (sdv2set.containsKey(key.value_prim)) {
                        resultItem.add("set: " + sdv2set.get(key.value_prim));
                    }
                } else {
                    resultItem.add(path);
                }
            }
            result.put("ValueAffect", resultItem);
        }
        return result;
    }

    public void get_after_value_array(SceneDataSet value_array, List<SceneDataValue> sdvAffectList, JSONObject result) throws Exception {
        if (SetRow2sdv.containsKey(value_array)) {
            Map<SceneDataValue, Boolean> afterList = SetRow2sdv.get(value_array);
            JSONArray resultItem = new JSONArray();
            for (SceneDataValue sdvInner : afterList.keySet()) {
                resultItem.add(PathUtil.getDataPath(sdvInner));
                sdvAffectList.add(sdvInner);
            }
            result.put("RowAffect", resultItem);
        }
        if (SetColumn2sdv.containsKey(value_array)) {
            Map<String, Map<SceneDataValue, Boolean>> column_afterList = SetColumn2sdv.get(value_array);
            JSONArray resultItem = new JSONArray();
            for (String Column : column_afterList.keySet()) {
                JSONObject columnItem = new JSONObject();
                columnItem.put("Column", Column);
                Map<SceneDataValue, Boolean> afterList = column_afterList.get(Column);
                JSONArray itemInner = new JSONArray();
                for (SceneDataValue sdvInner : afterList.keySet()) {
                    itemInner.add(PathUtil.getDataPath(sdvInner));
                    // sdvAffectList.add(sdvInner);
                }
                columnItem.put("sdvList", itemInner);
                resultItem.add(columnItem);
            }
            result.put("ColumnAffect", resultItem);
        }
    }

    public void get_after_value_prim(RepositoryBase Repository, SceneDataValue sdv, List<SceneDataValue> sdvAffectList, JSONObject result)
            throws Exception {
        List<String> sdvList = new CopyOnWriteArrayList<String>();
        if (sdv2sdv.containsKey(sdv)) {
            Map<SceneDataValue, Boolean> afterList = sdv2sdv.get(sdv);
            for (SceneDataValue sdvInner : afterList.keySet()) {
                sdvList.add(PathUtil.getDataPath(sdvInner));
                sdvAffectList.add(sdvInner);
            }
        }
        Map<String, Boolean> SetColumnList = new ConcurrentHashMap<String, Boolean>();
        Map<String, JSONObject> SetColumnMap = new ConcurrentHashMap<String, JSONObject>();
        if (sdv2SetColumn.containsKey(sdv)) {
            Map<SceneDataSet, Map<String, Boolean>> afterList = sdv2SetColumn.get(sdv);
            for (SceneDataSet sdvInner : afterList.keySet()) {
                Map<String, Boolean> columnMap = afterList.get(sdvInner);
                for (String column : columnMap.keySet()) {
                    SetColumnList.put(sdvInner.path + " : " + column, true);
                }
            }
        }
        // 根据上级object间接关联
        if (sdv.parentObjectData != null) {
            Queue<SceneDataObject> queue = new LinkedList<SceneDataObject>();
            queue.offer(sdv.parentObjectData);
            while (queue.size() > 0) {
                SceneDataObject sdoTmp = queue.poll();
                if (!sdoTmp.containsKey(sdv.myPropertyName)) {
                    continue;
                }
                if (sdv2Children.containsKey(sdoTmp)) {
                    for (SceneDataObject child : sdv2Children.get(sdoTmp)) {
                        queue.offer(child);
                    }
                }
                SceneDataValue parentSet = sdoTmp.parentArrayData;
                if (parentSet == null) {
                    continue;
                }
                String path = PathUtil.getDataPath(parentSet);
                if (path.length() == 0) {
                    path = parentSet.value_array.path;
                }
                JSONObject SetColumnJSON = new JSONObject();
                SetColumnJSON.put("SetColumn", path + " : " + sdv.myPropertyName);
                SetColumnList.put(path + " : " + sdv.myPropertyName, true);
                List<String> otherList = new CopyOnWriteArrayList<String>();
                if (SetColumn2sdv.containsKey(parentSet.value_array)) {
                    Map<String, Map<SceneDataValue, Boolean>> Column2sdv = SetColumn2sdv.get(parentSet.value_array);
                    if (Column2sdv.containsKey(sdv.myPropertyName)) {
                        Map<SceneDataValue, Boolean> sdvListInner = Column2sdv.get(sdv.myPropertyName);
                        for (SceneDataValue sdvInner : sdvListInner.keySet()) {
                            otherList.add(PathUtil.getDataPath(sdvInner));
                            sdvAffectList.add(sdvInner);
                        }
                    }
                }
                Collections.sort(otherList);
                SetColumnJSON.put("otherList", otherList);
                SetColumnMap.put(path + " : " + sdv.myPropertyName, SetColumnJSON);
            }
        }
        Collections.sort(sdvList);
        String[] SetColumnListArray = SetColumnList.keySet().toArray(new String[0]);
        Arrays.sort(SetColumnListArray);
        result.put("sdvList", sdvList);
        JSONArray SetColumnArray = new JSONArray();
        for (String SetColumn : SetColumnListArray) {
            if (SetColumnMap.containsKey(SetColumn)) {
                SetColumnArray.add(SetColumnMap.get(SetColumn));
            } else {
                SetColumnArray.add(SetColumn);
            }
        }
        result.put("SetColumnList", SetColumnArray);

    }

    public void get_after(RepositoryBase Repository, SceneDataValue sdv, List<SceneDataValue> sdvAffectList) throws Exception {
        JSONObject result = new JSONObject();

        if (sdv.value_array != null) {
            this.get_after_value_array(sdv.value_array, sdvAffectList, result);
        } else if (sdv.value_prim != null) {
            this.get_after_value_prim(Repository, sdv, sdvAffectList, result);
        }
    }
}
