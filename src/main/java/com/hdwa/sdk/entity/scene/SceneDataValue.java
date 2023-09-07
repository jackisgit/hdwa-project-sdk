package com.hdwa.sdk.entity.scene;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.hdwa.sdk.entity.repository.InfluenceFactor;
import com.hdwa.sdk.entity.repository.RepositoryBase;
import com.hdwa.sdk.utils.QueryUtil;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

public class SceneDataValue {
    public boolean finish = false;

    public SceneDataObject parentObjectData;
    public String myPropertyName;

    public SceneProperty rel_property;

    public SceneDataObject value_object;
    public SceneDataSet value_array;
    public SceneDataPrimitive value_prim;

    public InfluenceFactor rowFactor;
    public Map<String, InfluenceFactor> colFactorMap;

    public Date last_compute_time;
    public ReentrantLock lock;

    public SceneDataValue(RepositoryBase Repository, SceneDataObject parentObjectData, String myPropertyName, SceneProperty rel_property) {
        this.parentObjectData = parentObjectData;
        this.myPropertyName = myPropertyName;
        this.rel_property = rel_property;

        if (this.rel_property != null) {
            if (this.rel_property.propertyValueType.equals("query") || this.rel_property.propertyValueType.equals("static")
                    || this.rel_property.propertyValueType.equals("deamon")) {
                if (Repository.property2SDV_enable) {
                    List<SceneDataValue> sdvList = Repository.property2SDV.get(this.rel_property);
                    sdvList.add(this);
                }
            }
            if (this.rel_property.propertyValueType.equals("query")) {
                this.lock = new ReentrantLock(true);
            }
        }

        if (this.rel_property != null) {
            if (this.rel_property.propertyValueType.equals("query")) {
            } else if (this.rel_property.propertyValueType.equals("custom")) {
                this.value_object = new SceneDataObject(Repository, parentObjectData, myPropertyName, null, this.rel_property.custom_object, null,
                        null);
                this.finish = true;
            } else if (this.rel_property.propertyValueType.equals("static")) {
                if (this.rel_property.propertyValueSchema.equals("JSONArray")) {
                    this.value_array = new SceneDataSet(false);
                    for (SceneObject SceneObject : this.rel_property.static_array) {
                        SceneDataObject sdo = new SceneDataObject(Repository, null, null, this, SceneObject, this.rel_property.query_attached, null);
                        this.value_array.set.add(sdo);
                    }
                    this.finish = true;
                }
            }
        }

        if (this.rel_property != null) {
            if (this.rel_property.propertyValueType.equals("static")) {
                if (this.rel_property.propertyValueSchema.equals("JSONObject")) {
                    this.finish = true;
                } else if (this.rel_property.propertyValueSchema.equals("JSONArray")) {
                } else {
                    this.value_prim = new SceneDataPrimitive();
                    this.value_prim.value = QueryUtil.parse_static(this.rel_property.propertyValueSchema, this.rel_property.static_value);
                    this.value_prim.change = false;
                    this.finish = true;
                }
            }
        }
    }

    public Object toJSON(boolean is_direct, int depth) {
        return this.toJSON(is_direct, depth, false);
    }

    public Object toJSON(boolean is_direct, int depth, boolean with_change) {
        if (!is_direct && this.rel_property != null && this.rel_property.allow_pass.equals("0")) {
            return null;
        }

        int curr_depth = depth;
        if (!is_direct && this.rel_property != null && curr_depth != -1) {
            curr_depth -= Integer.parseInt(this.rel_property.offset_level);
            if (curr_depth < 0) {
                return null;
            }
        }

        if (curr_depth > 0 || curr_depth == -1) {
            if (this.value_object != null) {
                return this.value_object.toJSON(curr_depth, with_change);
            } else if (this.value_array != null) {
                return this.value_array.toJSON(curr_depth, with_change);
            } else if (this.value_prim != null) {
                return this.value_prim.value;
            }
            return null;
        } else {
            if (this.value_prim != null) {
                return this.value_prim.value;
            }
            return null;
        }
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this.toJSON(true, 1), SerializerFeature.WriteMapNullValue);
    }
}
