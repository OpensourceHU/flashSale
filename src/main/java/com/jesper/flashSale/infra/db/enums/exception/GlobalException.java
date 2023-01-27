package com.jesper.flashSale.infra.db.enums.exception;

import com.jesper.flashSale.infra.db.enums.result_code.CodeMsg;

/**
 * Created by OpensourceHU on 2021/5/22.
 * <p>
 * 自定义全局异常类
 */
public class GlobalException extends RuntimeException {

  private static final long servialVersionUID = 1L;

  private final CodeMsg codeMsg;

  public GlobalException(CodeMsg codeMsg) {
    super(codeMsg.toString());
    this.codeMsg = codeMsg;
  }

  public CodeMsg getCodeMsg() {
    return codeMsg;
  }
}
