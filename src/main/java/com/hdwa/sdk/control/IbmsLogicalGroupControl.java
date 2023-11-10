package com.hdwa.sdk.control;

import com.hdwa.sdk.service.IbmsLogicalGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author abao
 * @since 2023/8/15
 * IBMS逻辑编组控制
 */
@RequestMapping("/ibmsLogicalGroup")
@RestController
public class IbmsLogicalGroupControl {

    @Autowired
    private IbmsLogicalGroupService ibmsLogicalGroupService;

    /**
     * 下载逻辑编组数据
     *
     * @return
     */
    @GetMapping("/downLoadData")
    public Object downLoadData() {
        return ibmsLogicalGroupService.downLoadLogicalGroupData();
    }
}
