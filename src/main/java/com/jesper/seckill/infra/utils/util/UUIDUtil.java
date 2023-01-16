package com.jesper.seckill.infra.utils.util;

import java.util.UUID;

/**
 * Created by OpensourceHU on 2021/5/22.
 * <p>
 * 唯一id生成类
 */
public class UUIDUtil {

    public static String uuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }

}
