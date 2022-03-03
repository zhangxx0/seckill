package org.seckill.web;

import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.dto.SeckillResult;
import org.seckill.entity.Seckill;
import org.seckill.enums.SeckillStatEnum;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.service.SekillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * 控制器
 * Created by xinxin on 2017/11/4.
 */
@Component
@RequestMapping("//seckill") //url:模块/资源/{}/细分
public class SeckillController {

    @Autowired
    private SekillService seckillService;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public String list(Model model) {
        List<Seckill> seckillList = seckillService.getSeckillList();
        model.addAttribute("list", seckillList);
        return "list";
    }

    @RequestMapping(value = "/{seckillId}/detail", method = RequestMethod.GET)
    public String detail(@PathVariable("seckillId") Long seckillId, Model model) {
        if (seckillId == null) {
            return "redirect:/seckill/list"; // 重定向
        }
        Seckill seckill = seckillService.getById(seckillId);
        if (seckill == null) {
            return "forward:/seckill/list"; // 转发
        }
        model.addAttribute("seckill", seckill);
        return "detail";
    }

    /**
     * ajax ,json暴露秒杀接口的方法
     */
    @RequestMapping(value = "/{seckillId}/exposer",
            method = RequestMethod.GET, // TODO post???
            produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public SeckillResult<Exposer> exposer(@PathVariable("seckillId") Long seckillId) {

        SeckillResult<Exposer> result;
        try {
            Exposer exposer = seckillService.exportSeckillUrl(seckillId);
            return new SeckillResult<Exposer>(true, exposer);
        } catch (Exception e) {
            e.printStackTrace();
            result = new SeckillResult<Exposer>(false, e.getMessage());
        }
        return result;
    }

    /**
     * 获取系统时间
     */
    @RequestMapping(value = "/time/now", method = RequestMethod.GET)
    @ResponseBody
    public SeckillResult<Long> time() {
        Date date = new Date();
        return new SeckillResult<Long>(true, date.getTime());
    }

    /**
     * 执行秒杀
     */
    @RequestMapping(value = "/{seckillId}/{md5}/execution",
            method = RequestMethod.POST,
            produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public SeckillResult<SeckillExecution> execute(@PathVariable("seckillId") Long seckillId,
                                                   @PathVariable("md5") String md5,
                                                   @CookieValue(value = "userPhone", required = false) Long phone) {
        if (phone == null) {
            return new SeckillResult<SeckillExecution>(false, "未注册");
        }

        try {
            // 普通的java控制事务调用 74ms| 21ms
//            SeckillExecution seckillExecution = seckillService.executeSeckill(seckillId, phone, md5);
            // 调用数据库事务 27ms | 24ms
//            SeckillExecution seckillExecution = seckillService.executeSeckillProcedure(seckillId,phone,md5);
            // 使用消息队列实现直接返回结果(排队中)，然后前端轮询查询秒杀结果 10ms以内
            // 前端可根据请求的执行时间制定轮询的时间间隔；
            SeckillExecution seckillExecution = seckillService.executeSeckillWithRabbitMQ(seckillId, phone, md5);
            return new SeckillResult<SeckillExecution>(true, seckillExecution);
        } catch (RepeatKillException e1) {
            SeckillExecution seckillExecution = new SeckillExecution(seckillId, SeckillStatEnum.REPEAT_KILL);
            return new SeckillResult<SeckillExecution>(true, seckillExecution);
        } catch (SeckillCloseException e2) {
            SeckillExecution seckillExecution = new SeckillExecution(seckillId, SeckillStatEnum.END);
            return new SeckillResult<SeckillExecution>(true, seckillExecution);
        } catch (Exception e) {
            SeckillExecution seckillExecution = new SeckillExecution(seckillId, SeckillStatEnum.INNER_ERROR);
            return new SeckillResult<SeckillExecution>(true, seckillExecution);
        }
    }

    /**
     * 获取秒杀结果
     */
    @RequestMapping(value = "/{seckillId}/result", method = RequestMethod.GET)
    @ResponseBody
    public SeckillResult<SeckillExecution> seckillResult(@PathVariable("seckillId") Long seckillId,
                                                         @CookieValue(value = "userPhone", required = false) Long phone) {
        if (phone == null) {
            return new SeckillResult<SeckillExecution>(false, "未注册");
        }

        SeckillExecution result = seckillService.seckillResult(seckillId, phone);

        return new SeckillResult<SeckillExecution>(true, result);
    }

}
