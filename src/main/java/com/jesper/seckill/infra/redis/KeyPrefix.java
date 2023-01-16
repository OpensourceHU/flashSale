package com.jesper.seckill.infra.redis;

/**
 * Created by OpensourceHU on 2021/5/21.
 *
 * 缓冲key前缀
 */
public interface KeyPrefix {

  /**
   * 有效期
   *
   * @return
   */
  int expireSeconds();

    /**
     * 前缀
     * @return
     */
    String getPrefix();
}
