package com.hdwa.sdk.utils;

import com.hdwa.sdk.entity.repository.RepositoryBase;
import com.hdwa.sdk.entity.scene.SceneDataValue;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CountDownLatch;

@Slf4j
public class ComputeThread extends Thread {
    RepositoryBase Repository;
    SceneDataValue sv;
    CountDownLatch cdl;

    public ComputeThread(RepositoryBase Repository, SceneDataValue sv, CountDownLatch cdl) {
        super();
        this.Repository = Repository;
        this.sv = sv;
        this.cdl = cdl;
    }

    @Override
    public void run() {
        try {
            ComputeUtil.computeProperty(Repository, sv);
        } catch (Exception e) {
            try {
                String path = PathUtil.getPropertyPath(Repository, sv.rel_property);
                log.error(path + " " + e.getMessage(), e);
            } catch (Exception e1) {
                log.error(e.getMessage(), e);
            }
        } finally {
            cdl.countDown();
        }
    }
}