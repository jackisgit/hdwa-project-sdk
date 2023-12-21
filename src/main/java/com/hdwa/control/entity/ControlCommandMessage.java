package com.hdwa.control.entity;

import com.alibaba.fastjson.JSONObject;
import com.hdwa.control.constant.CommonConst;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ControlCommandMessage implements Serializable {

    /*
    唯一标识
     */
    private long streamId;
    private String channelId;
    /*
     *操作类型编码
     * 200 - 连接建立，此时的source= 当前项目的projectId
     * 1 -云端发送执行给边缘端
     * 2 -边缘端发送指令反馈给云端
     * 3 -通知消息
     * 4 -边缘端处理发生异常
     */
    private int opCode;
    /**
     * 请求来源
     */
    private String groupCode;
    private String projectId;

    /*
     * 用于项目控制程序清除之前的时间和命令
     */
    private String clearBeforeTimeFlag;
    /*
    传输内容
     */
    private List<ControlCommand> content;
    /*
    成功标识
     */
    private Boolean success;

    /**
     * 备注说明
     */
    private String remark;
    /**
     * 错误摘要
     */
    private String errorMessage;

    public ControlCommandMessage(int opCode) {
        this.opCode = opCode;
        this.projectId = CommonConst.projectId;
        this.groupCode = CommonConst.groupCode;
        this.success = true;
    }

    public ControlCommandMessage(int opCode, boolean sucess) {
        this.opCode = opCode;
        this.projectId = CommonConst.projectId;
        this.groupCode = CommonConst.groupCode;
        this.success = true;
    }

    /**
     * ControlCommandMessage
     *
     * @param opCode  操作码
     * @param content 指令
     * @param success 结果
     */
    public ControlCommandMessage(int opCode, List<ControlCommand> content, Boolean success) {
        this.opCode = opCode;
        this.projectId = CommonConst.projectId;
        this.groupCode = CommonConst.groupCode;
        this.content = content;
        this.success = success;
    }

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }

}