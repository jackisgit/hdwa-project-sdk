package com.hdwa.alarm.constant;

import com.hdwa.alarm.vo.WSMessage;
import lombok.Getter;

@Getter
public enum WSMessageTypeEnum {
    /**
     * 告警(默认)
     */
    ALARM,

    /**
     * 代办事项
     */
    TODO_LIST;

    public <T> WSMessage<T> createMessage(T data) {
        return new WSMessage<>(this, data);
    }
}