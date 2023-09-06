package com.hdwa.sdk.entity;

import com.alibaba.fastjson.JSONArray;
import lombok.Data;

/**
 * @author abao
 * @since 2023/8/3
 * 路径查询接口入参数
 */
@Data
public class PathApiParam {

    /**
     * 集团编码
     */
    private String groupCode;


    /**
     * 项目id
     */
    private String projectId;

    /**
     * 接口路径
     */
    private JSONArray path;

    public PathApiParam(String groupCode, String projectId, JSONArray path) {
        this.groupCode = groupCode;
        this.projectId = projectId;
        this.path = path;
    }

    public PathApiParam(String projectId, JSONArray path) {
        this.projectId = projectId;
        this.path = path;
    }

    public PathApiParam() {
    }
}
