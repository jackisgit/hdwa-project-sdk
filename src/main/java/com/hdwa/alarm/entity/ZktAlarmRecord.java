package com.hdwa.alarm.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "zkt_alarm_record")
public class ZktAlarmRecord {

    //报警定义ID（报警对象ID-报警类型ID）
    @TableId(value = "ID", type = IdType.ASSIGN_UUID)
	private String id;

    //项目ID
    @TableField(value = "PROJECT_ID")
    private String projectId;
    //报警对象ID
    @TableField(value = "OBJ_ID")
    private String objId;
    //报警条目编码
    @TableField(value = "ITEM_CODE")
    private String itemCode;
    //报警定义对应最新一条报警记录状态
    @TableField(value = "STATE")
    private String state;
    //报警定义对应最新一条报警记录ID
    @TableField(value = "ALARM_ID")
    private String alarmId;
    //报警时间
    @TableField(value = "ALARM_TIME")
    private String alarmTime;
    //报警生效结束时间(报警过期时间)
    @TableField(value = "EFFECT_END_TIME")
    private String effectEndTime;
    //备注
    @TableField(value = "REMARK")
    private String remark;
    //报警名称
    @TableField(value = "NAME")
    private String name;
    //报警结束值
    @TableField(value = "END_INFO")
    private String endInfo;
    //报警结束时间
    @TableField(value = "END_TIME")
    private java.util.Date endTime;
    //报警类型ID
    @TableField(value = "ITEM_ID")
    private String itemId;
}


