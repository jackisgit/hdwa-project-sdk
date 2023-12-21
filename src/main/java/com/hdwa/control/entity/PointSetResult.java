package com.hdwa.control.entity;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @description: 控制出参
 * @since: 2023/8/17
 * @version: V4.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PointSetResult {

    /**
     * 是否虚拟点 默认值false
     */
    private Boolean virtual = false;

    /**
     * 项目编码-10位数字，如果是批量参数的下级则不需要设置
     */
    private String building;

    /**
     * 操作类型，不需要设置 pointread/pointset
     */
    private String operation;

    /**
     * 仪表编号
     */
    private String meter;

    /**
     * 功能号
     */
    private Integer funcid;

    /**
     * 操作类型为pointread：该属性是返回值；操作类型为pointset：该属性是参数
     */
    private BigDecimal data;

    /**
     * 操作开始时间
     */
    @JSONField(name = "receivetime", alternateNames = {"receiveTime", "receivetime"})
    private String receivetime;

    /**
     * 同步接口返回finish:success是成功，其他都是失败；异步接口可能返回start:sent，或者finish:{除success外的其他字符串}表示失败
     */
    private String status;

    /**
     * 操作结束时间
     */
    @JSONField(name = "endtime", alternateNames = {"endtime", "endTime"})
    private String endtime;

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }

}