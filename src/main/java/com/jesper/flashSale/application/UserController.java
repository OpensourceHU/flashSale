package com.jesper.flashSale.application;

import com.jesper.flashSale.domain.UserService;
import com.jesper.flashSale.infra.db.enums.result_code.Result;
import com.jesper.flashSale.infra.db.pojos.User;
import com.jesper.flashSale.infra.redis.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by OpensourceHU on 2021/5/23.
 */
@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    RedisService redisService;

    @RequestMapping("/info")
    @ResponseBody
    public Result<User> info(Model model, User user) {
        return Result.success(user);
    }
}