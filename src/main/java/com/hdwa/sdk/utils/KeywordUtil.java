package com.hdwa.sdk.utils;

import com.hdwa.sdk.constant.BaseDecConstant;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class KeywordUtil {
    public static Map<String, Boolean> keyWordMap = new HashMap<>(16);
    public static Map<String, Boolean> prefixMap = new HashMap<>(16);
    public static Map<String, Boolean> suffixMap = new HashMap<>(16);
    public static List<String> keyProperty = new CopyOnWriteArrayList<String>();

    static {
        keyProperty.add("名称");
        keyProperty.add("部件");

        suffixMap.put("-" + BaseDecConstant.METER_FUNGICIDE, true);

        keyWordMap.put("所在建筑", true);
        keyWordMap.put("所在楼层", true);
        keyWordMap.put("楼层名称", true);
        keyWordMap.put("所在空间", true);
        keyWordMap.put("被设备控制", true);
        keyWordMap.put("控制设备", true);
        keyWordMap.put("被设备供电", true);
        keyWordMap.put("所属系统", true);
        keyWordMap.put("给设备供电", true);
        keyWordMap.put("关联系统", true);
        keyWordMap.put("服务空间", true);
        keyWordMap.put("空间名称", true);
        keyWordMap.put("部件", true);
        keyWordMap.put("报警列表", true);
        keyWordMap.put("wdCode", true);
        keyWordMap.put("grouping", true);
        keyWordMap.put("aliasCode", true);
    }

    public static boolean containsKey(String key) {
        if (keyWordMap.containsKey(key)) {
            return true;
        }
        if (prefixMap != null) {
            for (String prefix : prefixMap.keySet()) {
                if (key.startsWith(prefix)) {
                    return true;
                }
            }
        }
        if (suffixMap != null) {
            for (String suffix : suffixMap.keySet()) {
                if (key.endsWith(suffix)) {
                    return true;
                }
            }
        }

        return false;
    }

    public static String keyDefault() {
        return keyProperty.get(0);
    }
}
