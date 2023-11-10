package com.hdwa.sdk.kafka;

import lombok.Data;


/**
 * @author abao
 * @since 2023/9/8
 * kafka消息内容
 */
@Data
public class MessageDto {

    private String topic;

    private String message;

    /**
     * 项目id
     */
    private String projectId;

    /**
     * 集团编码
     */
    private String groupCode;

    /**
     * 消息类型
     */
    private String msgType;
}
