package com.hdwa.sdk.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author abao
 * @since 2023/9/8
 * kafka测试
 */
@RequestMapping("/msg")
@RestController
public class MessageController {

    @Autowired
    private KafkaProducer kafkaProducer;

    @PostMapping("/send")
    public Object sendMsg(@RequestBody MessageDto msg) {
        kafkaProducer.sendMessage(msg);
        return "ok";
    }
}
