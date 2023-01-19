package com.jesper.flashSale.infra.rabbitmq;

import com.jesper.flashSale.domain.GoodsService;
import com.jesper.flashSale.domain.OrderService;
import com.jesper.flashSale.domain.flashSaleService;
import com.jesper.flashSale.domain.vo.GoodsVo;
import com.jesper.flashSale.infra.db.pojos.User;
import com.jesper.flashSale.infra.db.pojos.flashSaleOrder;
import com.jesper.flashSale.infra.redis.RedisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by OpensourceHU on 2021/5/29.
 */
@Service
public class MQReceiver {

    private static final Logger log = LoggerFactory.getLogger(MQReceiver.class);


    @Autowired
    RedisService redisService;

    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;

  @Autowired
  flashSaleService flashSaleService;

    @RabbitListener(queues=MQConfig.QUEUE)
    public void receive(String message){
        log.info("receive message:"+message);
      flashSaleMessage m = RedisService.stringToBean(message, flashSaleMessage.class);
      User user = m.getUser();
        long goodsId = m.getGoodsId();

        GoodsVo goodsVo = goodsService.getGoodsVoByGoodsId(goodsId);
        int stock = goodsVo.getStockCount();
        if(stock <= 0){
            return;
        }

        //判断重复秒杀
      flashSaleOrder order = orderService.getOrderByUserIdGoodsId(user.getId(), goodsId);
        if(order != null) {
            return;
        }

        //减库存 下订单 写入秒杀订单
      flashSaleService.flashSale(user, goodsVo);
    }

    @RabbitListener(queues = MQConfig.TOPIC_QUEUE1)
    public void receiveTopic1(String message) {
        log.info(" topic  queue1 message:" + message);
    }

    @RabbitListener(queues = MQConfig.TOPIC_QUEUE2)
    public void receiveTopic2(String message) {
        log.info(" topic  queue2 message:" + message);
    }
}
