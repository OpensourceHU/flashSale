package com.jesper.flashSale.infra.db.mapper;

import com.jesper.flashSale.domain.vo.GoodsVo;
import com.jesper.flashSale.infra.db.pojos.flashSaleGoods;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * Created by OpensourceHU on 2021/5/22.
 */
@Mapper
public interface GoodsMapper {

  @Select("select g.*, sg.stock_count, sg.start_date, sg.end_date, sg.flashSale_price, sg.version" +
      " from sk_goods_flashSale sg left join sk_goods g on sg.goods_id = g.id")
  List<GoodsVo> listGoodsVo();

  @Select("select g.*, sg.stock_count, sg.start_date, sg.end_date, sg.flashSale_price, sg.version" +
      "  from sk_goods_flashSale sg left join sk_goods g  on sg.goods_id = g.id where g.id = " +
      "#{goodsId}")
  GoodsVo getGoodsVoByGoodsId(@Param("goodsId") long goodsId);

  //stock_count > 0 和 版本号实(版本号最新)现乐观锁 防止超卖
  @Update("update sk_goods_flashSale set stock_count = stock_count - 1, version= version + 1 " +
      "where goods_id = #{goodsId} and stock_count > 0 and version = #{version}")
  int reduceStockByVersion(flashSaleGoods flashSaleGoods);

  // 获取最新版本号
  @Select("select version from sk_goods_flashSale  where goods_id = #{goodsId}")
  int getVersionByGoodsId(@Param("goodsId") long goodsId);


}
