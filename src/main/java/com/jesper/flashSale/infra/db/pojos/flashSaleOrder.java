package com.jesper.flashSale.infra.db.pojos;

/**
 * Created by OpensourceHU on 2021/5/22.
 */
public class flashSaleOrder {
  private Long id;
  private Long userId;
  private Long orderId;
  private Long goodsId;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public Long getOrderId() {
    return orderId;
  }

  public void setOrderId(Long orderId) {
    this.orderId = orderId;
  }

  public Long getGoodsId() {
    return goodsId;
  }

  public void setGoodsId(Long goodsId) {
    this.goodsId = goodsId;
  }
}
