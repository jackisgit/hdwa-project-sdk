package com.hdwa.sdk.entity.repository;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hdwa.sdk.entity.scene.SceneDataSet;
import com.hdwa.sdk.entity.scene.SceneDataValue;
import com.hdwa.sdk.utils.PathUtil;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InfluenceFactor {
    public Map<SceneDataSet, Boolean> rowChange = new ConcurrentHashMap<SceneDataSet, Boolean>();
    public Map<SceneDataSet, Map<String, Boolean>> colChange = new ConcurrentHashMap<SceneDataSet, Map<String, Boolean>>();
    public Map<SceneDataValue, Boolean> valueChange = new ConcurrentHashMap<SceneDataValue, Boolean>();

    public void merge(InfluenceFactor other) {
        for (SceneDataSet key : other.rowChange.keySet()) {
            this.rowChange.put(key, true);
        }
        for (SceneDataSet key : other.colChange.keySet()) {
            this.colChange.putIfAbsent(key, new ConcurrentHashMap<String, Boolean>());
            for (String col : other.colChange.get(key).keySet()) {
                this.colChange.get(key).put(col, true);
            }
        }
        for (SceneDataValue key : other.valueChange.keySet()) {
            this.valueChange.put(key, true);
        }
    }

    public Object toJSON() throws Exception {
        JSONObject result = new JSONObject();
        if (rowChange.size() > 0) {
            JSONArray row = new JSONArray();
            for (SceneDataSet key : rowChange.keySet()) {
                row.add(key.path);
            }
            result.put("row", row);
        }
        if (colChange.size() > 0) {
            JSONArray col = new JSONArray();
            for (SceneDataSet key : colChange.keySet()) {
                Map<String, Boolean> colMap = colChange.get(key);
                for (String Column : colMap.keySet()) {
                    col.add(key.path + " : " + Column);
                }
            }
            result.put("col", col);
        }
        if (valueChange.size() > 0) {
            JSONArray value = new JSONArray();
            for (SceneDataValue key : valueChange.keySet()) {
                value.add(PathUtil.getDataPath(key));
            }
            result.put("value", value);
        }
        return result;
    }
}
