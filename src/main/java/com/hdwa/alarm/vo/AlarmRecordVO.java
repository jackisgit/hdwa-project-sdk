package com.hdwa.alarm.vo;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
@ApiModel(value = "报警记录")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AlarmRecordVO implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 主键
     */
    @ApiModelProperty(value = "主键")
    private String id;

    /**
     * 报警条目编码
     */
    @ApiModelProperty(value = "报警条目编码")
    private String itemCode;

    /**
     * 项目id
     */
    @ApiModelProperty(value = "项目id")
    private String projectId;

    /**
     * 对象id
     */
    @ApiModelProperty(value = "对象id")
    private String objId;

    /**
     * 报警对象类型ID
     */
    @ApiModelProperty(value = "报警对象类型ID")
    private String itemId;

    /**
     * 对象id
     */
    @ApiModelProperty(value = "对象类型")
    private String classCode;

    /**
     * 严重程度
     */
    @ApiModelProperty(value = "严重程度")
    private String level;

    /**
     * 报警描述
     */
    @ApiModelProperty(value = "报警描述")
    private String remark;
    /**
     * 报警名称
     */
    @ApiModelProperty(value = "报警名称")
    private String name;
    /**
     * 报警分类
     */
    @ApiModelProperty(value = "报警分类")
    private String objType;

    /**
     * 是否重点关注
     */
    @ApiModelProperty(value = "是否重点关注")
    private Integer concern;

    /**
     * 报警状态
     */
    @ApiModelProperty(value = "报警状态")
    private Integer state;

    /**
     * 有效期开始时间(目前置空)
     */
    @ApiModelProperty(value = "有效期开始时间")
    private Date effectStartTime;

    /**
     * 有效期结束时间（自动恢复时间，为空表示是不过期）
     */
    @ApiModelProperty(value = "有效期结束时间")
    private Date effectEndTime;

    /**
     * 报警条件(产生+恢复)
     */
    @ApiModelProperty(value = "报警触发条件")
    @JSONField(jsonDirect = true)
    private String condition;

    /**
     * 报警触发值
     */
    @ApiModelProperty(value = "报警触发值")
    @JSONField(jsonDirect = true)
    private String triggerInfo;

    /**
     * 报警时间
     */
    @ApiModelProperty(value = "报警时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime triggerTime;

    /**
     * 报警结束值
     */
    @ApiModelProperty(value = "报警结束值")
    @JSONField(jsonDirect = true)
    private String endInfo;

    /**
     * 有效期结束时间
     */
    @ApiModelProperty(value = "报警自动过期时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;


    /**
     * 创建用户
     */
    @ApiModelProperty(value = "创建用户")
    private String createUser;

    /**
     * 创建时间 时间戳
     */
    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    /**
     * 更新用户
     */
    @ApiModelProperty(value = "更新用户")
    private String updateUser;

    /**
     * 更新时间
     */
    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    /**
     * 删除标记 1:true,0:false
     */
    @ApiModelProperty(value = "删除标记 1:true,0:false")
    private Integer valid;

    /**
     * 报警性质
     */
    @ApiModelProperty(value = "报警性质")
    private Integer nature;

    /**
     * 处理方式
     */
    @ApiModelProperty(value = "处理方式")
    private Integer treatMode;

    /**
     * 处理状态
     */
    @ApiModelProperty(value = "处理状态")
    private Integer treatState;

    /**
     * 集团编码
     */
    @ApiModelProperty(value = "集团编码")
    private String groupCode;

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }

    private String ibmsClassCode;

    private String ibmsSceneCode;
}