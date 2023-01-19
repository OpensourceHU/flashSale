package com.jesper.flashSale.infra.db.pojos;

import java.util.Date;

/**
 * Created by OpensourceHU on 2021/5/22.
 */
public class flashSaleGoods {
  private Long id;
  private Long goodsId;
  //库存数
  private Integer stockCount;
  //版本号
  private int version;
  private Date startDate;
  private Date endDate;

  public int getVersion() {
    return version;
  }

  public void setVersion(int version) {
    this.version = version;
  }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }

    public Integer getStockCount() {
        return stockCount;
    }

    public void setStockCount(Integer stockCount) {
        this.stockCount = stockCount;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
}
