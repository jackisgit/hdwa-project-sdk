package com.hdwa.sdk.control;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.hdwa.sdk.constant.BaseDecConstant;
import com.hdwa.sdk.entity.PathApiParam;
import com.hdwa.sdk.service.PathApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author abao
 * @since 2023/9/6
 * 路径接口方式查询控制器
 */
@RequestMapping("/pathApi")
@RestController
public class PathApiControl {

    @Autowired
    private PathApiService pathApiService;

    /**
     * 路径查询接口
     *
     * @param param
     * @return
     */
    @PostMapping(path = {"/post"})
    public Object post(@RequestBody PathApiParam param) {
        return pathApiService.post(param);
    }


}
