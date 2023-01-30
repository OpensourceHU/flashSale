package com.jesper.flashSale.infra.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Service
public class RedisPoolFactory {

  @Autowired
  private static RedisConfig redisConfig;

  private static volatile JedisPool INSTANCE;

  private static JedisPool init() {
    JedisPoolConfig config = new JedisPoolConfig();
    config.setMaxIdle(redisConfig.getPoolMaxIdle());
    config.setMaxTotal(redisConfig.getPoolMaxTotal());
    config.setMaxWaitMillis(redisConfig.getPoolMaxWait() * 1000L);
    JedisPool jp = new JedisPool(config, redisConfig.getHost(), redisConfig.getPort(),
        redisConfig.getTimeout() * 1000, redisConfig.getPassword(), 0);
    return jp;
  }

  public static JedisPool getJedisPool() {
    if (INSTANCE == null) {
      synchronized (RedisPoolFactory.class) {
        if (INSTANCE == null) {
          INSTANCE = init();
        }
      }
    }
    return INSTANCE;
  }

}
