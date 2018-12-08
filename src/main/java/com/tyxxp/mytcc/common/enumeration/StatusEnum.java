package com.tyxxp.mytcc.common.enumeration;

/**
 * tcc状态
 *
 * @author tangyu
 */
public enum StatusEnum {

    /**
     * try方法执行前的状态
     */
    TRY(0),

    /**
     * 已执行try方法的状态
     */
    TRIED(1),

    /**
     * cancel方法执行前的状态
     */
    CANCEL(2),

    /**
     * 已执行cancel方法的状态
     */
    CANCELED(3),

    /**
     * confirm方法执行前的状态
     */
    CONFIRM(4),

    /**
     * 已执行cancel方法的状态
     */
    CONFIRMED(5);

    /**
     * 对应数据库中ExecutionStatus的代码
     */
    private int code;

    StatusEnum(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
