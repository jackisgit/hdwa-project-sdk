package com.hdwa.sdk.kafka;

import com.hdwa.control.kafka.KafkaProducerControl;
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
    private KafkaProducerSdk kafkaProducerSdk;

    @Autowired
    private KafkaProducerControl kafkaProducerControl;


    @PostMapping("/send")
    public Object sendMsg(@RequestBody MessageDto msg) {
        kafkaProducerSdk.sendMessage(msg);
        return "ok";
    }


    @PostMapping("/sendControl")
    public Object sendControl(@RequestBody MessageDto msg) {
        kafkaProducerControl.send(msg.getTopic(), msg.getMessage());
        return "ok";
    }
}
