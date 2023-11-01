package com.hdwa.sdk.utils;

import com.hdwa.sdk.entity.repository.RepositoryBase;
import com.hdwa.sdk.entity.repository.WaitItem;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

/**
 * @author abao
 * @since 2023/10/23
 * 计算
 */
@Slf4j
public class ComputeThread implements Runnable {

    private final RepositoryBase repository;
    private final long interval;
    private boolean stop = false;

    public ComputeThread(RepositoryBase Repository, long interval) {
        this.repository = Repository;
        this.interval = interval;
    }

    @Override
    public void run() {
        log.warn("****计算线程已启动");
        int cycleCount = 0;
       /* int computeCount = 0;
        long computeLag = 0;
        Date lastTime = new Date();*/
        while (!stop) {
            cycleCount++;
            if (cycleCount >= 1000) {
                try {
                    Thread.sleep(100L);
                } catch (InterruptedException e) {
                    log.error("线程休眠异常", e);
                }
                cycleCount = 0;
            }

            Date currTime = new Date();
           /* if (currTime.getTime() / (1000L * 15) != lastTime.getTime() / (1000L * 15)) {
                lastTime = currTime;
                if (computeCount > 0) {
                    log.warn("compute_count: " + computeCount + " avg_lag: " + (computeLag / computeCount));
                    computeCount = 0;
                    computeLag = 0;
                }
            }*/

            WaitItem waitItem = repository.WaitCompute.pollFromQueue();
            if (waitItem == null) {
                continue;
            }
            if (currTime.getTime() < waitItem.sdv.lastComputeTime.getTime() + this.interval) {
                repository.WaitCompute.offerToQueue(waitItem);
                continue;
            }
            repository.WaitCompute.removeFromMap(waitItem);
            try {
               /* computeCount++;
                if (waitItem.time.getTime() > waitItem.sdv.lastComputeTime.getTime() + this.interval) {
                    computeLag += currTime.getTime() - waitItem.time.getTime();
                } else {
                    computeLag += currTime.getTime() - (waitItem.sdv.lastComputeTime.getTime() + this.interval);
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
