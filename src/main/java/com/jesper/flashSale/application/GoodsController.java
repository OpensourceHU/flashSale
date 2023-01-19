package com.jesper.flashSale.application;

import com.alibaba.druid.util.StringUtils;
import com.jesper.flashSale.domain.GoodsService;
import com.jesper.flashSale.domain.UserService;
import com.jesper.flashSale.domain.vo.GoodsDetailVo;
import com.jesper.flashSale.domain.vo.GoodsVo;
import com.jesper.flashSale.infra.db.enums.result_code.Result;
import com.jesper.flashSale.infra.db.pojos.User;
import com.jesper.flashSale.infra.redis.RedisService;
import com.jesper.flashSale.infra.redis.keys.GoodsKey;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.spring4.context.SpringWebContext;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;

/**
 * Created by OpensourceHU on 2021/5/22.
 */
@Controller
@RequestMapping("/goods")
public class GoodsController {

  @Autowired
  UserService userService;

  @Autowired
  RedisService redisService;

  @Autowired
  GoodsService goodsService;

  @Autowired
  ApplicationContext applicationContext;

  @Autowired
  ThymeleafViewResolver thymeleafViewResolver;

  /**
   * 商品列表页面
   * QPS:433
   * 1000 * 10
   */
  @RequestMapping(value = "/to_list", produces = "text/html")
  @ResponseBody
  public String list(HttpServletRequest request, HttpServletResponse response, Model model,
                     User user) {

    //取缓存
    String html = redisService.get(GoodsKey.getGoodsList, "", String.class);
    if (!StringUtils.isEmpty(html)) {
      return html;
    }
    List<GoodsVo> goodsList = goodsService.listGoodsVo();
    model.addAttribute("user", user);
    model.addAttribute("goodsList", goodsList);

    //手动渲染
    SpringWebContext ctx = new SpringWebContext(request, response,
        request.getServletContext(), request.getLocale(), model.asMap(), applicationContext);
    html = thymeleafViewResolver.getTemplateEngine().process("goods_list", ctx);

    if (!StringUtils.isEmpty(html)) {
      redisService.set(GoodsKey.getGoodsList, "", html);
    }
    //结果输出
    return html;
  }


  /**
   * 商品详情页面
   */
  @RequestMapping(value = "/to_detail2/{goodsId}", produces = "text/html")
  @ResponseBody
  public String detail2(HttpServletRequest request, HttpServletResponse response, Model model,
                        User user, @PathVariable("goodsId") long goodsId) {
    model.addAttribute("user", user);

    //取缓存
    String html = redisService.get(GoodsKey.getGoodsDetail, "" + goodsId, String.class);
    if (!StringUtils.isEmpty(html)) {
      return html;
    }

    //根据id查询商品详情
    GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
    model.addAttribute("goods", goods);

    long startTime = goods.getStartDate().getTime();
    long endTime = goods.getEndDate().getTime();
    long now = System.currentTimeMillis();

    int flashSaleStatus = 0;
    int remainSeconds = 0;

    if (now < startTime) {//秒杀还没开始，倒计时
      flashSaleStatus = 0;
      remainSeconds = (int) ((startTime - now) / 1000);
    } else if (now > endTime) {//秒杀已经结束
      flashSaleStatus = 2;
      remainSeconds = -1;
    } else {//秒杀进行中
      flashSaleStatus = 1;
      remainSeconds = 0;
    }
    model.addAttribute("flashSaleStatus", flashSaleStatus);
    model.addAttribute("remainSeconds", remainSeconds);

    //手动渲染
    SpringWebContext ctx = new SpringWebContext(request, response,
        request.getServletContext(), request.getLocale(), model.asMap(), applicationContext);
    html = thymeleafViewResolver.getTemplateEngine().process("goods_detail", ctx);
    if (!StringUtils.isEmpty(html)) {
      redisService.set(GoodsKey.getGoodsDetail, "" + goodsId, html);
    }
    return html;
  }

  /**
   * 商品详情页面
   */
  @RequestMapping(value = "/detail/{goodsId}")
  @ResponseBody
  public Result<GoodsDetailVo> detail(HttpServletRequest request, HttpServletResponse response,
                                      Model model, User user,
                                      @PathVariable("goodsId") long goodsId) {

    //根据id查询商品详情
    GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
    model.addAttribute("goods", goods);

    long startTime = goods.getStartDate().getTime();
    long endTime = goods.getEndDate().getTime();
    long now = System.currentTimeMillis();

    int flashSaleStatus = 0;
    int remainSeconds = 0;

    if (now < startTime) {//秒杀还没开始，倒计时
      flashSaleStatus = 0;
      remainSeconds = (int) ((startTime - now) / 1000);
    } else if (now > endTime) {//秒杀已经结束
      flashSaleStatus = 2;
      remainSeconds = -1;
    } else {//秒杀进行中
      flashSaleStatus = 1;
      remainSeconds = 0;
    }
    GoodsDetailVo vo = new GoodsDetailVo();
    vo.setGoods(goods);
    vo.setUser(user);
    vo.setRemainSeconds(remainSeconds);
    vo.setflashSaleStatus(flashSaleStatus);

    return Result.success(vo);
  }
}
