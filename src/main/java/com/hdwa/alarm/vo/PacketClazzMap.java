package com.hdwa.alarm.vo;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class PacketClazzMap {

    public final static Map<Byte, Class<? extends Packet>> packetTypeMap = new ConcurrentHashMap<>();

    static {
        packetTypeMap.put(Command.NETTY_MESSAGE, NettyMessage.class);
    }
}