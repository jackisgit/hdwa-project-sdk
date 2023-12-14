package com.hdwa.sdk.utils;


import com.hdwa.sdk.entity.scene.DataObject;
import com.hdwa.sdk.entity.scene.DataValue;

import java.util.List;

public class CompareUtil {
    private static CompareUtil instance = new CompareUtil();

    private CompareUtil() {

    }

    public static CompareUtil Instance() {
        return instance;
    }

    public boolean CompareObject(DataValue sdv1, DataValue sdv2) {
        if (sdv1 == null && sdv2 == null) {
            return true;
        } else if (sdv1 == null) {
            return false;
        } else if (sdv2 == null) {
            return false;
        }

        Object value1 = sdv1.valuePrim.value != null ? sdv1.valuePrim.value : (sdv1.valueObject != null ? sdv1.valueObject : sdv1.valueArray);
        Object value2 = sdv2.valuePrim.value != null ? sdv2.valuePrim.value : (sdv2.valueObject != null ? sdv2.valueObject : sdv2.valueArray);
        if (value1 == null && value2 == null) {
            return true;
        } else if (value1 == null) {
            return false;
        } else if (value2 == null) {
            return false;
        }

        boolean result = false;
        if (value1.getClass() == value2.getClass()) {
            if (value1 instanceof Long && value2 instanceof Long) {
                result = ((Long) value1).longValue() == ((Long) value2).longValue();
            } else if (value1 instanceof Double && value2 instanceof Double) {
                result = Math.abs((Double) value1 - (Double) value2) < 0.00000001;
            } else if (value1 instanceof Boolean && value2 instanceof Boolean) {
                result = ((Boolean) value1).booleanValue() == ((Boolean) value2).booleanValue();
            } else if (value1 instanceof String && value2 instanceof String) {
                result = value1.equals(value2);
            } else if (value1 instanceof DataObject && value2 instanceof DataObject) {
                result = this.Compare((DataObject) value1, (DataObject) value2);
            } else if (value1 instanceof List && value2 instanceof List) {
                result = this.Compare((List<DataObject>) value1, (List<DataObject>) value2);
            }
        }
        return result;
    }

    public boolean Compare(DataObject arg1, DataObject arg2) {
        if (arg1 == null && arg2 == null) {
            return true;
        } else if (arg1 == null) {
            return false;
        } else if (arg2 == null) {
            return false;
        }

        if (arg1.size() != arg2.size()) {
            return false;
        }

        for (String key : arg1.keySet()) {
            if (arg2.containsKey(key)) {
                DataValue value1 = arg1.get(key);
                DataValue value2 = arg2.get(key);

                boolean equal = this.CompareObject(value1, value2);
                if (!equal) {
                    return false;
                }
            } else {
                return false;
            }
        }
        return true;
    }

    public boolean Compare(List<DataObject> arg1, List<DataObject> arg2) {
        if (arg1 == null && arg2 == null) {
            return true;
        } else if (arg1 == null) {
            return false;
        } else if (arg2 == null) {
            return false;
        }

        if (arg1.size() != arg2.size()) {
            return false;
        }

        boolean[] useArray = new boolean[arg1.size()];
        for (int i = 0; i < arg1.size(); i++) {
            DataObject value1 = arg1.get(i);

            // 在arg2中匹配
            boolean find = false;
            for (int ii = 0; ii < arg1.size(); ii++) {
                if (useArray[ii]) {
                    continue;
                }

                DataObject value2 = arg2.get(ii);

                boolean equal = this.Compare(value1, value2);
                if (equal) {
                    useArray[ii] = true;
                    find = true;
                    break;
                }
            }

            // 匹配失败，返回
            if (!find) {
                return false;
            }
        }
        return true;

    }
}
