package com.jesper.flashSale.infra.redis.keys;

/**
 * Created by OpensourceHU on 2021/5/21.
 */
public class UserKey extends BasePrefix {
  //过期时间 单位: 秒
  public static final int TOKEN_EXPIRE = 3600 * 24 * 2;//默认两天
  /**
   * 需要缓存的字段
   */
  public static UserKey token = new UserKey(TOKEN_EXPIRE, "token");
  public static UserKey getById = new UserKey(0, "id");

  private UserKey(int expireSeconds, String prefix) {
    super(expireSeconds, prefix);
  }

}
