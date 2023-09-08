package com.hdwa.control.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.hdwa.control.constant.CommonConst;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @description: 批量控制入参
 * @since: 2023/8/17
 * @version: V4.0
 */
@Data
@Builder
@AllArgsConstructor
public class BatchPointSetParam {

    /**
     * 项目编码-10位数字，如果是批量参数的下级则不需要设置
     */
    private String building;

    /**
     * 点位数组
     */
    private List<PointSetParam> points;

    /**
     * 操作结束时间
     */
    @JSONField(name = "receivetime", alternateNames = {"receiveTime", "receivetime"})
    private String receiveTime;

    /**
     * 同步接口返回finish:success是成功，其他都是失败；异步接口可能返回start:sent，或者finish:{除success外的其他字符串}表示失败
     */
    private String status;

    public BatchPointSetParam() {
    }

    public BatchPointSetParam(List<PointSetParam> points) {
        this.building = CommonConst.iotId;
        this.points = points;
    }

}