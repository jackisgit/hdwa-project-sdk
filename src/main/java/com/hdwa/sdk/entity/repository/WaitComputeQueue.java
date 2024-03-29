package com.hdwa.sdk.entity.repository;


import com.hdwa.sdk.entity.scene.DataValue;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 计算等待队列
 */
public class WaitComputeQueue {
    private ConcurrentLinkedQueue<WaitItem> queue = new ConcurrentLinkedQueue<>();
    private Map<DataValue, Boolean> map = new ConcurrentHashMap<>(16);

    public void offer(WaitItem WaitItem) {
        Boolean exist = map.putIfAbsent(WaitItem.sdv, true);
        if (exist == null) {
            queue.offer(WaitItem);
        }
    }

    public void offerToQueue(WaitItem waitItem) {
        queue.offer(waitItem);
    }

    public WaitItem pollFromQueue() {
        return queue.poll();
    }

    public void removeFromMap(WaitItem waitItem) {
        if (waitItem != null) {
            map.remove(waitItem.sdv);
        }
    }
}
