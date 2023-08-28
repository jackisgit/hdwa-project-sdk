package com.hdwa.sdk.websocket;

import java.net.SocketAddress;
import java.util.Date;

public class WebSocketChannelInfo {
    public SocketAddress remoteAddress;
    public Date connectTime;

    public WebSocketChannelInfo(SocketAddress remoteAddress, Date connectTime) {
        this.remoteAddress = remoteAddress;
        this.connectTime = connectTime;
    }
}
