package com.hdwa.sdk.cache;

import org.springframework.stereotype.Component;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 实时数据
 */
@Component
public class CurrentDataCache {

    private final ConcurrentHashMap<String, Double> latestIOTDataMap = new ConcurrentHashMap<>();

    /**
     * 获取设备实时数据
     */
    public double getCurrentData(String meterId, String funcId) {
        return latestIOTDataMap.get(getKey(meterId, funcId));
    }

    public static String getKey(String meterId, String funcId) {
        return meterId + "-" + funcId;
    }

    /**
     * 存放实时数据
     */
    public void putCurrentData(String meterId, String funcId, double currentData) {
        latestIOTDataMap.put(getKey(meterId, funcId), currentData);
    }

    /**
     * 判断是否包含有改表号功能号的数据
     */
    public boolean hasKey(String meterId, String funcId) {
        return latestIOTDataMap.containsKey(getKey(meterId, funcId));
    }
}