package com.hdwa.sdk.enums;

/**
 * @author abao
 * @since 2023/8/16
 * 点位配置枚举
 */
public enum PointEnum {

    modularCode("模块编码"),
    ibmsCode("IBMS编码"),
    pointType("信息点类型"),
    pointCode("信息点编码"),
    pointName("信息点名称"),
    pointAlias("信息点别名"),
    isShow("是否显示"),
    isKey("是否关键参数"),
    isBatchControl("是否批量控制"),
    controlFeedbackCode("控制反馈点编码"),
    controlFeedback("控制反馈点"),
    forceModel("强制模式"),
    forceModelFeedback("强制模式反馈"),
    controlPoint("控制点");

    private final String name;

    PointEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
