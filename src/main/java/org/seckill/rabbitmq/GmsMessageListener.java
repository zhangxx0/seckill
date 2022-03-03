package org.seckill.rabbitmq;


import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;

public class GmsMessageListener implements ChannelAwareMessageListener {

    private static final Logger logger = LoggerFactory.getLogger(GmsMessageListener.class);

    public void onMessage(Message message) {
    }

    public void onMessage(Message message, Channel channel) throws Exception {
//         logger.info("接收的消息参数：{}", JSON.toJSONString(message));
        logger.info("接收的消息参数：{}", message.getBody().toString());

        try {
            // 对监听到的消息进行相应的业务处理
            String messages = new String(message.getBody(), "UTF-8");
            Thread.sleep(5000L);
            logger.info("接收到的消息：{}", messages);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);            // 确认消息
            logger.info("手动ack");
        } catch (Exception e) {
            logger.error("接收消息出现其他异常: ", e);
        }

    }
}
