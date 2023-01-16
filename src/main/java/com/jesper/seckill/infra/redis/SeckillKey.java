package com.jesper.seckill.infra.redis;

/**
 * Created by OpensourceHU on 2021/5/29.
 */
public class SeckillKey extends BasePrefix {
    private SeckillKey(String prefix) {
        super(prefix);
    }

    public static SeckillKey isGoodsOver = new SeckillKey("go");
}
