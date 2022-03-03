package org.seckill.rabbitmq;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 秒杀下单消息发送
 */
@Service
public class SeckillSender {

    private final Logger logger = LoggerFactory.getLogger(GmsMqSender.class);

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendMessage(SeckillMessage msg) {
        try {
            logger.info("发送信息开始");
            logger.info("send message:" + msg);
            //发送信息
            rabbitTemplate.convertAndSend("seckillExchange", "seckillKey", msg);
            logger.info("发送信息结束，发送的消息：{}", msg);
        } catch (Exception e) {
            logger.error("测试消息发送失败：", e);
        }
    }

    public static <T> String beanToString(T value) {
        if(value == null) {
            return null;
        }
        Class<?> clazz = value.getClass();
        if(clazz == int.class || clazz == Integer.class) {
            return ""+value;
        }else if(clazz == String.class) {
            return (String)value;
        }else if(clazz == long.class || clazz == Long.class) {
            return ""+value;
        }else {
            return JSON.toJSONString(value);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T stringToBean(String str, Class<T> clazz) {
        if(str == null || str.length() <= 0 || clazz == null) {
            return null;
        }
        if(clazz == int.class || clazz == Integer.class) {
            return (T)Integer.valueOf(str);
        }else if(clazz == String.class) {
            return (T)str;
        }else if(clazz == long.class || clazz == Long.class) {
            return  (T)Long.valueOf(str);
        }else {
            return JSON.toJavaObject(JSON.parseObject(str), clazz);
        }
    }

}
