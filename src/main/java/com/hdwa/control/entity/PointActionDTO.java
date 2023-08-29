package com.hdwa.control.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PointActionDTO implements Serializable {

    @ApiModelProperty(value = "信息点名称")
    private String name;

    @ApiModelProperty(value = "信息点code")
    private String code;

    @ApiModelProperty(value = "信息点值")
    private String value;

}