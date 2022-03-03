package org.seckill.rabbitmq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GmsMqSender {
    private final Logger logger = LoggerFactory.getLogger(GmsMqSender.class);

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendMessage(String msg) {
        try {
            logger.info("发送信息开始");
            logger.info("send message:"+msg);
            //发送信息
            rabbitTemplate.convertAndSend("seckillExchange","seckillKey", msg);
            logger.info("发送信息结束，发送的消息：{}", msg);
        } catch (Exception e) {
            logger.error("测试消息发送失败：", e);
        }
    }

}
