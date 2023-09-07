
/**
 * <pre>
 *
 * 描述：报警记录ID实体类定义
 * 表:w_zkt_alarm_record
 * 作者：xzw
 * 邮箱: 
 * 日期:2023-09-06 14:54:18
 * 版权：万达
 * </pre>
 */
package com.hdwa.sdk.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.redxun.common.base.entity.BaseExtEntity;
import lombok.*;
import lombok.experimental.Accessors;


@Data
@Builder
@AllArgsConstructor
@Accessors(chain = true)
@TableName(value = "w_zkt_alarm_record")
public class ZktAlarmRecord extends BaseExtEntity<String> {

    @JsonCreator
    public ZktAlarmRecord() {
    }

    //报警定义ID（报警对象ID-报警类型ID）
    @TableId(value = "ID_", type = IdType.ASSIGN_UUID)
	private String id;

    //项目ID
    @TableField(value = "PROJECT_ID_")
    private String projectId;
    //报警对象ID
    @TableField(value = "OBJ_ID_")
    private String objId;
    //报警条目编码
    @TableField(value = "ITEM_CODE_")
    private String itemCode;
    //报警定义对应最新一条报警记录状态
    @TableField(value = "STATE_")
    private String state;
    //报警定义对应最新一条报警记录ID
    @TableField(value = "ALARM_ID_")
    private String alarmId;
    //报警时间
    @TableField(value = "ALARM_TIME_")
    private String alarmTime;
    //报警生效结束时间(报警过期时间)
    @TableField(value = "EFFECT_END_TIME_")
    private String effectEndTime;
    //备注
    @TableField(value = "REMARK_")
    private String remark;
    //报警名称
    @TableField(value = "NAME_")
    private String name;
    //报警结束值
    @TableField(value = "END_INFO_")
    private String endInfo;
    //报警结束时间
    @TableField(value = "END_TIME_")
    private java.util.Date endTime;
    //报警类型ID
    @TableField(value = "ITEM_ID_")
    private String itemId;
    //公司ID
//    @TableField(value = "COMPANY_ID_")
//    private String companyId;



    @Override
    public String getPkId() {
        return id;
    }

    @Override
    public void setPkId(String pkId) {
        this.id=pkId;
    }


    /**
    生成子表属性的Array List
    */

}



