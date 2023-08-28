package com.hdwa.sdk.utils;

import java.util.concurrent.ConcurrentLinkedQueue;

public class PacketBuffer<T> {
    private ConcurrentLinkedQueue<T> buffer = new ConcurrentLinkedQueue<T>();

    public PacketBuffer() {
    }

    public boolean offer(T packet, int size) {
        if (this.buffer.size() >= size) {
            return false;
        }

        this.buffer.offer(packet);
        return true;
    }

    public T poll() {
        T MyPackage = this.buffer.poll();
        return MyPackage;
    }

    public T take() {
        T MyPackage = this.buffer.peek();
        return MyPackage;
    }

    public int BufferSize() {
        return this.buffer.size();
    }
}
