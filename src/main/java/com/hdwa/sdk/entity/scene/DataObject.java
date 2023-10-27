package com.hdwa.sdk.entity.scene;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.hdwa.sdk.entity.repository.RepositoryBase;
import com.hdwa.sdk.utils.AttributeFilteringUtil;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author abao
 * @since 2023/8/25
 * 场景对象类
 */
public class DataObject {

    public DataObject parentObjectData;
    public String myPropertyName;
    public DataValue parentArrayData;
    public DataObjectBase relObject;
    public DataProperty[] queryAttached;
    public Map<String, DataValue> valueObject;
    public DataChange dataChange = new DataChange();
    public DataObject father;
    public Map<String, Boolean> fatherReturnColumnMap;

    public DataObject() {
    }

    public DataObject(RepositoryBase Repository, DataValue parentArrayData, DataObjectBase custom_object, DataProperty[] queryAttached) {
        this.parentArrayData = parentArrayData;
        this.relObject = custom_object;
        this.queryAttached = queryAttached;

        this.valueObject = new HashMap<>(16);
        if (this.relObject != null) {
            for (DataProperty dataProperty : this.relObject.propertyList) {
                DataValue sdv = new DataValue(Repository, this, dataProperty.propertyName, dataProperty);
                this.valueObject.put(dataProperty.propertyName, sdv);
            }
        }
        if (this.queryAttached != null) {
            for (DataProperty dataProperty : this.queryAttached) {
                this.valueObject.put(dataProperty.propertyName, new DataValue(Repository, this, dataProperty.propertyName, dataProperty));
            }
        }
    }


    public DataObject(RepositoryBase Repository, DataObject parentObjectData, String myPropertyName, DataValue parentArrayData,
                      DataObjectBase custom_object, DataProperty[] queryAttached, DataObject father) {
        this.parentObjectData = parentObjectData;
        this.myPropertyName = myPropertyName;
        this.parentArrayData = parentArrayData;
        this.relObject = custom_object;
        this.queryAttached = queryAttached;
        this.father = father;

        this.valueObject = new HashMap<>(16);
        if (this.relObject != null) {
            for (DataProperty dataProperty : this.relObject.propertyList) {
                this.valueObject.put(dataProperty.propertyName, new DataValue(Repository, this, dataProperty.propertyName, dataProperty));
            }
        }
        if (this.queryAttached != null) {
            for (DataProperty dataProperty : this.queryAttached) {
                this.valueObject.put(dataProperty.propertyName, new DataValue(Repository, this, dataProperty.propertyName, dataProperty));
            }
        }
    }

    public Object toJSON(int depth) {
        return this.toJSON(depth, false);
    }

    public Object toJSON(int depth, boolean with_change) {
        // 根据信息点是否显示过滤设备类型
        if (this.relObject != null && this.relObject.allowPass.equals("0")) {
            return null;
        }

        int depthInner = depth == -1 ? -1 : (depth > 0 ? depth - 1 : 0);
        JSONObject result = new JSONObject();
        if (with_change) {
            result.put("sailfish_inner_RowChange", this.getRowChange());
        }
        for (String key : this.keySet()) {
            if (AttributeFilteringUtil.containsKey(key)) {
                continue;
            }
            DataValue sdvInner = this.get(key);
            if (sdvInner != null) {
                result.put(key, sdvInner.toJSON(false, depthInner, with_change));
            }
        }
        return result;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this.toJSON(1), SerializerFeature.WriteMapNullValue);
    }

    public Set<String> keySetSelf() {
        return new HashSet<>(this.valueObject.keySet());
    }

    public Set<String> keySet() {
        Set<String> result = new HashSet<>(this.valueObject.keySet());
        if (this.father != null) {
            for (String key : this.father.keySet()) {
                if (this.fatherReturnColumnMap == null || this.fatherReturnColumnMap.containsKey(key)) {
                    result.add(key);
                }
            }
        }
        return result;
    }

    public boolean containsKey(String key) {
        if (this.valueObject.containsKey(key)) {
            return true;
        }
        if (this.father != null) {
            if (this.fatherReturnColumnMap == null || this.fatherReturnColumnMap.containsKey(key)) {
                return this.father.containsKey(key);
            }
        }
        return false;
    }

    public DataValue get(String key) {
        if (this.valueObject.containsKey(key)) {
            return this.valueObject.get(key);
        }
        if (this.father != null) {
            if (this.fatherReturnColumnMap == null || this.fatherReturnColumnMap.containsKey(key)) {
                if (this.father.containsKey(key)) {
                    return this.father.get(key);
                }
            }
        }
        return null;
    }

    public void put(String key, DataValue value) {
        this.valueObject.put(key, value);
    }

    public void remove(String key) {
        this.valueObject.remove(key);
    }

    public int size() {
        int result = 0;
        if (this.father != null) {
            result += this.father.size();
        }
        result += this.valueObject.size();
        return result;
    }

    public boolean getRowChange() {
        if (this.father != null) {
            return this.father.getRowChange();
        } else {
            return this.dataChange.rowChange;
        }
    }

    public void setRowChange(boolean value) {
        this.dataChange.rowChange = value;
    }

    public Map<String, Boolean> getColChange() {
        Map<String, Boolean> result;
        if (this.father != null) {
            result = new ConcurrentHashMap<>();
            Map<String, Boolean> result_father = this.father.getColChange();
            {
                for (String col : result_father.keySet()) {
                    result.put(col, true);
                }
            }
            {
                for (String col : this.dataChange.colChangeMap.keySet()) {
                    result.put(col, true);
                }
            }
        } else {
            result = this.dataChange.colChangeMap;
        }
        return result;
    }

    public void setColChange(String col) {
        if (!this.hasColChange(col)) {
            this.dataChange.colChangeMap.put(col, true);
        }
    }

    public void setColChange(Map<String, Boolean> colMap) {
        for (String col : colMap.keySet()) {
            if (!this.hasColChange(col)) {
                this.dataChange.colChangeMap.put(col, true);
            }
        }
    }

    public boolean hasColChange(String col) {
        if (this.father != null) {
            if (this.father.hasColChange(col)) {
                return true;
            }
        }
        return this.dataChange.colChangeMap.containsKey(col);
    }
}
