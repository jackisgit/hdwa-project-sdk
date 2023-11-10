package com.hdwa.sdk.kafka;

import com.alibaba.fastjson.JSON;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;

/**
 * @author abao
 * @since 2023/9/8
 * 消息序列号
 */
public class MsgJsonSerializer implements Serializer<MessageDto> {

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {

    }

    @Override
    public byte[] serialize(String topic, MessageDto msg) {
        return JSON.toJSONBytes(msg);
    }

    @Override
    public void close() {
    }
}

