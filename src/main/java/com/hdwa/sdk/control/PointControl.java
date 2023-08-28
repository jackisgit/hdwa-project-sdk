package com.hdwa.sdk.control;

import com.hdwa.sdk.service.PointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author abao
 * @since 2023/8/16
 * 点位控制器
 */
@RequestMapping("/point")
@RestController
public class PointControl {

    @Autowired
    private PointService pointService;

    /**
     * 下载点位数据
     *
     * @return
     */
    @RequestMapping("/downLoadData")
    public Object downLoadData() {
        return pointService.downLoadPoint();
    }
}
