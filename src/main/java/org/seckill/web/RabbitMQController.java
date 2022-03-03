package org.seckill.web;

import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.rabbitmq.GmsMqSender;
import org.seckill.service.SekillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Component
@RequestMapping("//rabbitmq") //url:模块/资源/{}/细分
public class RabbitMQController {

    @Autowired
    private GmsMqSender mqSender;

    @Autowired
    private SekillService seckillService;

    @RequestMapping(value = "/test", method = RequestMethod.GET)
    @ResponseBody
    public String list() {
//        mqSender.setExchangeName();
        mqSender.sendMessage("zxxtest");
        return "OK";
    }

    @RequestMapping(value = "/kill", method = RequestMethod.GET)
    @ResponseBody
    public String kill() {
        long seckillId = 1003;
        long phone = 18706480736L;
        Exposer exposer = seckillService.exportSeckillUrl(seckillId);
        if (exposer.isExposed()) {
            String md5 = exposer.getMd5();
            SeckillExecution seckillExecution = seckillService.executeSeckillWithRabbitMQ(seckillId, phone, md5);
        }
        return "OK";
    }

}
