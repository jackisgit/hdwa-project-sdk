package com.hdwa.sdk.websocket;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class WebSocketChannelPool {
    public static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    // 收到信息后，群发消息
    // ChannelHandlerPool.channelGroup.writeAndFlush(new TextWebSocketFrame(message));

    public static Map<String, Channel> id2Channel = new ConcurrentHashMap<String, Channel>();
    public static Map<String, WebSocketChannelInfo> id2ChannelInfo = new ConcurrentHashMap<String, WebSocketChannelInfo>();

    public static void Send(String id, Object content) {
        try {
            Channel channel = id2Channel.get(id);
            if (channel != null) {
                String sendString = JSONObject.toJSONString(content, SerializerFeature.WriteMapNullValue);
                // log.info("WebSocket send " + id + "\t" + channel.localAddress().toString() + "\t" + channel.remoteAddress().toString() + "\t"
                // + sendString);
                log.info("WebSocket send " + channel.remoteAddress().toString() + "\t" + sendString);
                channel.writeAndFlush(new TextWebSocketFrame(sendString));
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}