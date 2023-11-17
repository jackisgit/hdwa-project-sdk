package com.hdwa.sdk.entity.repository;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hdwa.sdk.entity.scene.DataSet;
import com.hdwa.sdk.entity.scene.DataValue;
import com.hdwa.sdk.utils.PathUtil;

import java.util.HashMap;
import java.util.Map;

public class InfluenceFactor {
    public Map<DataSet, Boolean> rowChange = new HashMap<>(16);
    public Map<DataSet, Map<String, Boolean>> colChange = new HashMap<>(16);
    public Map<DataValue, Boolean> valueChange = new HashMap<>(16);

    public void merge(InfluenceFactor other) {
        for (DataSet key : other.rowChange.keySet()) {
            this.rowChange.put(key, true);
        }
        for (DataSet key : other.colChange.keySet()) {
            this.colChange.putIfAbsent(key, new HashMap<>(16));
            for (String col : other.colChange.get(key).keySet()) {
                this.colChange.get(key).put(col, true);
            }
        }
        for (DataValue key : other.valueChange.keySet()) {
            this.valueChange.put(key, true);
        }
    }

    public Object toJSON() throws Exception {
        JSONObject result = new JSONObject();
        if (rowChange.size() > 0) {
            JSONArray row = new JSONArray();
            for (DataSet key : rowChange.keySet()) {
                row.add(key.path);
            }
            result.put("row", row);
        }
        if (colChange.size() > 0) {
            JSONArray col = new JSONArray();
            for (DataSet key : colChange.keySet()) {
                Map<String, Boolean> colMap = colChange.get(key);
                for (String Column : colMap.keySet()) {
                    col.add(key.path + " : " + Column);
                }
            }
            result.put("col", col);
        }
        if (valueChange.size() > 0) {
            JSONArray value = new JSONArray();
            for (DataValue key : valueChange.keySet()) {
                value.add(PathUtil.getDataPath(key));
            }
            result.put("value", value);
        }
        return result;
    }
}
