package com.hdwa.control.entity;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.hdwa.control.constant.CommonConst;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * @description: 控制指令
 * @since: 2023/8/17
 * @version: V4.0
 */
@Data
@Accessors(chain = true)
@AllArgsConstructor
public class ControlCommand implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    private String id;
    /**
     * projectId
     */
    private String projectId;
    /**
     * 功能号
     */
    private String funcId;
    /**
     * 表号id
     */
    private String meterId;
    /**
     * 执行指令的时间，手动时为空
     */
    private String commandTime;
    /**
     * 指令执行结果,0-默认；1-确认请求；10-指令已经下发；2-执行成功；3-执行失败；4-超时不响应，99-重复命令不执行；
     */
    private Integer commandResult;
    /**
     * createTime
     */
    private Date createTime;
    /**
     * updateTime
     */
    private Date updateTime;
    /**
     * 扩展字段
     */
    private String remark;
    /**
     * 信息点要执行的指令
     */
    @JSONField(jsonDirect = true)
    private String pointAction;
    /**
     * 对象id
     */
    private String objectId;

    /**
     * 设备手自动状态key
     */
    private String manualAutoSet;


    /**
     * 模式id
     */
    private String modelId;

    /**
     * 模式名称
     */
    private String modelName;

    /**
     * 日程/日程详情名称
     */
    private String planName;

    /**
     * 设备名称
     */
    private String objName;

    /**
     * 设备编码
     */
    private String localId;

    /**
     * 逻辑编组名称
     */
    private String logicalGroupingName;

    /**
     * 产品模块编码
     */
    private String ibmsSceneCode;

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }

    public ControlCommand() {
    }

    public ControlCommand(String id, Integer commandResult) {
        this.id = id;
        this.commandResult = commandResult;
        this.projectId = CommonConst.projectId;
    }

    public ControlCommand(String id, Integer commandResult, String pointAction) {
        this.id = id;
        this.commandResult = commandResult;
        this.projectId = CommonConst.projectId;
        this.pointAction = pointAction;
    }

}