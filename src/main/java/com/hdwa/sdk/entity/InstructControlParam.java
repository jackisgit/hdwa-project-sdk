package com.hdwa.sdk.entity;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author abao
 * @since 2023/9/7
 * 指令控制参数
 */
@Data
public class InstructControlParam {

    /**
     * 用户id
     */
    private String userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 接口路径
     */
    private JSONArray path;

    /**
     * 控制点位
     */
    private JSONObject infoValueSet;

    /**
     * 项目id
     */
    private String projectId;

    /**
     * 集团名称
     */
    private String groupCode;


    /**
     * 单点位批量下发数据
     */
    private List<Map<String, Object>> data;

}
