package com.hdwa.alarm.vo;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

@Data
public class AlarmDefineVO {
    /**
     * 报警定义ID
     */
    private String id;
    /**
     * 报警对象ID
     */
    private String objId;
    /**
     * 系统编码
     */
    private String systemCode;
    /**
     * 系统名称
     */
    private String systemName;
    /**
     * 类型id
     */
    private String itemId;
    /**
     * 对象类型
     */
    private String classCode;
    /**
     * 项目ID
     */
    private String projectId;
    /**
     * 报警对象类型
     */
    private String objType;
    /**
     * 严重程度
     */
    private String level;
    /**
     * 触发条件（信息点详情）
     */
    private String condition;

    /**
     * 报警条目编码
     */
    private String itemCode;
    /**
     * 报警名称
     */
    private String name;
    /**
     * 备注
     */
    private String remark;
    /**
     * 屏蔽状态 1-生效 0-屏蔽
     */
    private int open;
    /**
     * 是否重点关注
     */
    private int concern;

    private String ibmsClassCode;

    private String ibmsSceneCode;

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}