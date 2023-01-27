package com.jesper.flashSale.infra.rabbitmq;

import com.jesper.flashSale.infra.db.pojos.User;

/**
 * Created by OpensourceHU on 2021/5/29.
 * <p>
 * 消息体
 */
public class flashSaleMessage {

  private User user;
  private long goodsId;

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public long getGoodsId() {
    return goodsId;
  }

  public void setGoodsId(long goodsId) {
    this.goodsId = goodsId;
  }
}
