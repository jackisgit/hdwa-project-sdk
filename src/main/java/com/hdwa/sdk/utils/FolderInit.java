package com.hdwa.sdk.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Files;

/**
 * @author abao
 * @since 2023/8/9
 * 初始目录
 */
@Slf4j
@Component
public class FolderInit implements CommandLineRunner {

    @Value("${project.id}")
    private String projectId;

    @Value("${project.groupCode}")
    private String groupCode;

    @Value("${dirName.config}")
    private String config;

    @Value("${dirName.point}")
    private String point;

    @Value("${dirName.physicalWorld}")
    private String physicalWorld;

    @Value("${dirName.ibmsPhysicalWorld}")
    private String ibmsPhysicalWorld;

    @Value("${dirName.ibmsLogicalGroup}")
    private String ibmsLogicalGroup;


    @Value("${dirName.temp}")
    private String temp;

    @Override
    public void run(String... args) throws Exception {
        log.warn("*****初始数据文件夹");
        // 创建根目录文件夹
        File root = new File(groupCode + File.separator + projectId);
        if (!root.exists()) {
            log.warn(root.getPath());
            Files.createDirectories(root.toPath());
        }

        //json接口配置文件夹
        File configPath = new File(root.getPath() + File.separator + config);
        if (!configPath.exists()) {
            log.warn(configPath.getPath());
            Files.createDirectories(configPath.toPath());
        }


        //点位数据文件夹
        File pointPath = new File(root.getPath() + File.separator + point);
        if (!pointPath.exists()) {
            log.warn(pointPath.getPath());
            Files.createDirectories(pointPath.toPath());
        }

        //物理世界数据文件夹
        File physicalWorldPath = new File(root.getPath() + File.separator + physicalWorld);
        if (!physicalWorldPath.exists()) {
            log.warn(physicalWorldPath.getPath());
            Files.createDirectories(physicalWorldPath.toPath());
        }

        //ibms物理世界数据文件夹
        File ibmsPhysicalWorldPath = new File(root.getPath() + File.separator + ibmsPhysicalWorld);
        if (!ibmsPhysicalWorldPath.exists()) {
            log.warn(ibmsPhysicalWorldPath.getPath());
            Files.createDirectories(ibmsPhysicalWorldPath.toPath());
        }

        //ibms逻辑分组数据文件夹
        File ibmsLogicalGroupPath = new File(root.getPath() + File.separator + ibmsLogicalGroup);
        if (!ibmsLogicalGroupPath.exists()) {
            log.warn(ibmsLogicalGroupPath.getPath());
            Files.createDirectories(ibmsLogicalGroupPath.toPath());
        }


        //临时数据文件夹
        File tempPath = new File(root.getPath() + File.separator + temp);
        if (!tempPath.exists()) {
            log.warn(tempPath.getPath());
            Files.createDirectories(tempPath.toPath());
        }
    }
}
