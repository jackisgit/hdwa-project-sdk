package com.hdwa.sdk.entity.repository;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hdwa.sdk.entity.scene.DataObject;
import com.hdwa.sdk.entity.scene.DataPrimitive;
import com.hdwa.sdk.entity.scene.DataSet;
import com.hdwa.sdk.entity.scene.DataValue;
import com.hdwa.sdk.utils.PathUtil;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class RepositoryDependency {
    public Map<DataValue, Map<DataValue, Boolean>> sdv2sdv = new ConcurrentHashMap<>(16);
    public Map<DataValue, Map<DataSet, Map<String, Boolean>>> sdv2SetColumn = new ConcurrentHashMap<>(16);
    public Map<DataSet, Map<DataValue, Boolean>> SetRow2sdv = new ConcurrentHashMap<>();
    public Map<DataSet, Map<String, Map<DataValue, Boolean>>> SetColumn2sdv = new ConcurrentHashMap<>(16);

    public Map<DataObject, CopyOnWriteArrayList<DataObject>> sdv2Children = new ConcurrentHashMap<>(16);

    public void clear() {
        sdv2sdv = new ConcurrentHashMap<>();
        sdv2SetColumn = new ConcurrentHashMap<>();
        SetRow2sdv = new ConcurrentHashMap<>();
        SetColumn2sdv = new ConcurrentHashMap<>();

        sdv2Children.clear();
    }

    public void add_sdv2SetColumn(DataValue point_value, DataSet objectArray, String Column) {
        sdv2SetColumn.putIfAbsent(point_value, new ConcurrentHashMap<>());
        sdv2SetColumn.get(point_value).putIfAbsent(objectArray, new ConcurrentHashMap<>());
        sdv2SetColumn.get(point_value).get(objectArray).putIfAbsent(Column, true);
    }

    public void add_compute(DataValue sdv) {
        InfluenceFactor other = sdv.rowFactor;
        for (DataSet key : other.rowChange.keySet()) {
            SetRow2sdv.putIfAbsent(key, new ConcurrentHashMap<>());
            SetRow2sdv.get(key).putIfAbsent(sdv, true);
        }
        for (DataSet key : other.colChange.keySet()) {
            SetColumn2sdv.putIfAbsent(key, new ConcurrentHashMap<>());
            for (String col : other.colChange.get(key).keySet()) {
                SetColumn2sdv.get(key).putIfAbsent(col, new ConcurrentHashMap<>());
                SetColumn2sdv.get(key).get(col).putIfAbsent(sdv, true);
            }
        }
        for (DataValue key : other.valueChange.keySet()) {
            sdv2sdv.putIfAbsent(key, new ConcurrentHashMap<DataValue, Boolean>());
            sdv2sdv.get(key).putIfAbsent(sdv, true);
        }
        // 考虑附加属性
        if (sdv.parentObjectData.parentArrayData != null && sdv.parentObjectData.parentArrayData.relProperty.propertyValueType.equals("query")) {
            sdv2SetColumn.putIfAbsent(sdv, new ConcurrentHashMap<>());
            sdv2SetColumn.get(sdv).putIfAbsent(sdv.parentObjectData.parentArrayData.valueArray, new ConcurrentHashMap<>());
            sdv2SetColumn.get(sdv).get(sdv.parentObjectData.parentArrayData.valueArray).putIfAbsent(sdv.myPropertyName, true);
        }
    }

    public JSONObject get_before(RepositoryBase Repository, DataValue sdv) throws Exception {
        JSONObject result = new JSONObject();
        InfluenceFactor other = sdv.rowFactor;
        {
            JSONArray resultItem = new JSONArray();
            for (DataSet key : other.rowChange.keySet()) {
                resultItem.add(key.path);
            }
            result.put("RowAffect", resultItem);
        }
        {
            JSONArray resultItem = new JSONArray();
            for (DataSet key : other.colChange.keySet()) {
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
            for (DataValue key : other.valueChange.keySet()) {
                String path = PathUtil.getDataPath(key);
                if (path.length() == 0) {
                    ConcurrentHashMap<DataPrimitive, String> sdv2point = Repository.sdv2point();
                    ConcurrentHashMap<DataPrimitive, String> sdv2set = Repository.sdv2set();
                    if (sdv2point.containsKey(key.valuePrim)) {
                        resultItem.add("point: " + sdv2point.get(key.valuePrim));
                    } else if (sdv2set.containsKey(key.valuePrim)) {
                        resultItem.add("set: " + sdv2set.get(key.valuePrim));
                    }
                } else {
                    resultItem.add(path);
                }
            }
            result.put("ValueAffect", resultItem);
        }
        return result;
    }

    public void get_after_value_array(DataSet value_array, List<DataValue> sdvAffectList, JSONObject result) throws Exception {
        if (SetRow2sdv.containsKey(value_array)) {
            Map<DataValue, Boolean> afterList = SetRow2sdv.get(value_array);
            JSONArray resultItem = new JSONArray();
            for (DataValue sdvInner : afterList.keySet()) {
                resultItem.add(PathUtil.getDataPath(sdvInner));
                sdvAffectList.add(sdvInner);
            }
            result.put("RowAffect", resultItem);
        }
        if (SetColumn2sdv.containsKey(value_array)) {
            Map<String, Map<DataValue, Boolean>> column_afterList = SetColumn2sdv.get(value_array);
            JSONArray resultItem = new JSONArray();
            for (String Column : column_afterList.keySet()) {
                JSONObject columnItem = new JSONObject();
                columnItem.put("Column", Column);
                Map<DataValue, Boolean> afterList = column_afterList.get(Column);
                JSONArray itemInner = new JSONArray();
                for (DataValue sdvInner : afterList.keySet()) {
                    itemInner.add(PathUtil.getDataPath(sdvInner));
                    // sdvAffectList.add(sdvInner);
                }
                columnItem.put("sdvList", itemInner);
                resultItem.add(columnItem);
            }
            result.put("ColumnAffect", resultItem);
        }
    }

    public void get_after_value_prim(RepositoryBase Repository, DataValue sdv, List<DataValue> sdvAffectList, JSONObject result)
            throws Exception {
        List<String> sdvList = new CopyOnWriteArrayList<String>();
        if (sdv2sdv.containsKey(sdv)) {
            Map<DataValue, Boolean> afterList = sdv2sdv.get(sdv);
            for (DataValue sdvInner : afterList.keySet()) {
                sdvList.add(PathUtil.getDataPath(sdvInner));
                sdvAffectList.add(sdvInner);
            }
        }
        Map<String, Boolean> SetColumnList = new ConcurrentHashMap<String, Boolean>();
        Map<String, JSONObject> SetColumnMap = new ConcurrentHashMap<String, JSONObject>();
        if (sdv2SetColumn.containsKey(sdv)) {
            Map<DataSet, Map<String, Boolean>> afterList = sdv2SetColumn.get(sdv);
            for (DataSet sdvInner : afterList.keySet()) {
                Map<String, Boolean> columnMap = afterList.get(sdvInner);
                for (String column : columnMap.keySet()) {
                    SetColumnList.put(sdvInner.path + " : " + column, true);
                }
            }
        }
        // 根据上级object间接关联
        if (sdv.parentObjectData != null) {
            Queue<DataObject> queue = new LinkedList<DataObject>();
            queue.offer(sdv.parentObjectData);
            while (queue.size() > 0) {
                DataObject sdoTmp = queue.poll();
                if (!sdoTmp.containsKey(sdv.myPropertyName)) {
                    continue;
                }
                if (sdv2Children.containsKey(sdoTmp)) {
                    for (DataObject child : sdv2Children.get(sdoTmp)) {
                        queue.offer(child);
                    }
                }
                DataValue parentSet = sdoTmp.parentArrayData;
                if (parentSet == null) {
                    continue;
                }
                String path = PathUtil.getDataPath(parentSet);
                if (path.length() == 0) {
                    path = parentSet.valueArray.path;
                }
                JSONObject SetColumnJSON = new JSONObject();
                SetColumnJSON.put("SetColumn", path + " : " + sdv.myPropertyName);
                SetColumnList.put(path + " : " + sdv.myPropertyName, true);
                List<String> otherList = new CopyOnWriteArrayList<String>();
                if (SetColumn2sdv.containsKey(parentSet.valueArray)) {
                    Map<String, Map<DataValue, Boolean>> Column2sdv = SetColumn2sdv.get(parentSet.valueArray);
                    if (Column2sdv.containsKey(sdv.myPropertyName)) {
                        Map<DataValue, Boolean> sdvListInner = Column2sdv.get(sdv.myPropertyName);
                        for (DataValue sdvInner : sdvListInner.keySet()) {
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

    public void get_after(RepositoryBase Repository, DataValue sdv, List<DataValue> sdvAffectList) throws Exception {
        JSONObject result = new JSONObject();

        if (sdv.valueArray != null) {
            this.get_after_value_array(sdv.valueArray, sdvAffectList, result);
        } else if (sdv.valuePrim != null) {
            this.get_after_value_prim(Repository, sdv, sdvAffectList, result);
        }
    }
}
