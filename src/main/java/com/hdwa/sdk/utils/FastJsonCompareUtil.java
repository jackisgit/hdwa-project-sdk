package com.hdwa.sdk.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.math.BigDecimal;
import java.math.BigInteger;

public class FastJsonCompareUtil {

    private static FastJsonCompareUtil instance = new FastJsonCompareUtil();

    private FastJsonCompareUtil() {

    }

    public static FastJsonCompareUtil Instance() {
        return instance;
    }


    public boolean CompareObject(Object value1, Object value2) {
        return this.CompareObject(value1, value2, false);
    }

    public boolean CompareObject(Object value1, Object value2, boolean sequence) {
        if (value1 == null && value2 == null) {
            return true;
        } else if (value1 == null) {
            return false;
        } else if (value2 == null) {
            return false;
        }

        boolean result = false;
        if (value1.getClass() == value2.getClass()) {
            if (value1 instanceof Integer && value2 instanceof Integer) {
                result = ((Integer) value1).intValue() == ((Integer) value2).intValue();
            } else if (value1 instanceof Long && value2 instanceof Long) {
                result = ((Long) value1).longValue() == ((Long) value2).longValue();
            } else if (value1 instanceof BigInteger && value2 instanceof BigInteger) {
                result = ((BigInteger) value1).longValue() == ((BigInteger) value2).longValue();
            } else if (value1 instanceof Float && value2 instanceof Float) {
                result = Math.abs((Float) value1 - (Float) value2) < 0.00000001;
            } else if (value1 instanceof Double && value2 instanceof Double) {
                result = Math.abs((Double) value1 - (Double) value2) < 0.00000001;
            } else if (value1 instanceof BigDecimal && value2 instanceof BigDecimal) {
                result = Math.abs(((BigDecimal) value1).doubleValue() - ((BigDecimal) value2).doubleValue()) < 0.00000001;
            } else if (value1 instanceof Boolean && value2 instanceof Boolean) {
                result = ((Boolean) value1).booleanValue() == ((Boolean) value2).booleanValue();
            } else if (value1 instanceof String && value2 instanceof String) {
                result = ((String) value1).equals((String) value2);
            } else if (value1 instanceof JSONObject && value2 instanceof JSONObject) {
                result = this.Compare((JSONObject) value1, (JSONObject) value2, sequence);
            } else if (value1 instanceof JSONArray && value2 instanceof JSONArray) {
                result = this.Compare((JSONArray) value1, (JSONArray) value2, sequence);
            }
        }
        return result;
    }

    public boolean Compare(JSONObject arg1, JSONObject arg2, boolean sequence) {
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
                Object value1 = arg1.get(key);
                Object value2 = arg2.get(key);

                boolean equal = this.CompareObject(value1, value2, sequence);
                if (!equal) {
                    return false;
                }
            } else {
                return false;
            }
        }
        return true;
    }

    public boolean Compare(JSONArray arg1, JSONArray arg2, boolean sequence) {
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

        if (sequence) {
            for (int i = 0; i < arg1.size(); i++) {
                Object value1 = arg1.get(i);
                Object value2 = arg2.get(i);
                boolean equal = this.CompareObject(value1, value2, true);
                if (!equal) {
                    return false;
                }
            }
        } else {
            boolean[] useArray = new boolean[arg1.size()];
            for (int i = 0; i < arg1.size(); i++) {
                Object value1 = arg1.get(i);

                // 在arg2中匹配
                boolean find = false;
                for (int ii = 0; ii < arg1.size(); ii++) {
                    if (useArray[ii]) {
                        continue;
                    }

                    Object value2 = arg2.get(ii);

                    boolean equal = this.CompareObject(value1, value2, false);
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
        }
        return true;
    }
}
