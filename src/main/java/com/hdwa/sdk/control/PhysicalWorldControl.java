package com.hdwa.sdk.control;

import com.hdwa.sdk.entity.repository.RepositoryImpl;
import com.hdwa.sdk.service.PhysicalWorldService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author abao
 * @since 2023/8/10
 * 物理世界控制器
 */
@RequestMapping("/physicalWorld")
@RestController
public class PhysicalWorldControl {
    @Autowired
    private PhysicalWorldService physicalWorldService;

    /**
     * 下载物理世界数据
     *
     * @return
     */
    @GetMapping("/downLoadData")
    public Object downLoadData() {
        return physicalWorldService.downLoadPhysicalWorldData();
    }

    /**
     * 加载物理世界数据
     *
     * @return
     */
    @GetMapping("/loadPhysicalWorldData")
    public Object loadPhysicalWorldData() {


        return physicalWorldService.loadPhysicalWorldData();
    }

}
