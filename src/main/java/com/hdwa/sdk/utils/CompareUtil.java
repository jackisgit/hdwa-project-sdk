package com.hdwa.sdk.utils;


import com.hdwa.sdk.entity.scene.SceneDataObject;
import com.hdwa.sdk.entity.scene.SceneDataValue;

import java.util.Iterator;
import java.util.List;

public class CompareUtil {
    private static CompareUtil instance = new CompareUtil();

    private CompareUtil() {

    }

    public static CompareUtil Instance() {
        return instance;
    }

    @SuppressWarnings("unchecked")
    public boolean CompareObject(SceneDataValue sdv1, SceneDataValue sdv2) {
        if (sdv1 == null && sdv2 == null) {
            return true;
        } else if (sdv1 == null && sdv2 != null) {
            return false;
        } else if (sdv1 != null && sdv2 == null) {
            return false;
        }

        Object value1 = sdv1.value_prim.value != null ? sdv1.value_prim.value : (sdv1.value_object != null ? sdv1.value_object : sdv1.value_array);
        Object value2 = sdv2.value_prim.value != null ? sdv2.value_prim.value : (sdv2.value_object != null ? sdv2.value_object : sdv2.value_array);
        if (value1 == null && value2 == null) {
            return true;
        } else if (value1 == null && value2 != null) {
            return false;
        } else if (value1 != null && value2 == null) {
            return false;
        }

        boolean result = false;
        if (value1.getClass() == value2.getClass()) {
            if (value1 instanceof Long && value2 instanceof Long) {
                result = ((Long) value1).longValue() == ((Long) value2).longValue();
            } else if (value1 instanceof Double && value2 instanceof Double) {
                result = Math.abs(((Double) value1).doubleValue() - ((Double) value2).doubleValue()) < 0.00000001;
            } else if (value1 instanceof Boolean && value2 instanceof Boolean) {
                result = ((Boolean) value1).booleanValue() == ((Boolean) value2).booleanValue();
            } else if (value1 instanceof String && value2 instanceof String) {
                result = ((String) value1).equals((String) value2);
            } else if (value1 instanceof SceneDataObject && value2 instanceof SceneDataObject) {
                result = this.Compare((SceneDataObject) value1, (SceneDataObject) value2);
            } else if (value1 instanceof List && value2 instanceof List) {
                result = this.Compare((List<SceneDataObject>) value1, (List<SceneDataObject>) value2);
            }
        }
        return result;
    }

    public boolean Compare(SceneDataObject arg1, SceneDataObject arg2) {
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

        Iterator<String> keys = arg1.keySet().iterator();
        while (keys.hasNext()) {
            String key = (String) keys.next();
            if (arg2.containsKey(key)) {
                SceneDataValue value1 = arg1.get(key);
                SceneDataValue value2 = arg2.get(key);

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

    public boolean Compare(List<SceneDataObject> arg1, List<SceneDataObject> arg2) {
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

        {
            boolean[] useArray = new boolean[arg1.size()];
            for (int i = 0; i < arg1.size(); i++) {
                SceneDataObject value1 = arg1.get(i);

                // 在arg2中匹配
                boolean find = false;
                for (int ii = 0; ii < arg1.size(); ii++) {
                    if (useArray[ii]) {
                        continue;
                    }

                    SceneDataObject value2 = arg2.get(ii);

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
}
