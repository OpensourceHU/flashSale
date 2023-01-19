package com.jesper.flashSale.domain;

import com.jesper.flashSale.domain.vo.GoodsVo;
import com.jesper.flashSale.infra.db.pojos.OrderInfo;
import com.jesper.flashSale.infra.db.pojos.User;
import com.jesper.flashSale.infra.db.pojos.flashSaleOrder;
import com.jesper.flashSale.infra.redis.RedisService;
import com.jesper.flashSale.infra.redis.keys.flashSaleKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by OpensourceHU on 2021/5/23.
 */
@Service
public class flashSaleService {

    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;

    @Autowired
    RedisService redisService;

    //保证这三个操作，减库存 下订单 写入秒杀订单是一个事务
    @Transactional
    public OrderInfo flashSale(User user, GoodsVo goods) {
        //减库存
        boolean success = goodsService.reduceStock(goods);
        if (success) {
            //下订单 写入秒杀订单
            return orderService.createOrder(user, goods);
        } else {
            setGoodsOver(goods.getId());
            return null;
        }
    }

    public long getflashSaleResult(long userId, long goodsId) {
        flashSaleOrder order = orderService.getOrderByUserIdGoodsId(userId, goodsId);
        if (order != null) {
            return order.getOrderId();
        } else {
            boolean isOver = getGoodsOver(goodsId);
            if (isOver) {
                return -1;
            } else {
                return 0;
            }
        }
    }

    private void setGoodsOver(Long goodsId) {
        redisService.set(flashSaleKey.isGoodsOver, "" + goodsId, true);
    }

    private boolean getGoodsOver(long goodsId) {
        return redisService.exists(flashSaleKey.isGoodsOver, "" + goodsId);
    }
}
