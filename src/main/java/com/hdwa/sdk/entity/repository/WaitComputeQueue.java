package com.hdwa.sdk.entity.repository;


import com.hdwa.sdk.entity.scene.SceneDataValue;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class WaitComputeQueue {
    private ConcurrentLinkedQueue<WaitItem> queue = new ConcurrentLinkedQueue<WaitItem>();
    private Map<SceneDataValue, Boolean> map = new ConcurrentHashMap<SceneDataValue, Boolean>();

    public void offer(WaitItem WaitItem) {
        Boolean exist = map.putIfAbsent(WaitItem.sdv, true);
        if (exist == null) {
            queue.offer(WaitItem);
        }
    }

    public void offerToQueue(WaitItem WaitItem) {
        queue.offer(WaitItem);
    }

    public WaitItem pollFromQueue() {
        WaitItem WaitItem = queue.poll();
        return WaitItem;
    }

    public void removeFromMap(WaitItem WaitItem) {
        if (WaitItem != null) {
            map.remove(WaitItem.sdv);
        }
    }
}
