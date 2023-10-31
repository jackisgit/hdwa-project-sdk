package com.hdwa.sdk.utils;

import com.hdwa.sdk.entity.repository.RepositoryBase;
import com.hdwa.sdk.entity.repository.WaitItem;
import lombok.extern.slf4j.Slf4j;

/**
 * @author abao
 * @since 2023/10/23
 * 计算
 */
@Slf4j
public class ComputeThread implements Runnable {

    private final RepositoryBase repository;
    private final long interval;
    private volatile boolean stop = false;

    public ComputeThread(RepositoryBase Repository, long interval) {
        this.repository = Repository;
        this.interval = interval;
    }

    @Override
    public void run() {
        log.warn("****计算线程已启动");
        int cycleCount = 0;
        while (!stop) {
            cycleCount++;
            if (cycleCount >= 1000) {
                try {
                    Thread.sleep(1L);
                } catch (InterruptedException e) {
                    log.error(e.getMessage(), e);
                }
                cycleCount = 0;
            }

           /* Date currTime = new Date();
            if (currTime.getTime() / (1000L * 15) != lastTime.getTime() / (1000L * 15)) {
                lastTime = currTime;
                if (compute_count > 0) {
                    log.warn("compute_count: " + compute_count + " avg_lag: " + (compute_lag / compute_count));
                    compute_count = 0;
                    compute_lag = 0;
                }
            }*/

            WaitItem waitItem = repository.WaitCompute.pollFromQueue();
            if (waitItem == null) {
                continue;
            }
            /*if (currTime.getTime() < WaitItem.sdv.lastComputeTime.getTime() + this.interval) {
                repository.WaitCompute.offerToQueue(WaitItem);
                continue;
            }*/
            repository.WaitCompute.removeFromMap(waitItem);
            try {
                /*  if (WaitItem.time.getTime() > WaitItem.sdv.lastComputeTime.getTime() + this.interval) {
                    compute_lag += currTime.getTime() - WaitItem.time.getTime();
                } else {
                    compute_lag += currTime.getTime() - (WaitItem.sdv.lastComputeTime.getTime() + this.interval);
                }*/
                //log.warn("compute: " + PathUtil.getDataPath(waitItem.sdv));
                CalculateApiJsonUtil.calculateProperty(repository, waitItem.sdv);

                //repository.ComputeOccur(WaitItem.sdv);
                repository.addWaitCompute(waitItem.sdv);
            } catch (Exception e) {
                try {
                    String path = PathUtil.getDataPath(waitItem.sdv);
                    log.error(path, e);
                } catch (Exception e1) {
                    log.error("路径获取异常", e1);
                }
            }

        }
    }
}
