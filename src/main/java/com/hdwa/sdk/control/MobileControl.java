package com.hdwa.sdk.control;

import com.hdwa.sdk.service.MobileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author abao
 * @since 2024/4/2
 * 手机端接口
 */
@RestController
@RequestMapping("/mobile")
public class MobileControl {
    @Autowired
    private MobileService mobileService;

    /**
     * 系统手自动状态
     *
     * @return
     */
    @PostMapping("/systemManual")
    public Object systemManual(@RequestBody Map<String, Object> params) {
        return mobileService.systemManual(params);
    }
}
