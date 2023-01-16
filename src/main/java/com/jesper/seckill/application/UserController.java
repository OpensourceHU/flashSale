package com.jesper.seckill.application;

import com.jesper.seckill.domain.UserService;
import com.jesper.seckill.infra.db.pojos.User;
import com.jesper.seckill.infra.db.enums.result_code.Result;
import com.jesper.seckill.infra.redis.RedisService;
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