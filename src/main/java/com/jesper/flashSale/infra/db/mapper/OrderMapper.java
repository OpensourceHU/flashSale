package com.jesper.flashSale.infra.db.mapper;

import com.jesper.flashSale.infra.db.pojos.OrderInfo;
import com.jesper.flashSale.infra.db.pojos.flashSaleOrder;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectKey;

/**
 * Created by OpensourceHU on 2021/5/23.
 */
@Mapper
public interface OrderMapper {


  @Select("select * from sk_order where user_id = #{userId} and goods_id = #{goodsId}")
  flashSaleOrder getOrderByUserIdGoodsId(@Param("userId") long userId,
                                         @Param("goodsId") long goodsId);


  /**
   * 通过@SelectKey使insert成功后返回主键id，也就是订单id
   *
   * @param orderInfo
   * @return
   */
  @Insert(
      "insert into sk_order_info(user_id, goods_id, goods_name, goods_count, goods_price, " +
          "order_channel, status, create_date)values("
          +
          "#{userId}, #{goodsId}, #{goodsName}, #{goodsCount}, #{goodsPrice}, #{orderChannel}," +
          "#{status},#{createDate} )")
  @SelectKey(keyColumn = "id", keyProperty = "id", resultType = long.class, before = false,
      statement = "select last_insert_id()")
  long insert(OrderInfo orderInfo);


  @Insert(
      "insert into sk_order (user_id, goods_id, order_id)values(#{userId}, #{goodsId}, #{orderId})")
  int insertflashSaleOrder(flashSaleOrder order);

  @Select("select * from sk_order_info where id = #{orderId}")
  OrderInfo getOrderById(@Param("orderId") long orderId);

}
