package com.jesper.flashSale.domain;

import com.jesper.flashSale.domain.vo.GoodsVo;
import com.jesper.flashSale.infra.db.mapper.OrderMapper;
import com.jesper.flashSale.infra.db.pojos.OrderInfo;
import com.jesper.flashSale.infra.db.pojos.User;
import com.jesper.flashSale.infra.db.pojos.flashSaleOrder;
import com.jesper.flashSale.infra.redis.RedisService;
import com.jesper.flashSale.infra.redis.keys.OrderKey;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by OpensourceHU on 2021/5/23.
 */
@Service
public class OrderService {

  @Autowired
  OrderMapper orderMapper;

  @Autowired
  RedisService redisService;

  public flashSaleOrder getOrderByUserIdGoodsId(long userId, long goodsId) {
    return redisService.get(OrderKey.getflashSaleOrderByUidGid, "" + userId + "_" + goodsId,
        flashSaleOrder.class);
  }

  public OrderInfo getOrderById(long orderId) {
    return orderMapper.getOrderById(orderId);
  }

  /**
   * 因为要同时分别在订单详情表和秒杀订单表都新增一条数据，加注解保证一致性
   */
  @Transactional
  public OrderInfo createOrder(User user, GoodsVo goods) {
    //OrderInfo Table
    OrderInfo orderInfo = new OrderInfo();
    orderInfo.setCreateDate(new Date());
    orderInfo.setDeliveryAddrId(0L);
    orderInfo.setGoodsCount(1);
    orderInfo.setGoodsId(goods.getId());
    orderInfo.setGoodsName(goods.getGoodsName());
    orderInfo.setGoodsPrice(goods.getGoodsPrice());
    orderInfo.setOrderChannel(1);
    orderInfo.setStatus(0);
    orderInfo.setUserId(user.getId());
    orderMapper.insert(orderInfo);
    //flashSaleOrder
    flashSaleOrder flashSaleOrder = new flashSaleOrder();
    flashSaleOrder.setGoodsId(goods.getId());
    flashSaleOrder.setOrderId(orderInfo.getId());
    flashSaleOrder.setUserId(user.getId());
    orderMapper.insertflashSaleOrder(flashSaleOrder);
    //refresh cache
    redisService.set(OrderKey.getflashSaleOrderByUidGid,
        "" + user.getId() + "_" + goods.getId(), flashSaleOrder);
    return orderInfo;
  }


}
