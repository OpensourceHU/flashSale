package com.jesper.flashSale.application;

import com.google.common.util.concurrent.RateLimiter;
import com.jesper.flashSale.domain.GoodsService;
import com.jesper.flashSale.domain.OrderService;
import com.jesper.flashSale.domain.flashSaleService;
import com.jesper.flashSale.domain.vo.GoodsVo;
import com.jesper.flashSale.infra.db.enums.result_code.CodeMsg;
import com.jesper.flashSale.infra.db.enums.result_code.Result;
import com.jesper.flashSale.infra.db.pojos.User;
import com.jesper.flashSale.infra.db.pojos.flashSaleOrder;
import com.jesper.flashSale.infra.rabbitmq.MQSender;
import com.jesper.flashSale.infra.rabbitmq.flashSaleMessage;
import com.jesper.flashSale.infra.redis.RedisService;
import com.jesper.flashSale.infra.redis.keys.GoodsKey;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by OpensourceHU on 2021/5/22.
 */
@Controller
@RequestMapping("/flashSale")
public class flashSaleController implements InitializingBean {

  //做标记，判断该商品是否被处理过了
  private final HashMap<Long, Boolean> localOverMap = new HashMap<Long, Boolean>();
  @Autowired
  GoodsService goodsService;
  @Autowired
  OrderService orderService;
  @Autowired
  flashSaleService flashSaleService;
  @Autowired
  RedisService redisService;
  @Autowired
  MQSender sender;
  //基于令牌桶算法的限流实现类
  RateLimiter rateLimiter = RateLimiter.create(10);

  /**
   * GET POST
   * 1、GET幂等
   * 2、POST，向服务端提交数据，不是幂等
   * <p>
   * 将同步下单改为异步下单
   *
   * @param model
   * @param user
   * @param goodsId
   * @return
   */
  @RequestMapping(value = "/do_flashSale", method = RequestMethod.POST)
  @ResponseBody
  public Result<Integer> list(Model model, User user, @RequestParam("goodsId") long goodsId) {
    //令牌桶限流
    if (!rateLimiter.tryAcquire(1000, TimeUnit.MILLISECONDS)) {
      return Result.error(CodeMsg.ACCESS_LIMIT_REACHED);
    }

    if (user == null) {
      return Result.error(CodeMsg.SESSION_ERROR);
    }
    model.addAttribute("user", user);
    //内存标记，减少redis访问
    boolean over = localOverMap.get(goodsId);
    if (over) {
      return Result.error(CodeMsg.flashSale_OVER);
    }
    //预减库存
    long stock = redisService.decr(GoodsKey.getGoodsStock, "" + goodsId);//10
    if (stock < 0) {
      afterPropertiesSet();
      long stock2 = redisService.decr(GoodsKey.getGoodsStock, "" + goodsId);//10
      if (stock2 < 0) {
        localOverMap.put(goodsId, true);
        return Result.error(CodeMsg.flashSale_OVER);
      }
    }
    //判断重复秒杀
    flashSaleOrder order = orderService.getOrderByUserIdGoodsId(user.getId(), goodsId);
    if (order != null) {
      return Result.error(CodeMsg.REPEATE_flashSale);
    }
    //入队
    flashSaleMessage message = new flashSaleMessage();
    message.setUser(user);
    message.setGoodsId(goodsId);
    sender.sendflashSaleMessage(message);
    return Result.success(0);//排队中
  }

  /**
   * 系统初始化,将商品信息加载到redis和本地内存
   */
  @Override
  public void afterPropertiesSet() {
    List<GoodsVo> goodsVoList = goodsService.listGoodsVo();
    if (goodsVoList == null) {
      return;
    }
    for (GoodsVo goods : goodsVoList) {
      redisService.set(GoodsKey.getGoodsStock, "" + goods.getId(), goods.getStockCount());
      //初始化商品都是没有处理过的
      localOverMap.put(goods.getId(), false);
    }
  }

  /**
   * orderId：成功
   * -1：秒杀失败
   * 0： 排队中
   */
  @RequestMapping(value = "/result", method = RequestMethod.GET)
  @ResponseBody
  public Result<Long> flashSaleResult(Model model, User user,
                                      @RequestParam("goodsId") long goodsId) {
    model.addAttribute("user", user);
    if (user == null) {
      return Result.error(CodeMsg.SESSION_ERROR);
    }
    long orderId = flashSaleService.getflashSaleResult(user.getId(), goodsId);
    return Result.success(orderId);
  }
}
