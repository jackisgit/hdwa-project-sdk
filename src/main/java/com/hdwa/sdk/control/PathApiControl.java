package com.hdwa.sdk.control;

import com.hdwa.sdk.entity.PathApiParam;
import com.hdwa.sdk.service.PathApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

    @Autowired
    private HttpServletResponse response;

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


    /**
     * 路径查询接口筛选分页
     *
     * @param param
     * @return
     */
    @PostMapping(path = {"/postPage"})
    public Object postPage(@RequestBody PathApiParam param) {
        return pathApiService.postPage(param);
    }

    /**
     * 路径查询数据筛选数据导出
     *
     * @param param
     */
    @PostMapping(path = {"/postExport"})
    public void postExport(@RequestBody PathApiParam param) {
        pathApiService.postExport(param, response);
    }

}
