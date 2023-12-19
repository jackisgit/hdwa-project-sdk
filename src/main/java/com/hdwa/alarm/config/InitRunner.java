package com.hdwa.alarm.config;

import cn.hutool.core.thread.ThreadUtil;
import com.googlecode.aviator.AviatorEvaluator;
import com.hdwa.alarm.service.AlarmQuartzServiceImpl;
import com.hdwa.alarm.websocket.WebSocketClientFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.ServletContextAttributeListener;

@Component
@Order(1)
public class InitRunner implements ServletContextAttributeListener, CommandLineRunner {

    @Autowired
    AlarmQuartzServiceImpl alarmQuartzService;

    @Autowired
    private WebSocketClientFactory webSocketClientFactory;

    @Override
    public void run(String... args) throws Exception {
        //5.0 开始引入了 LRU 缓存，可指定缓存的表达式个数，比如设置为最大 1 万个缓存结果：
        AviatorEvaluator.getInstance().useLRUExpressionCache(10000);
        //启动websocket,接受IOT采集数据
        webSocketClientFactory.retryOutCallWebSocketClient();
        //异步消费消息
        ThreadUtil.execAsync(new AlarmMessageThread(alarmQuartzService), true);
    }
}