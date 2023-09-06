package com.hdwa.sdk.utils;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
public class LockUtil {

    //创建 SingleObject 的一个对象
    private static final LockUtil instance = new LockUtil();
    public Lock lock = new ReentrantLock();
    public Condition condition = lock.newCondition();
    AtomicInteger num = new AtomicInteger(0);
    private volatile boolean execute = true;

    private LockUtil() {
    }

    public static LockUtil getInstance() {
        return instance;
    }

    public static void main(String[] args) throws InterruptedException {
        LockUtil.getInstance().await();
    }

    public boolean isExecute() {
        return execute;
    }

    public void setExecute(boolean execute) {
        this.execute = execute;
    }

    public void await() throws InterruptedException {
        System.out.println("----------");

        ExecutorService executorService = Executors.newFixedThreadPool(5);
        for (int j = 0; j < 20; j++) {
            executorService.execute(() -> {
                for (int i = 0; i < 100000; i++) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (LockUtil.getInstance().isExecute()) {
                        System.out.println(num.getAndIncrement());
                    } else {
                        lock.lock();
                        try {

                            log.info("[" + Thread.currentThread().getName() + "]" + "等待[{}]", lock);
                            try {
                                condition.await();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            log.info("[" + Thread.currentThread().getName() + "]" + "等待执行" + num.getAndIncrement());
                        } finally {
                            lock.unlock();
                        }

                    }
                }
            });
        }


        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(() -> {
            lock.lock();
            try {
                LockUtil.getInstance().setExecute(!LockUtil.getInstance().isExecute());
                //加个等待，保证正在执行的逻辑执行成功
                Thread.sleep(1000);
                if (LockUtil.getInstance().isExecute()) {
                    log.info("[" + Thread.currentThread().getName() + "]" + "signalAll");
                    condition.signalAll();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }

        }, 0, 3, TimeUnit.SECONDS);
    }
}