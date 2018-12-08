package com.tyxxp.mytcc.common.enumeration;

/**
 * 方法调用类型
 *
 * @author tangyu
 */
public enum MethodTypeEnum {

    /**
     * try方法，预留资源
     */
    TRY,

    /**
     * confirm方法，提交预留资源
     */
    CONFIRM,

    /**
     * cancel方法，回滚预留资源
     */
    CANCEL;
}
