package com.jesper.flashSale.domain.vo;

import com.jesper.flashSale.infra.db.pojos.Goods;
import java.util.Date;

/**
 * Created by OpensourceHU on 2021/5/22.
 */
public class GoodsVo extends Goods {
  private Double flashSalePrice;
  private Integer stockCount;
  private Date startDate;
  private Date endDate;
  private Integer version;

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

  public Double getflashSalePrice() {
    return flashSalePrice;
  }

  public void setflashSalePrice(Double flashSalePrice) {
    this.flashSalePrice = flashSalePrice;
  }

  public Integer getVersion() {
    return version;
  }

  public void setVersion(Integer version) {
    this.version = version;
  }
}

