package com.hdwa.sdk.entity.scene;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author abao
 * @since 2023/8/25
 * 动态值改变标识类
 */
public class Change {

    public boolean row_change = false;

    public Map<String, Boolean> colChangeMap = new ConcurrentHashMap<>();

    @Override
    public String toString() {
        return "" + row_change + "\t" + colChangeMap;
    }
}
