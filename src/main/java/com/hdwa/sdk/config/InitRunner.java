package com.hdwa.sdk.config;


import cn.hutool.core.thread.ThreadUtil;
import com.googlecode.aviator.AviatorEvaluator;
import com.hdwa.sdk.websocket.WebSocketClientFactory;
import com.persagy.client.WebSocketClientFactory;
import com.persagy.constant.CommonConst;
import com.persagy.job.AlarmMessageThread;
import com.persagy.netty.client.NettyClient;
import com.persagy.netty.collect.IotCollectNettyClient;
import com.persagy.service.AlarmQuartzService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import javax.servlet.ServletContextAttributeListener;


@Service
@Order(1)
public class InitRunner implements ServletContextAttributeListener, CommandLineRunner {

    @Autowired
    AlarmQuartzService alarmQuartzService;
    
    @Autowired
    private WebSocketClientFactory webSocketClientFactory;

    @Override
    public void run(String... args) throws Exception {

        //5.0 开始引入了 LRU 缓存，可指定缓存的表达式个数，比如设置为最大 1 万个缓存结果：
        AviatorEvaluator.getInstance().useLRUExpressionCache(10000);
        //启动websocket,接受IOT采集庶数据
        webSocketClientFactory.retryOutCallWebSocketClient();
        //异步消费消息
        ThreadUtil.execAsync(new AlarmMessageThread(alarmQuartzService), true);
    }

}
