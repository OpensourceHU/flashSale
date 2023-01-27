package com.jesper.flashSale.infra.utils.validator;

import com.jesper.flashSale.infra.utils.util.ValidatorUtil;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by OpensourceHU on 2021/5/22.
 * <p>
 * 自定义手机格式校验器
 */
public class IsMobileValidator implements ConstraintValidator<IsMobile, String> {

  private boolean required = false;

  //初始化
  @Override
  public void initialize(IsMobile isMobile) {
    required = isMobile.required();
  }

  @Override
  public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
    if (required) {
      return ValidatorUtil.isMobile(value);
    } else {
      if (StringUtils.isEmpty(value)) {
        return true;
      } else {
        return ValidatorUtil.isMobile(value);
      }
    }
  }
}
