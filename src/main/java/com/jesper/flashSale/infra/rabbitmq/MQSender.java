package com.jesper.flashSale.infra.rabbitmq;

import com.jesper.flashSale.infra.redis.RedisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by OpensourceHU on 2021/5/29.
 */
@Service
public class MQSender {

  private static final Logger log = LoggerFactory.getLogger(MQSender.class);

  @Autowired
  AmqpTemplate amqpTemplate;

  public void sendTopic(Object message) {
    String msg = RedisService.beanToString(message);
    log.info("send topic message:" + msg);
    amqpTemplate.convertAndSend(MQConfig.TOPIC_EXCHANGE, "topic.key1", msg + "1");
    amqpTemplate.convertAndSend(MQConfig.TOPIC_EXCHANGE, "topic.key2", msg + "2");
  }

  public void sendFlashSaleMessage(flashSaleMessage message) {
    String msg = RedisService.beanToString(message);
    log.info("send message:" + msg);
    amqpTemplate.convertAndSend(MQConfig.QUEUE, msg);
  }
}
