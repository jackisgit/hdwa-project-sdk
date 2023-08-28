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

    /***
     * objectSet中是否包含属性名为content的JSONArray属性值，该JSONArray包含object
     *
     * @param objectSet
     * @param content
     * @param object
     * @return
     */
    public boolean Contain(JSONObject objectSet, String content, JSONObject object) {
        if (objectSet == null) {
            return false;
        }
        if (!objectSet.containsKey(content)) {
            return false;
        }
        Object contentObject = objectSet.get(content);
        if (!(contentObject instanceof JSONArray)) {
            return false;
        }
        JSONArray contentArray = (JSONArray) contentObject;
        for (int i = 0; i < contentArray.size(); i++) {
            Object itemObject = contentArray.get(i);
            if (!(itemObject instanceof JSONObject)) {
                continue;
            }
            JSONObject jsonItem = (JSONObject) itemObject;
            if (this.Compare(jsonItem, object, false)) {
                return true;
            }
        }
        return false;
    }

    public boolean Contain(JSONArray objectSet, Object object, String[] keys) {
        if (object == null) {
            return true;
        } else {
            if (objectSet == null) {
                return false;
            }
        }

        for (int i = 0; i < objectSet.size(); i++) {
            if (this.CompareObject(objectSet.get(i), object, keys)) {
                return true;
            }
        }
        return false;
    }

    public boolean Contain(JSONArray objectSet, Object object) {
        if (object == null) {
            return true;
        } else {
            if (objectSet == null) {
                return false;
            }
        }

        for (int i = 0; i < objectSet.size(); i++) {
            if (this.CompareObject(objectSet.get(i), object, false)) {
                return true;
            }
        }
        return false;
    }

    public boolean Contain(JSONObject objectSet, JSONObject object) {
        if (object == null) {
            return true;
        } else {
            if (objectSet == null) {
                return false;
            }
        }

        for (String key : object.keySet()) {
            Object value = object.get(key);

            if (!objectSet.containsKey(key)) {
                return false;
            } else {
                Object valueSet = objectSet.get(key);
                if (!this.CompareObject(valueSet, value, false)) {
                    return false;
                }
            }
        }

        return true;
    }

    /***
     * 比较两个对象是否相等，对象类型可以是4种简单类型或者JSONObject、JSONArray
     *
     * @param value1
     * @param value2
     * @return
     */
    public boolean CompareObject(Object value1, Object value2) {
        return this.CompareObject(value1, value2, false);
    }

    public boolean CompareObject(Object value1, Object value2, boolean sequence) {
        if (value1 == null && value2 == null) {
            return true;
        } else if (value1 == null && value2 != null) {
            return false;
        } else if (value1 != null && value2 == null) {
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
                result = Math.abs(((Float) value1).floatValue() - ((Float) value2).floatValue()) < 0.00000001;
            } else if (value1 instanceof Double && value2 instanceof Double) {
                result = Math.abs(((Double) value1).doubleValue() - ((Double) value2).doubleValue()) < 0.00000001;
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

    public boolean CompareObject(Object value1, Object value2, String[] keys) {
        if (value1 == null && value2 == null) {
            return true;
        } else if (value1 == null && value2 != null) {
            return false;
        } else if (value1 != null && value2 == null) {
            return false;
        }

        boolean result = false;
        result = this.Compare((JSONObject) value1, (JSONObject) value2, keys);
        return result;
    }

    /***
     * 比较两个JSONObject是否相等，顺序无所谓
     *
     * @param arg1
     * @param arg2
     * @return
     */
    public boolean Compare(JSONObject arg1, JSONObject arg2) {
        return this.Compare(arg1, arg2, false);
    }

    public boolean Compare(JSONObject arg1, JSONObject arg2, boolean sequence) {
        if (arg1 == null && arg2 == null) {
            return true;
        } else if (arg1 == null && arg2 != null) {
            return false;
        } else if (arg1 != null && arg2 == null) {
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

    public boolean Compare(JSONObject arg1, JSONObject arg2, String[] keys) {
        if (arg1 == null && arg2 == null) {
            return true;
        } else if (arg1 == null && arg2 != null) {
            return false;
        } else if (arg1 != null && arg2 == null) {
            return false;
        }

        for (String key : keys) {
            if (arg1.containsKey(key) && arg2.containsKey(key)) {
                Object value1 = arg1.get(key);
                Object value2 = arg2.get(key);
                if (!this.CompareObject(value1, value2, false)) {
                    return false;
                }
            } else if (!arg1.containsKey(key) && arg2.containsKey(key)) {
                return false;
            } else if (arg1.containsKey(key) && !arg2.containsKey(key)) {
                return false;
            }
        }
        return true;
    }

    /***
     * 比较两个JSONArray是否相等，顺序无所谓
     *
     * @param arg1
     * @param arg2
     * @return
     */
    public boolean Compare(JSONArray arg1, JSONArray arg2) {
        return this.Compare(arg1, arg2, false);
    }

    public boolean Compare(JSONArray arg1, JSONArray arg2, boolean sequence) {
        if (arg1 == null && arg2 == null) {
            return true;
        } else if (arg1 == null && arg2 != null) {
            return false;
        } else if (arg1 != null && arg2 == null) {
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
            return true;
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
            return true;
        }
    }
}
