package com.tyxxp.mytcc.common.exception;

/**
 * exception基类
 *
 * @author tangyu
 */
public class MyTccException extends RuntimeException {

    public MyTccException() {
        super();
    }

    public MyTccException(String message) {
        super(message);
    }

    public MyTccException(String message, Throwable cause) {
        super(message, cause);
    }

    public MyTccException(Throwable cause) {
        super(cause);
    }

    protected MyTccException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
