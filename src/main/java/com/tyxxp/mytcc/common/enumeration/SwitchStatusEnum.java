package com.tyxxp.mytcc.common.enumeration;

/**
 * job开关状态
 *
 * @author tangyu
 */
public enum SwitchStatusEnum {

    /**
     * 开启
     */
    OPEN(1),

    /**
     * 关闭
     */
    CLOSE(0);

    /**
     * 对应数据库中jobStatus代码
     */
    private int code;

    SwitchStatusEnum(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    /**
     * 根据code获取枚举
     */
    public static SwitchStatusEnum getByCode(int code) {
        for (SwitchStatusEnum item : values()) {
            if (item.getCode() == code) {
                return item;
            }
        }
        return null;
    }
}
