package com.hdwa.sdk.entity.repository;

import com.hdwa.sdk.utils.ComputeUtil;
import com.hdwa.sdk.utils.PathUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

@Slf4j
public class RepositoryComputeThread extends Thread {
    private RepositoryBase Repository;
    private long interval;
    private volatile boolean stop = false;

    public RepositoryComputeThread(RepositoryBase Repository, long interval) {
        this.Repository = Repository;
        this.interval = interval;
    }

    public void requestStop() {
        stop = true;
        try {
            this.join();
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        }
    }

    public void run() {
        int cycle_count = 0;
        int compute_count = 0;
        long compute_lag = 0;
        Date lastTime = new Date();
        while (!stop) {
            cycle_count++;
            if (cycle_count >= 1000) {
                try {
                    Thread.sleep(1L);
                } catch (InterruptedException e) {
                    log.error(e.getMessage(), e);
                }
                cycle_count = 0;
            }

            Date currTime = new Date();
            if (currTime.getTime() / (1000L * 15) != lastTime.getTime() / (1000L * 15)) {
                lastTime = currTime;
                if (compute_count > 0) {
                    log.debug("compute_count: " + compute_count + " avg_lag: " + (compute_lag / compute_count));
                    compute_count = 0;
                    compute_lag = 0;
                }
            }

            WaitItem WaitItem = Repository.WaitCompute.pollFromQueue();
            if (WaitItem == null) {
                continue;
            }
            if (currTime.getTime() < WaitItem.sdv.last_compute_time.getTime() + this.interval) {
                Repository.WaitCompute.offerToQueue(WaitItem);
                continue;
            }
            Repository.WaitCompute.removeFromMap(WaitItem);
            try {
                compute_count++;
                if (WaitItem.time.getTime() > WaitItem.sdv.last_compute_time.getTime() + this.interval) {
                    compute_lag += currTime.getTime() - WaitItem.time.getTime();
                } else {
                    compute_lag += currTime.getTime() - (WaitItem.sdv.last_compute_time.getTime() + this.interval);
                }
                log.debug("compute: " + PathUtil.getDataPath(WaitItem.sdv));
                boolean computeValueChanged = ComputeUtil.computeProperty(Repository, WaitItem.sdv);
                if (computeValueChanged) {
                    Repository.ComputeOccur(WaitItem.sdv);
                    Repository.addWaitCompute(WaitItem.sdv);
                }
            } catch (Exception e) {
                try {
                    String path = PathUtil.getDataPath(WaitItem.sdv);
                    log.error(path, e);
                } catch (Exception e1) {
                }
            }

        }
    }
}
