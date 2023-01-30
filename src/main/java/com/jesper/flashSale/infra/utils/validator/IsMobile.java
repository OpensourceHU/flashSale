package com.jesper.flashSale.infra.utils.validator;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * Created by OpensourceHU on 2021/5/22.
 * <p>
 * 自定义手机格式校验注解
 */
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE,
    ElementType.CONSTRUCTOR, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(
    validatedBy = {IsMobileValidator.class}
)//引进校验器
public @interface IsMobile {

  boolean required() default true;//默认不能为空

  String message() default "手机号码格式错误";//校验不通过输出信息

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
