package com.jesper.flashSale.domain.vo;

import com.jesper.flashSale.infra.db.pojos.User;

/**
 * Created by OpensourceHU on 2021/5/24.
 */
public class GoodsDetailVo {
  private int flashSaleStatus = 0;
  private int remainSeconds = 0;
  private GoodsVo goods;
  private User user;

  public int getflashSaleStatus() {
    return flashSaleStatus;
  }

  public void setflashSaleStatus(int flashSaleStatus) {
    this.flashSaleStatus = flashSaleStatus;
  }

  public int getRemainSeconds() {
    return remainSeconds;
  }

  public void setRemainSeconds(int remainSeconds) {
    this.remainSeconds = remainSeconds;
  }

    public GoodsVo getGoods() {
        return goods;
    }

    public void setGoods(GoodsVo goods) {
        this.goods = goods;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
