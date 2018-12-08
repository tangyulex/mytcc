package com.tyxxp.mytcc.common.enumeration;

/**
 * Job执行状态
 *
 * @author tangyu
 */
public enum JobStatusEnum {

    /**
     * 初始化
     */
    INITIAL(0),

    /**
     * 正在执行
     */
    DOING(1),

    /**
     * 执行成功
     */
    SUCCESS(2),

    /**
     * 执行失败
     */
    FAIL(3);

    /**
     * 对应数据库中jobStatus代码
     */
    private int code;

    JobStatusEnum(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    /**
     * 根据code获取枚举
     */
    public static JobStatusEnum getByCode(int code) {
        for (JobStatusEnum item : values()) {
            if (item.getCode() == code) {
                return item;
            }
        }
        return null;
    }
}
