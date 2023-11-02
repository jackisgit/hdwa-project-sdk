package com.hdwa.alarm.vo;

import com.hdwa.alarm.constant.WSMessageTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WSMessage<T> {

    /**
     * 消息类型
     */
    @NotNull
    private WSMessageTypeEnum type;

    /**
     * 数据内容
     */
    @NotNull
    private T data;
}