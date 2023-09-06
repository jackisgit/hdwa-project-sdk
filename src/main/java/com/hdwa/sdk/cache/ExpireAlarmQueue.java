package com.hdwa.sdk.cache;

import com.hdwa.sdk.vo.ExpireAlarmMessageVO;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 报警过期，取消过期的消息队列，防止同步阻塞 报警过期，取消过期涉及到定时任务的添加和删除，多线程操作会有并发异常，单线程操作当报警很多的时候会有性能问题
 * 增加缓冲队列
 **/
public class ExpireAlarmQueue {
    //队列大小
    static final int QUEUE_MAX_SIZE = 20000;

    static BlockingQueue<ExpireAlarmMessageVO> blockingQueue = new LinkedBlockingQueue<ExpireAlarmMessageVO>(QUEUE_MAX_SIZE);

    /**
     * 私有的默认构造子，保证外界无法直接实例化
     */
    private ExpireAlarmQueue() {
    }

    //单例队列
    public static ExpireAlarmQueue getExpireAlarmMessageQueue() {
        return SingletonHolder.queue;
    }

    //生产入队
    public void produce(ExpireAlarmMessageVO expireAlarmMessage) throws InterruptedException {
        blockingQueue.put(expireAlarmMessage);
    }

    //消费出队
    public ExpireAlarmMessageVO consume() throws InterruptedException {
        return blockingQueue.take();
    }

    // 获取队列大小
    public int size() {
        return blockingQueue.size();
    }

    /**
     * 类级的内部类，也就是静态的成员式内部类，该内部类的实例与外部类的实例
     * 没有绑定关系，而且只有被调用到才会装载，从而实现了延迟加载
     */
    private static class SingletonHolder {
        /**
         * 静态初始化器，由JVM来保证线程安全
         */
        private static final ExpireAlarmQueue queue = new ExpireAlarmQueue();
    }
}