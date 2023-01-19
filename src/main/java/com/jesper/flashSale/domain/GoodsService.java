package com.jesper.flashSale.domain;

import com.jesper.flashSale.domain.vo.GoodsVo;
import com.jesper.flashSale.infra.db.mapper.GoodsMapper;
import com.jesper.flashSale.infra.db.pojos.flashSaleGoods;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by OpensourceHU on 2021/5/22.
 */
@Service
public class GoodsService {

  //乐观锁冲突最大重试次数
  private static final int DEFAULT_MAX_RETRIES = 5;

  @Autowired
  GoodsMapper goodsMapper;

  /**
   * 查询商品列表
   *
   * @return
   */
  public List<GoodsVo> listGoodsVo() {
    return goodsMapper.listGoodsVo();
  }

  /**
   * 根据id查询指定商品
   *
   * @return
   */
  public GoodsVo getGoodsVoByGoodsId(long goodsId) {
    return goodsMapper.getGoodsVoByGoodsId(goodsId);
  }

  /**
   * 减少库存，每次减一
   *
   * @return
   */
  public boolean reduceStock(GoodsVo goods) {
    int numAttempts = 0;
    int ret = 0;
    flashSaleGoods sg = new flashSaleGoods();
    sg.setGoodsId(goods.getId());
    sg.setVersion(goods.getVersion());
    //自旋
    do {
      numAttempts++;
      try {
        sg.setVersion(goodsMapper.getVersionByGoodsId(goods.getId()));
        ret = goodsMapper.reduceStockByVersion(sg);
      } catch (Exception e) {
        e.printStackTrace();
      }
      if (ret != 0) {
        break;
      }
    } while (numAttempts < DEFAULT_MAX_RETRIES);

    return ret > 0;
  }
}
