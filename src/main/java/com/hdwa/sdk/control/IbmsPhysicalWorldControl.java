package com.hdwa.sdk.control;

import com.hdwa.sdk.service.IbmsPhysicalWorldService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author abao
 * @since 2023/8/15
 * IBMS物理世界控制器
 */
@RequestMapping("/ibmsPhysicalWorld")
@RestController
public class IbmsPhysicalWorldControl {
    @Autowired
    private IbmsPhysicalWorldService ibmsPhysicalWorldService;

    /**
     * 下载物理世界数据
     *
     * @return
     */
    @GetMapping("/downLoadData")
    public Object downLoadData() {
        return ibmsPhysicalWorldService.downLoadIbmsPhysicalWorldData();
    }
}
