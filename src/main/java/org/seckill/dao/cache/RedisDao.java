package org.seckill.dao.cache;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import org.seckill.dto.SeckillExecution;
import org.seckill.entity.Seckill;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * Created by xinxin on 2017/11/4.
 */
public class RedisDao {

    private final JedisPool jedisPool;

    public RedisDao(String ip, int port) {
        jedisPool = new JedisPool(ip, port);
    }

    // 对应的模式
    private RuntimeSchema<Seckill> schema = RuntimeSchema.createFrom(Seckill.class);
    private RuntimeSchema<SeckillExecution> schema2 = RuntimeSchema.createFrom(SeckillExecution.class);

    public Seckill getSeckill(long seckillId) {
        //redis操作逻辑
        try {
            Jedis jedis = jedisPool.getResource();
            try {
                String key = "seckill:" + seckillId;
                //并没有实现哪部序列化操作
                //采用自定义序列化
                //protostuff: pojo.(带setget方法的对象)
                byte[] bytes = jedis.get(key.getBytes());
                //缓存中获取到
                if (bytes != null) {
                    // 空对象
                    Seckill seckill = schema.newMessage();
                    // 压缩空间时间都大幅优化，同时更节省CPU
                    ProtostuffIOUtil.mergeFrom(bytes, seckill, schema);
                    //seckill被反序列化

                    return seckill;
                }
            } finally {
                jedis.close();
            }
        } catch (Exception e) {

        }
        return null;
    }

    public String putSeckill(Seckill seckill) {
        try {
            Jedis jedis = jedisPool.getResource();
            try {
                String key = "seckill:" + seckill.getSeckillId();
                byte[] bytes = ProtostuffIOUtil.toByteArray(seckill, schema,
                        LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));
                //超时缓存
                int timeout = 60 * 60;//1小时
                String result = jedis.setex(key.getBytes(), timeout, bytes);

                return result;
            } finally {
                jedis.close();
            }
        } catch (Exception e) {

        }

        return null;
    }


    public SeckillExecution getSeckillExecution(long seckillId, long phone) {
        //redis操作逻辑
        try {
            Jedis jedis = jedisPool.getResource();
            try {
                String key = "seckillResult:" + seckillId + "|" + phone;
                //并没有实现哪部序列化操作
                //采用自定义序列化
                //protostuff: pojo.(带setget方法的对象)
                byte[] bytes = jedis.get(key.getBytes());
                //缓存中获取到
                if (bytes != null) {
                    // 空对象
                    SeckillExecution seckillExecution = schema2.newMessage();
                    // 压缩空间时间都大幅优化，同时更节省CPU
                    ProtostuffIOUtil.mergeFrom(bytes, seckillExecution, schema2);
                    //seckill被反序列化

                    return seckillExecution;
                }
            } finally {
                jedis.close();
            }
        } catch (Exception e) {

        }
        return null;
    }

    public String putSeckillExecution(SeckillExecution seckillExecution, long phone) {
        try {
            Jedis jedis = jedisPool.getResource();
            try {
                String key = "seckillResult:" + seckillExecution.getSeckillId() + "|" + phone;
                byte[] bytes = ProtostuffIOUtil.toByteArray(seckillExecution, schema2,
                        LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));
                //超时缓存
                int timeout = 60 * 60;//1小时
                String result = jedis.setex(key.getBytes(), timeout, bytes);

                return result;
            } finally {
                jedis.close();
            }
        } catch (Exception e) {

        }

        return null;
    }
}
