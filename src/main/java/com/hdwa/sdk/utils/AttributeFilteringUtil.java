package com.hdwa.sdk.utils;

import com.hdwa.sdk.constant.BaseDecConstant;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 属性过滤类
 */
public class AttributeFilteringUtil {
    /**
     * 关键字
     */
    public static Map<String, Boolean> keyWordMap = new HashMap<>(16);
    /**
     * 前缀
     */
    public static Map<String, Boolean> prefixMap = new HashMap<>(16);
    /**
     * 后缀
     */
    public static Map<String, Boolean> suffixMap = new HashMap<>(16);
    /**
     * 属性
     */
    public static List<String> keyProperty = new CopyOnWriteArrayList<>();

    static {
        keyProperty.add("id");
        keyProperty.add("名称");

        suffixMap.put("-" + BaseDecConstant.METER_FUNGICIDE, true);

        keyWordMap.put("所在建筑", true);
        keyWordMap.put("所在楼层", true);
        keyWordMap.put("所属楼层", true);
        keyWordMap.put("所属建筑", true);
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
        keyWordMap.put("aliasName", true);
        keyWordMap.put("system", true);
        keyWordMap.put("valid", true);
        keyWordMap.put("systemName", true);
        keyWordMap.put("subSystemName", true);
        keyWordMap.put("subSystem", true);
        keyWordMap.put("显示名称", true);
        keyWordMap.put("所属控制模块对象", true);
        keyWordMap.put("所属配电箱对象", true);
        keyWordMap.put("所属电井对象", true);
        keyWordMap.put("关联的摄像头", true);
        //keyWordMap.put("associatedCamera", true);
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
