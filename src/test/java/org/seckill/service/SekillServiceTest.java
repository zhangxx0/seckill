package org.seckill.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entity.Seckill;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

/**
 * Created by xinxin on 2017/11/4.
 */
@RunWith(SpringJUnit4ClassRunner.class)
//告诉junit spring的配置文件
@ContextConfiguration({"classpath:spring/spring-dao.xml",
        "classpath:spring/spring-service.xml"})
public class SekillServiceTest {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SekillService seckillSevice;

    @Test
    public void getSeckillList() throws Exception {
        List<Seckill> list = seckillSevice.getSeckillList();
        System.out.println(list);
    }

    @Test
    public void getById() throws Exception {
        Seckill seckill = seckillSevice.getById(1000);
        System.out.println(seckill);
    }

    @Test
    public void exportSeckillUrl() throws Exception {
        long seckillId = 1000;
        Exposer exposer = seckillSevice.exportSeckillUrl(seckillId);
        System.out.println(exposer.toString());
    }

    @Test
    public void executeSeckill() throws Exception {
        long seckillId = 1000;
        long userPhone = 18706480736L;
        String md5 = "bf204e2683e7452aa7db1a50b5713bae";

        try {
            SeckillExecution seckillExecution = seckillSevice.executeSeckill(seckillId, userPhone, md5);

            System.out.println(seckillExecution);
        } catch (RepeatKillException e) {
            e.printStackTrace();
        } catch (SeckillCloseException e1) {
            e1.printStackTrace();
        }
    }

    @Test//完整逻辑代码测试，注意可重复执行
    public void testSeckillLogic() throws Exception {
        long seckillId = 1000;
        Exposer exposer = seckillSevice.exportSeckillUrl(seckillId);
        if (exposer.isExposed()) {

            System.out.println(exposer);

            long userPhone = 13476191876L;
            String md5 = exposer.getMd5();

            try {
                SeckillExecution seckillExecution = seckillSevice.executeSeckill(seckillId, userPhone, md5);
                System.out.println(seckillExecution);
            } catch (RepeatKillException e) {
                e.printStackTrace();
            } catch (SeckillCloseException e1) {
                e1.printStackTrace();
            }
        } else {
            //秒杀未开启
            System.out.println(exposer);
        }
    }

}