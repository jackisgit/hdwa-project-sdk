package com.hdwa.sdk.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hdwa.sdk.entity.scene.DataObject;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Comparator;

/**
 * @author abao
 * @since 2023/8/25
 * 场景排序对象
 */
public class ComparatorSceneDataObject implements Comparator<DataObject> {

    JSONArray OrderBy;

    public ComparatorSceneDataObject(JSONArray OrderBy) {
        this.OrderBy = OrderBy;
    }

    public int compare(DataObject o1, DataObject o2) {
        for (Object o : OrderBy) {
            JSONObject item = (JSONObject) o;
            String Column = (String) item.get("Column");
            Boolean Asc = (Boolean) item.get("Asc");
            Object v1 = null;
            if (o1.get(Column) != null && o1.get(Column).valuePrim != null) {
                v1 = o1.get(Column).valuePrim.value;
            }
            Object v2 = null;
            if (o2.get(Column) != null && o2.get(Column).valuePrim != null) {
                v2 = o2.get(Column).valuePrim.value;
            }
            if (v1 == null && v2 != null) {
                if (Asc) {
                    return -1;
                } else {
                    return 1;
                }
            } else if (v1 != null && v2 == null) {
                if (Asc) {
                    return 1;
                } else {
                    return -1;
                }
            } else {
                int cmp;
                if (v1 instanceof String && v2 instanceof String) {
                    cmp = ((String) v1).compareTo((String) v2);
                } else if (v1 instanceof Integer && v2 instanceof Integer) {
                    cmp = ((Integer) v1).compareTo((Integer) v2);
                } else if (v1 instanceof Long && v2 instanceof Long) {
                    cmp = ((Long) v1).compareTo((Long) v2);
                } else if (v1 instanceof BigInteger && v2 instanceof BigInteger) {
                    cmp = ((BigInteger) v1).compareTo((BigInteger) v2);
                } else if (v1 instanceof Float && v2 instanceof Float) {
                    cmp = ((Float) v1).compareTo((Float) v2);
                } else if (v1 instanceof Double && v2 instanceof Double) {
                    cmp = ((Double) v1).compareTo((Double) v2);
                } else if (v1 instanceof BigDecimal && v2 instanceof BigDecimal) {
                    cmp = ((BigDecimal) v1).compareTo((BigDecimal) v2);
                } else if (v1 instanceof Boolean && v2 instanceof Boolean) {
                    cmp = ((Boolean) v1).compareTo((Boolean) v2);
                } else {
                    continue;
                }
                if (cmp != 0) {
                    if (Asc) {
                        return cmp;
                    } else {
                        return -cmp;
                    }
                }
            }
        }
        return 0;
    }
}
