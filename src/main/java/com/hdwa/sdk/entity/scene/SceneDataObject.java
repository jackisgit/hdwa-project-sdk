package com.hdwa.sdk.entity.scene;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.hdwa.sdk.entity.repository.RepositoryBase;
import com.hdwa.sdk.utils.KeywordUtil;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class SceneDataObject {
    public SceneDataObject parentObjectData;
    public String myPropertyName;
    public SceneDataValue parentArrayData;

    public SceneObject rel_object;
    public SceneProperty[] query_attached;

    public Map<String, SceneDataValue> value_object;
    public Change change = new Change();

    public SceneDataObject father;
    public Map<String, Boolean> fatherReturnColumnMap;

    public SceneDataObject() {
    }

    public SceneDataObject(RepositoryBase Repository, SceneDataValue parentArrayData,SceneObject custom_object, SceneProperty[] query_attached) {
        this.parentArrayData = parentArrayData;
        this.rel_object = custom_object;
        this.query_attached = query_attached;

        this.value_object = new HashMap<>(16);
        if (this.rel_object != null) {
            for (SceneProperty sceneProperty : this.rel_object.propertyList) {
                SceneDataValue sdv = new SceneDataValue(Repository, this, sceneProperty.propertyName, sceneProperty);
                this.value_object.put(sceneProperty.propertyName,sdv );
            }
        }
        if (this.query_attached != null) {
            for (SceneProperty sceneProperty : this.query_attached) {
                this.value_object.put(sceneProperty.propertyName, new SceneDataValue(Repository, this, sceneProperty.propertyName, sceneProperty));
            }
        }
    }



    public SceneDataObject(RepositoryBase Repository, SceneDataObject parentObjectData, String myPropertyName, SceneDataValue parentArrayData,
                           SceneObject custom_object, SceneProperty[] query_attached, SceneDataObject father) {
        this.parentObjectData = parentObjectData;
        this.myPropertyName = myPropertyName;
        this.parentArrayData = parentArrayData;
        this.rel_object = custom_object;
        this.query_attached = query_attached;
        this.father = father;

        this.value_object = new HashMap<>(16);
        if (this.rel_object != null) {
            for (SceneProperty sceneProperty : this.rel_object.propertyList) {
                this.value_object.put(sceneProperty.propertyName, new SceneDataValue(Repository, this, sceneProperty.propertyName, sceneProperty));
            }
        }
        if (this.query_attached != null) {
            for (SceneProperty sceneProperty : this.query_attached) {
                this.value_object.put(sceneProperty.propertyName, new SceneDataValue(Repository, this, sceneProperty.propertyName, sceneProperty));
            }
        }
    }

    public Object toJSON(int depth) {
        Object result = this.toJSON(depth, false);
        return result;
    }

    public Object toJSON(int depth, boolean with_change) {
        // 根据信息点是否显示过滤设备类型
        if (this.rel_object != null && this.rel_object.allow_pass.equals("0")) {
            return null;
        }

        int depthInner = depth == -1 ? -1 : (depth > 0 ? depth - 1 : 0);
        JSONObject result = new JSONObject();
        if (with_change) {
            result.put("sailfish_inner_RowChange", this.getRowChange());
        }
        for (String key : this.keySet()) {
            if (KeywordUtil.containsKey(key)) {
                continue;
            }
            SceneDataValue sdvInner = this.get(key);
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
        Set<String> result = new HashSet<String>();
        result.addAll(this.value_object.keySet());
        return result;
    }

    public Set<String> keySet() {
        Set<String> result = new HashSet<String>();
        result.addAll(this.value_object.keySet());
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
        if (this.value_object.containsKey(key)) {
            return true;
        }
        if (this.father != null) {
            if (this.fatherReturnColumnMap == null || this.fatherReturnColumnMap.containsKey(key)) {
                return this.father.containsKey(key);
            }
        }
        return false;
    }

    public SceneDataValue get(String key) {
        if (this.value_object.containsKey(key)) {
            return this.value_object.get(key);
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

    public void put(String key, SceneDataValue value) {
        this.value_object.put(key, value);
    }

    public void remove(String key) {
        this.value_object.remove(key);
    }

    public int size() {
        int result = 0;
        if (this.father != null) {
            result += this.father.size();
        }
        result += this.value_object.size();
        return result;
    }

    public boolean getRowChange() {
        if (this.father != null) {
            return this.father.getRowChange();
        } else {
            return this.change.row_change;
        }
    }

    public void setRowChange(boolean value) throws Exception {
        this.change.row_change = value;
    }

    public Map<String, Boolean> getColChange() {
        Map<String, Boolean> result;
        if (this.father != null) {
            result = new ConcurrentHashMap<String, Boolean>();
            Map<String, Boolean> result_father = this.father.getColChange();
            {
                for (String col : result_father.keySet()) {
                    result.put(col, true);
                }
            }
            {
                for (String col : this.change.colChangeMap.keySet()) {
                    result.put(col, true);
                }
            }
        } else {
            result = this.change.colChangeMap;
        }
        return result;
    }

    public void setColChange(String col) {
        if (!this.hasColChange(col)) {
            this.change.colChangeMap.put(col, true);
        }
    }

    public void setColChange(Map<String, Boolean> colMap) {
        for (String col : colMap.keySet()) {
            if (!this.hasColChange(col)) {
                this.change.colChangeMap.put(col, true);
            }
        }
    }

    public boolean hasColChange(String col) {
        if (this.father != null) {
            if (this.father.hasColChange(col)) {
                return true;
            }
        }
        return this.change.colChangeMap.containsKey(col);
    }
}
