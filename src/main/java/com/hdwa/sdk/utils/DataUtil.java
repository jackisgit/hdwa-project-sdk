package com.hdwa.sdk.utils;

import com.hdwa.sdk.entity.scene.SceneDataObject;
import com.hdwa.sdk.entity.scene.SceneDataPrimitive;
import com.hdwa.sdk.entity.scene.SceneDataValue;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
public class DataUtil {
    public static List<SceneDataObject> avg(List<List<SceneDataObject>> dataListList) throws Exception {
        List<SceneDataObject> result = new CopyOnWriteArrayList<SceneDataObject>();
        int[] indexs = new int[dataListList.size()];
        while (true) {
            String data_time_min = null;
            int min_index = -1;
            for (int i = 0; i < dataListList.size(); i++) {
                List<SceneDataObject> dataListInner = dataListList.get(i);
                if (indexs[i] < dataListInner.size()) {
                    SceneDataObject datainner = (SceneDataObject) dataListInner.get(indexs[i]);
                    SceneDataValue sdvInner = datainner.get("data_time");
                    String data_time = (String) sdvInner.value_prim.value;
                    if (data_time_min == null || data_time_min.compareTo(data_time) > 0) {
                        data_time_min = data_time;
                        min_index = i;
                    }
                }
            }
            if (min_index == -1) {
                break;
            }

            SceneDataObject avgData = new SceneDataObject(null, null, null, null, null, null, null);
            int totalCount = 0;
            double totalValue = 0.0;
            for (int i = 0; i < dataListList.size(); i++) {
                List<SceneDataObject> dataListInner = dataListList.get(i);
                if (indexs[i] < dataListInner.size()) {
                    SceneDataObject datainner = (SceneDataObject) dataListInner.get(indexs[i]);
                    SceneDataValue sdvInner = datainner.get("data_time");
                    String data_time = (String) sdvInner.value_prim.value;
                    if (data_time.equals(data_time_min)) {
                        totalCount++;
                        totalValue += DataUtil.primitive2double(datainner.get("data_value").value_prim.value);
                        indexs[i]++;
                    }
                }
            }
            SceneDataValue data_time = new SceneDataValue(null, null, null, null);
            data_time.value_prim = new SceneDataPrimitive();
            data_time.value_prim.value = data_time_min;
            SceneDataValue data_value = new SceneDataValue(null, null, null, null);
            data_value.value_prim = new SceneDataPrimitive();
            data_value.value_prim.value = totalValue / totalCount;
            avgData.put("data_time", data_time);
            avgData.put("data_value", data_value);
            result.add(avgData);
        }

        return result;
    }

    public static double primitive2double(Object value_primitive) throws Exception {
        if (value_primitive instanceof Integer) {
            return ((Integer) value_primitive).doubleValue();
        } else if (value_primitive instanceof Long) {
            return ((Long) value_primitive).doubleValue();
        } else if (value_primitive instanceof BigInteger) {
            return ((BigInteger) value_primitive).doubleValue();
        } else if (value_primitive instanceof Float) {
            return ((Float) value_primitive).doubleValue();
        } else if (value_primitive instanceof Double) {
            return ((Double) value_primitive).doubleValue();
        } else if (value_primitive instanceof BigDecimal) {
            return ((BigDecimal) value_primitive).doubleValue();
        } else {
            log.error("primitive2double: " + value_primitive + "\t" + value_primitive.getClass().getName());
            throw new Exception();
        }
    }

    public static Object primitive_normalize(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Integer) {
            return ((Integer) value).doubleValue();
        } else if (value instanceof Long) {
            return ((Long) value).doubleValue();
        } else if (value instanceof BigInteger) {
            return ((BigInteger) value).doubleValue();
        } else if (value instanceof Float) {
            return ((Float) value).doubleValue();
        } else if (value instanceof Double) {
            return ((Double) value).doubleValue();
        } else if (value instanceof BigDecimal) {
            return ((BigDecimal) value).doubleValue();
        } else {
            return value;
        }
    }
}
