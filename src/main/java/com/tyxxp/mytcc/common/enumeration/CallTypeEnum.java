package com.tyxxp.mytcc.common.enumeration;

/**
 * 调用类型
 *
 * @author tangyu
 */
public enum CallTypeEnum {

    /**
     * 由发起节点调用所有节点
     */
    BY_SPONSOR(0),

    /**
     * 由父节点调用所有子节点
     */
    BY_PARENT(1);

    /**
     * 对应数据库中的CallType代码
     */
    private int code;

    CallTypeEnum(int code) {
        this.code = code;
    }

    public static boolean isBySponsor(int code) {
        return getByCode(code) == BY_SPONSOR;
    }

    public static boolean isByParent(int code) {
        return getByCode(code) == BY_PARENT;
    }

    public int getCode() {
        return code;
    }

    public static CallTypeEnum getByCode(int code) {
        for (CallTypeEnum item : values()) {
            if (item.getCode() == code) {
                return item;
            }
        }
        return null;
    }
}
