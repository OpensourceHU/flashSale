package com.jesper.seckill.infra.redis;

/**
 * Created by OpensourceHU on 2021/5/29.
 */
public class OrderKey extends BasePrefix {

    public OrderKey(String prefix) {
        super(prefix);
    }
    public static OrderKey getSeckillOrderByUidGid = new OrderKey("seckill");
}
