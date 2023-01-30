package com.jesper.flashSale.infra.redis.keys;

/**
 * Created by OpensourceHU on 2021/5/29.
 */
public class flashSaleKey extends BasePrefix {
  public static flashSaleKey isGoodsOver = new flashSaleKey("go");

  private flashSaleKey(String prefix) {
    super(prefix);
  }
}
