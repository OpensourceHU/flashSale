package com.jesper.flashSale.infra.redis.keys;

/**
 * Created by OpensourceHU on 2021/5/29.
 */
public class OrderKey extends BasePrefix {

    public OrderKey(String prefix) {
        super(prefix);
    }

  public static OrderKey getflashSaleOrderByUidGid = new OrderKey("flashSale");
}
