package com.jesper.flashSale.application;

import com.jesper.flashSale.domain.GoodsService;
import com.jesper.flashSale.domain.OrderService;
import com.jesper.flashSale.domain.UserService;
import com.jesper.flashSale.domain.vo.GoodsVo;
import com.jesper.flashSale.domain.vo.OrderDetailVo;
import com.jesper.flashSale.infra.db.enums.result_code.CodeMsg;
import com.jesper.flashSale.infra.db.enums.result_code.Result;
import com.jesper.flashSale.infra.db.pojos.OrderInfo;
import com.jesper.flashSale.infra.db.pojos.User;
import com.jesper.flashSale.infra.redis.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by OpensourceHU on 2021/5/28.
 */
@Controller
@RequestMapping("/order")
public class OrderController {

  @Autowired
  UserService userService;

  @Autowired
  RedisService redisService;

  @Autowired
  OrderService orderService;

  @Autowired
  GoodsService goodsService;

  @RequestMapping("/detail")
  @ResponseBody
  public Result<OrderDetailVo> info(Model model, User user,
                                    @RequestParam("orderId") long orderId) {
    if (user == null) {
      return Result.error(CodeMsg.SESSION_ERROR);
    }
    OrderInfo order = orderService.getOrderById(orderId);
    if (order == null) {
      return Result.error(CodeMsg.ORDER_NOT_EXIST);
    }
    long goodsId = order.getGoodsId();
    GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
    OrderDetailVo vo = new OrderDetailVo();
    vo.setOrder(order);
    vo.setGoods(goods);
    return Result.success(vo);
  }

}
