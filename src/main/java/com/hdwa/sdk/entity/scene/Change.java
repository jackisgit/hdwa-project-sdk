package com.hdwa.sdk.entity.scene;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Change {
    public boolean row_change = false;
    public Map<String, Boolean> colChangeMap = new ConcurrentHashMap<String, Boolean>();

    @Override
    public String toString() {
        return "" + row_change + "\t" + colChangeMap;
    }
}
