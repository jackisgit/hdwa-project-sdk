package com.hdwa.sdk.entity.scene;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.serializer.SerializerFeature;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author abao
 * @since 2023/7/25
 * 场景对象集合
 */
public class DataSet {

    public boolean isSingleValueSet = false;
    public List<DataObject> set = new ArrayList<>();
    public List<DataValue> singleValueSet = new ArrayList<>();
    public DataChange dataChange = new DataChange();
    public String path;

    public DataSet(boolean isSingleValueSet) {
        this.init(isSingleValueSet, false, null);
    }

    public DataSet(boolean isSingleValueSet, String path) {
        this.init(isSingleValueSet, false, path);
    }

    public DataSet(boolean isSingleValueSet, boolean rowChange) {
        this.init(isSingleValueSet, rowChange, null);
    }

    public void init(boolean isSingleValueSet, boolean rowChange, String path) {
        this.isSingleValueSet = isSingleValueSet;
        if (this.isSingleValueSet) {
            this.singleValueSet = new CopyOnWriteArrayList<>();
        } else {
            this.set = new CopyOnWriteArrayList<>();
        }

        this.setRowChange(rowChange);
        this.path = path;
    }

    public boolean getRowChange() {
        return this.dataChange.rowChange;
    }

    public void setRowChange(boolean value) {
        this.dataChange.rowChange = value;
    }

    public Map<String, Boolean> getColChange() {
        Map<String, Boolean> result = this.dataChange.colChangeMap;
        return result;
    }

    public void setColChange(String col) {
        this.dataChange.colChangeMap.put(col, true);
    }

    public void setColChange(Map<String, Boolean> colMap) {
        for (String col : colMap.keySet()) {
            this.dataChange.colChangeMap.put(col, true);
        }
    }

    public boolean hasColChange(String col) {
        return this.dataChange.colChangeMap.containsKey(col);
    }

    public JSONArray toJSON(int depth) {
        return this.toJSON(depth, false);
    }

    public JSONArray toJSON(int depth, boolean with_change) {
        JSONArray result = new JSONArray();
        if (with_change) {
            result.add(this.getRowChange());
        }
        if (this.isSingleValueSet) {
            for (DataValue sdv : this.singleValueSet) {
                result.add(sdv.valuePrim.value);
            }
        } else {
            for (DataObject sod : this.set) {
                // 根据信息点是否显示过滤设备类型
                if (sod.relObject != null && sod.relObject.allowPass.equals("0")) {
                    continue;
                }
                result.add(sod.toJSON(depth, with_change));
            }
        }
        return result;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this.toJSON(1), SerializerFeature.WriteMapNullValue);
    }
}
