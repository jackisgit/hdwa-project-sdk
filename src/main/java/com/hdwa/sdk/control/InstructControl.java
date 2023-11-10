package com.hdwa.sdk.control;

import com.hdwa.sdk.entity.InstructControlParam;
import com.hdwa.sdk.service.InstructControlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author abao
 * @since 2023/9/7
 * 指令控制
 */
@RestController
@RequestMapping("/instruct")
public class InstructControl {

    @Autowired
    private InstructControlService instructControlService;

    /**
     * 指令控制
     *
     * @param param
     * @return
     */
    @PostMapping("/control")
    public Object control(@RequestBody InstructControlParam param) {
        return instructControlService.control(param);
    }

    /**
     * 单点位控制多设备
     *
     * @param param
     * @return
     */
    @PostMapping("/controlByEquBatch")
    public Object controlByEquBatch(@RequestBody InstructControlParam param) {
        return instructControlService.controlByEquBatch(param);
    }

}
