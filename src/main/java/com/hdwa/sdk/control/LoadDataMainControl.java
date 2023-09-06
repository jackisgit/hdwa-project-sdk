package com.hdwa.sdk.control;

import com.hdwa.sdk.service.LoadDataMainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author abao
 * @since 2023/8/30
 * 加载数据入口控制器
 */
@RestController
@RequestMapping("/loadData")
public class LoadDataMainControl {

    @Autowired
    private LoadDataMainService loadDataMainService;

    @GetMapping("/main")
    public Object loadDataMain(){
        loadDataMainService.loadDataMain();
        return "ok";
    }
}
