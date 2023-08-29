package com.hdwa.control.entity;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@ApiModel(value = "点位参数与返回值", description = "注意：参数和返回值共用此类")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PointSetParam {

    @ApiModelProperty(position = 0, value = "参数：是否虚拟点，默认值false", example = "false")
    public boolean virtual = false;
    @ApiModelProperty(position = 1, value = "参数：项目编码-10位数字，如果是批量参数的下级则不需要设置", example = "\"1101070037\"")
    public String building;
    @ApiModelProperty(position = 2, value = "参数：操作类型，不需要设置", example = "\"pointread/pointset\"")
    public String operation;
    @ApiModelProperty(position = 3, value = "参数：仪表编号", example = "\"1001\"")
    public String meter;
    @ApiModelProperty(position = 4, value = "参数：功能号", example = "10101")
    public Integer funcid;
    @ApiModelProperty(position = 5, value = "操作类型为pointread：该属性是返回值；操作类型为pointset：该属性是参数", example = "13.14")
    public Double data;
    @ApiModelProperty(position = 6, value = "返回值：操作开始时间", example = "\"20201224192034\"")
    public String receivetime;
    @ApiModelProperty(position = 7, value = "返回值：同步接口返回finish:success是成功，其他都是失败；异步接口可能返回start:sent，或者finish:{除success外的其他字符串}表示失败", example = "finish:success")
    public String status;
    @ApiModelProperty(position = 8, value = "返回值：操作结束时间", example = "\"20201224192034\"")
    public String endtime;

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }

}