package com.hdwa.alarm.vo;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * 报警记录
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AlarmRecordVO implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 主键
     */
    private String id;

    /**
     * 报警条目编码
     */
    private String itemCode;

    /**
     * 项目id
     */
    private String projectId;

    /**
     * 对象id
     */
    private String objId;

    /**
     * 报警对象类型ID
     */
    private String itemId;

    /**
     * 对象id
     */
    private String classCode;

    /**
     * 严重程度
     */
    private String level;

    /**
     * 报警描述
     */
    private String remark;
    /**
     * 报警名称
     */
    private String name;
    /**
     * 报警分类
     */
    private String objType;

    /**
     * 是否重点关注
     */
    private Integer concern;

    /**
     * 报警状态
     */
    private Integer state;

    /**
     * 有效期开始时间(目前置空)
     */
    private Date effectStartTime;

    /**
     * 有效期结束时间（自动恢复时间，为空表示是不过期）
     */
    private Date effectEndTime;

    /**
     * 报警条件(产生+恢复)
     */
    @JSONField(jsonDirect = true)
    private String condition;

    /**
     * 报警触发值
     */
    @JSONField(jsonDirect = true)
    private String triggerInfo;

    /**
     * 报警时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime triggerTime;

    /**
     * 报警结束值
     */
    @JSONField(jsonDirect = true)
    private String endInfo;

    /**
     * 有效期结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;


    /**
     * 创建用户
     */
    private String createUser;

    /**
     * 创建时间 时间戳
     */
    private Date createTime;

    /**
     * 更新用户
     */
    private String updateUser;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 删除标记 1:true,0:false
     */
    private Integer valid;

    /**
     * 报警性质
     */
    private Integer nature;

    /**
     * 处理方式
     */
    private Integer treatMode;

    /**
     * 处理状态
     */
    private Integer treatState;

    /**
     * 集团编码
     */
    private String groupCode;
    private String ibmsClassCode;
    private String ibmsSceneCode;

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}