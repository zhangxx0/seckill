package org.seckill.rabbitmq;

import java.util.Date;

public class SeckillMessage {

    private long seckillId;
    private long userPhone;
    private String md5;
    private Date killTime;


    public long getSeckillId() {
        return seckillId;
    }

    public void setSeckillId(long seckillId) {
        this.seckillId = seckillId;
    }

    public long getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(long userPhone) {
        this.userPhone = userPhone;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public Date getKillTime() {
        return killTime;
    }

    public void setKillTime(Date killTime) {
        this.killTime = killTime;
    }

    public SeckillMessage() {
    }

    public SeckillMessage(long seckillId, long userPhone, String md5, Date killTime) {
        this.seckillId = seckillId;
        this.userPhone = userPhone;
        this.md5 = md5;
        this.killTime = killTime;
    }

    @Override
    public String toString() {
        return "SeckillMessage{" +
                "seckillId=" + seckillId +
                ", userPhone=" + userPhone +
                ", md5='" + md5 + '\'' +
                ", killTime=" + killTime +
                '}';
    }
}
