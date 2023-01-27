package com.jesper.flashSale.application;

import com.jesper.flashSale.domain.UserService;
import com.jesper.flashSale.domain.vo.LoginVo;
import com.jesper.flashSale.infra.db.enums.result_code.Result;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 * Created by OpensourceHU on 2021/5/21.
 */
@Controller
@RequestMapping("/login")
public class LoginController {

  private static final Logger log = LoggerFactory.getLogger(LoginController.class);

  @Autowired
  UserService userService;


  @RequestMapping("/to_login")
  public String toLogin() {
    return "login";
  }

  @RequestMapping("/do_login")
  @ResponseBody
  public Result<String> doLogin(HttpServletResponse response,
                                @Valid LoginVo loginVo) {//加入JSR303参数校验
    log.info(loginVo.toString());
    String token = userService.login(response, loginVo);
    return Result.success(token);
  }

}
