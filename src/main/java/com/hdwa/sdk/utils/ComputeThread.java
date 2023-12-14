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
public class ComputeThread extends Thread {

    private RepositoryBase repository;
    private long interval;
    private boolean stop = false;

    public ComputeThread(RepositoryBase repository, long interval) {
        this.repository = repository;
        this.interval = interval;
    }

    public void requestStop() {
        stop = true;
        try {
            this.join();
        } catch (InterruptedException e) {
            log.error("***停止线程失败", e);
        }
    }

    @Override
    public void run() {
        log.warn("****计算线程已启动");
        int cycleCount = 0;
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

            if (repository == null) {
                continue;
            }
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
                boolean computeValueChanged = CalculateApiJsonUtil.calculateProperty(repository, waitItem.sdv);
                if (computeValueChanged) {
                    repository.addWaitCompute(waitItem.sdv);
                }
            } catch (Exception e) {
                log.error(PathUtil.getDataPath(waitItem.sdv), e);
            }
        }
    }
}
