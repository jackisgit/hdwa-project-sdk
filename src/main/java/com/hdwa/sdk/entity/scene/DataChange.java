package com.hdwa.sdk.entity.scene;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author abao
 * @since 2023/8/25
 * 动态值改变标识类
 */
public class DataChange {

    public boolean rowChange = false;

    public Map<String, Boolean> colChangeMap = new ConcurrentHashMap<>();

    @Override
    public String toString() {
        return "" + rowChange + "\t" + colChangeMap;
    }
}
