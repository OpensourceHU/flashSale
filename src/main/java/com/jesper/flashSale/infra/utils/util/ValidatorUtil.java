package com.jesper.flashSale.infra.utils.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by OpensourceHU on 2021/5/22.
 * <p>
 * 登录校验工具类
 */
public class ValidatorUtil {

  //默认以1开头后面加10个数字为手机号
  private static final Pattern mobile_pattern = Pattern.compile("1\\d{10}");

  public static boolean isMobile(String src) {
    if (StringUtils.isEmpty(src)) {
      return false;
    }
    Matcher m = mobile_pattern.matcher(src);
    return m.matches();
  }
}
