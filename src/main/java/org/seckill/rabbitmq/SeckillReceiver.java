package org.seckill.rabbitmq;

import com.rabbitmq.client.Channel;
import org.seckill.dao.cache.RedisDao;
import org.seckill.dto.SeckillExecution;
import org.seckill.enums.SeckillStatEnum;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.service.SekillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.Objects;

@Service
public class SeckillReceiver implements ChannelAwareMessageListener {

    private static final Logger logger = LoggerFactory.getLogger(GmsMessageListener.class);

    @Autowired
    SekillService sekillService;

    @Autowired
    private RedisDao redisDao;

    public void onMessage(Message message) {

    }

    public void onMessage(Message message, Channel channel) throws Exception {

        try {
            // 对监听到的消息进行相应的业务处理
//            logger.info("接收到的消息：{}", messages);
//            String result = new String(message.getBody(), "UTF-8");
            logger.info("接收的消息参数：{}", message);
//            SeckillMessage msg = JSONObject.parseObject(result, SeckillMessage.class);
            Jackson2JsonMessageConverter jackson2JsonMessageConverter =new Jackson2JsonMessageConverter();
            // TODO 项目启动时，这句代码会报错：string不能转换为SeckillMessage
            if(Objects.isNull(message)) {
                return;
            }
            SeckillMessage msg = (SeckillMessage) jackson2JsonMessageConverter.fromMessage(message);

            logger.info("seckillmessage:{}", msg);
            // TODO 业务处理，记录秒杀状态，便于查询秒杀是否成功接口查询使用
            try {
                // 秒杀成功
                SeckillExecution seckillExecution = sekillService.executeSeckill(msg.getSeckillId(), msg.getUserPhone(), msg.getMd5());
                redisDao.putSeckillExecution(seckillExecution, msg.getUserPhone());
            } catch (RepeatKillException e1) {
                // 重复秒杀
                SeckillExecution seckillExecution = new SeckillExecution(msg.getSeckillId(), SeckillStatEnum.REPEAT_KILL);
                redisDao.putSeckillExecution(seckillExecution, msg.getUserPhone());
            } catch (SeckillCloseException e2) {
                // 秒杀结束
                SeckillExecution seckillExecution = new SeckillExecution(msg.getSeckillId(),SeckillStatEnum.END);
                redisDao.putSeckillExecution(seckillExecution, msg.getUserPhone());
            } catch (Exception e) {
                // 内部错误
                SeckillExecution seckillExecution = new SeckillExecution(msg.getSeckillId(),SeckillStatEnum.INNER_ERROR);
                redisDao.putSeckillExecution(seckillExecution, msg.getUserPhone());
            }
            // 手动确认消息
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            logger.info("手动ack完毕");
        } catch (Exception e) {
            logger.error("接收消息出现其他异常: ", e);
        }

    }
}
