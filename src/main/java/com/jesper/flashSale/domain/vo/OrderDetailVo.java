package com.jesper.flashSale.domain.vo;

import com.jesper.flashSale.infra.db.pojos.OrderInfo;

/**
 * Created by OpensourceHU on 2021/5/28.
 */
public class OrderDetailVo {
  private GoodsVo goods;
  private OrderInfo order;

  public GoodsVo getGoods() {
    return goods;
  }

  public void setGoods(GoodsVo goods) {
    this.goods = goods;
  }

  public OrderInfo getOrder() {
    return order;
  }

  public void setOrder(OrderInfo order) {
    this.order = order;
  }
}
