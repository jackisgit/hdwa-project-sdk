package com.hdwa.sdk.control;

import com.hdwa.sdk.entity.repository.RepositoryImpl;
import com.hdwa.sdk.service.ConfigApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author abao
 * @since 2023/9/5
 * 接口配置控制器
 */
@RestController
@RequestMapping("/configApi")
public class ConfigApiControl {
    @Autowired
    private ConfigApiService configApiService;

    /**
     * 下载接口配置文件
     *
     * @return
     */
    @GetMapping("/downLoadConfig")
    public Object downLoadConfig() {
        return configApiService.downLoadConfig();
    }

    /**
     * 下载接口配置文件
     *
     * @return
     */
    @GetMapping("/loadConfigData")
    public void loadConfigData() {
        configApiService.loadConfigData(new RepositoryImpl());
    }

}
