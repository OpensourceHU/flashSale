package com.jesper.flashSale.domain;

import com.alibaba.druid.util.StringUtils;
import com.jesper.flashSale.domain.vo.LoginVo;
import com.jesper.flashSale.infra.db.enums.exception.GlobalException;
import com.jesper.flashSale.infra.db.enums.result_code.CodeMsg;
import com.jesper.flashSale.infra.db.mapper.UserMapper;
import com.jesper.flashSale.infra.db.pojos.User;
import com.jesper.flashSale.infra.redis.RedisService;
import com.jesper.flashSale.infra.redis.keys.UserKey;
import com.jesper.flashSale.infra.utils.util.MD5Util;
import com.jesper.flashSale.infra.utils.util.UUIDUtil;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by OpensourceHU on 2021/5/22.
 */
@Service
public class UserService {

  public static final String COOKIE_NAME_TOKEN = "token";
  @Autowired
  UserMapper userMapper;
  @Autowired
  RedisService redisService;

  public User getById(long id) {
    //对象缓存
    User user = redisService.get(UserKey.getById, "" + id, User.class);
    if (user != null) {
      return user;
    }
    //取数据库
    user = userMapper.getById(id);
    //再存入缓存
    if (user != null) {
      redisService.set(UserKey.getById, "" + id, user);
    }
    return user;
  }

  /**
   * 典型缓存同步场景：更新密码
   */
  public boolean updatePassword(String token, long id, String formPass) {
    //取user
    User user = getById(id);
    if (user == null) {
      throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
    }
    //更新数据库
    User toBeUpdate = new User();
    toBeUpdate.setId(id);
    toBeUpdate.setPassword(MD5Util.formPassToDBPass(formPass, user.getSalt()));
    userMapper.update(toBeUpdate);
    //更新缓存：先删除再插入
    redisService.delete(UserKey.getById, "" + id);
    user.setPassword(toBeUpdate.getPassword());
    redisService.set(UserKey.token, token, user);
    return true;
  }

  public String login(HttpServletResponse response, LoginVo loginVo) {
    if (loginVo == null) {
      throw new GlobalException(CodeMsg.SERVER_ERROR);
    }
    String mobile = loginVo.getMobile();
    String formPass = loginVo.getPassword();
    //判断手机号是否存在
    User user = getById(Long.parseLong(mobile));
    if (user == null) {
      throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
    }
    //验证密码
    String dbPass = user.getPassword();
    String saltDB = user.getSalt();
    String calcPass = MD5Util.formPassToDBPass(formPass, saltDB);
    if (!calcPass.equals(dbPass)) {
      throw new GlobalException(CodeMsg.PASSWORD_ERROR);
    }
    //生成唯一id作为token
    String token = UUIDUtil.uuid();
    addCookie(response, token, user);
    return token;
  }

  /**
   * 将token做为key，用户信息做为value 存入redis模拟session
   * 同时将token存入cookie，保存登录状态
   */
  public void addCookie(HttpServletResponse response, String token, User user) {
    redisService.set(UserKey.token, token, user);
    Cookie cookie = new Cookie(COOKIE_NAME_TOKEN, token);
    cookie.setMaxAge(UserKey.token.expireSeconds());
    cookie.setPath("/");//设置为网站根目录
    response.addCookie(cookie);
  }

  /**
   * 根据token获取用户信息
   */
  public User getByToken(HttpServletResponse response, String token) {
    if (StringUtils.isEmpty(token)) {
      return null;
    }
    User user = redisService.get(UserKey.token, token, User.class);
    //延长有效期，有效期等于最后一次操作+有效期
    if (user != null) {
      addCookie(response, token, user);
    }
    return user;
  }

}
