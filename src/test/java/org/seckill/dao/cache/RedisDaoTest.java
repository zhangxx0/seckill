package org.seckill.dao.cache;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.dao.SeckillDao;
import org.seckill.entity.Seckill;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

/**
 * Created by xinxin on 2017/11/4.
 */
@RunWith(SpringJUnit4ClassRunner.class)
//告诉junit spring的配置文件
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class RedisDaoTest {

    @Autowired
    private RedisDao redisDao;
    @Autowired
    private SeckillDao seckillDao;

    private Long id = 1000L;

    @Test
    public void testSeckill() throws Exception {
        // get and put
        Seckill seckill = redisDao.getSeckill(id);
        if (seckill == null) {
            seckill =seckillDao.queryById(id);
            if (seckill != null) {
                String result = redisDao.putSeckill(seckill);
                System.out.println("put redis:" + result);
                seckill = redisDao.getSeckill(id);
            }
            System.out.println(seckill);
        }

    }

    @Test
    public void getSeckill() throws Exception {

    }

    @Test
    public void putSeckill() throws Exception {

    }

}