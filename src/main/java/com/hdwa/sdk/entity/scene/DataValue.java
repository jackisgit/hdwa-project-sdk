package com.hdwa.sdk.entity.scene;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.hdwa.sdk.entity.repository.InfluenceFactor;
import com.hdwa.sdk.entity.repository.RepositoryBase;
import com.hdwa.sdk.utils.QueryUtil;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author abao
 * @since 2023/7/25
 * 单个数据值对象
 */
public class DataValue {

    public boolean finish = false;
    public DataObject parentObjectData;
    public String myPropertyName;
    public DataProperty relProperty;
    public DataObject valueObject;
    public DataSet valueArray;
    public DataPrimitive valuePrim;
    public InfluenceFactor rowFactor;
    public Map<String, InfluenceFactor> colFactorMap;
    public Date lastComputeTime;
    public ReentrantLock lock;

    public DataValue(RepositoryBase Repository, DataObject parentObjectData, String myPropertyName, DataProperty relProperty) {
        this.parentObjectData = parentObjectData;
        this.myPropertyName = myPropertyName;
        this.relProperty = relProperty;

        if (this.relProperty != null) {
            if (this.relProperty.propertyValueType.equals("query") || this.relProperty.propertyValueType.equals("static")
                    || this.relProperty.propertyValueType.equals("deamon")) {
                if (Repository.property2SDV_enable) {
                    List<DataValue> sdvList = Repository.property2SDV.get(this.relProperty);
                    sdvList.add(this);
                }
            }
            if (this.relProperty.propertyValueType.equals("query")) {
                this.lock = new ReentrantLock(true);
            }
        }

        if (this.relProperty != null) {
            if (this.relProperty.propertyValueType.equals("query")) {
            } else if (this.relProperty.propertyValueType.equals("custom")) {
                this.valueObject = new DataObject(Repository, parentObjectData, myPropertyName, null, this.relProperty.customObject, null,
                        null);
                this.finish = true;
            } else if (this.relProperty.propertyValueType.equals("static")) {
                if (this.relProperty.propertyValueSchema.equals("JSONArray")) {
                    this.valueArray = new DataSet(false);
                    for (DataObjectBase DataObjectBase : this.relProperty.staticArray) {
                        DataObject sdo = new DataObject(Repository, null, null, this, DataObjectBase, this.relProperty.queryAttached, null);
                        this.valueArray.set.add(sdo);
                    }
                    this.finish = true;
                }
            }
        }

        if (this.relProperty != null) {
            if (this.relProperty.propertyValueType.equals("static")) {
                if (this.relProperty.propertyValueSchema.equals("JSONObject")) {
                    this.finish = true;
                } else if (this.relProperty.propertyValueSchema.equals("JSONArray")) {
                } else {
                    this.valuePrim = new DataPrimitive();
                    this.valuePrim.value = QueryUtil.parse_static(this.relProperty.propertyValueSchema, this.relProperty.staticValue);
                    this.valuePrim.change = false;
                    this.finish = true;
                }
            }
        }
    }

    public Object toJSON(boolean is_direct, int depth) {
        return this.toJSON(is_direct, depth, false);
    }

    public Object toJSON(boolean is_direct, int depth, boolean with_change) {
        if (!is_direct && this.relProperty != null && this.relProperty.allowPass.equals("0")) {
            return null;
        }

        int curr_depth = depth;
        if (!is_direct && this.relProperty != null && curr_depth != -1) {
            curr_depth -= Integer.parseInt(this.relProperty.offsetLevel);
            if (curr_depth < 0) {
                return null;
            }
        }

        if (curr_depth > 0 || curr_depth == -1) {
            if (this.valueObject != null) {
                return this.valueObject.toJSON(curr_depth, with_change);
            } else if (this.valueArray != null) {
                return this.valueArray.toJSON(curr_depth, with_change);
            } else if (this.valuePrim != null) {
                return this.valuePrim.value;
            }
        } else {
            if (this.valuePrim != null) {
                return this.valuePrim.value;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this.toJSON(true, 1), SerializerFeature.WriteMapNullValue);
    }


    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }
}
