package com.hdwa.sdk.utils;

import com.hdwa.sdk.entity.repository.RepositoryImpl;
import lombok.extern.slf4j.Slf4j;

/**
 * @author abao
 * @since 2023/11/06
 * iot数据执行类 多线程
 */
@Slf4j
public class IotJob implements Runnable {

    /**
     * point
     */
    private String point;
    /**
     * 项目id
     */
    private RepositoryImpl repository;

    public IotJob(String point, RepositoryImpl repository) {
        this.point = point;
        this.repository = repository;
    }

    /**
     * 计算iot数据
     */
    @Override
    public void run() {
        repository.processIot(point);
    }
}
