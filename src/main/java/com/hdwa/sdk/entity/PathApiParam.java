package com.hdwa.sdk.entity;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
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

    /**
     * 点位路径
     */
    private JSONArray pointPath;


    /**
     * 项目id
     */
    private JSONObject params;

    private boolean page;
    private Integer pageSize;
    private Integer pageIndex;

    public PathApiParam(String groupCode, String projectId, JSONArray path, JSONObject params, boolean page, Integer pageSize, Integer pageIndex) {
        this.groupCode = groupCode;
        this.projectId = projectId;
        this.path = path;
        this.params = params;
        this.page = page;
        this.pageSize = pageSize;
        this.pageIndex = pageIndex;
    }

    public PathApiParam(String groupCode, String projectId, JSONArray path, JSONObject params) {
        this.groupCode = groupCode;
        this.projectId = projectId;
        this.path = path;
        this.params = params;
    }

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
