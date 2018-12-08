package com.tyxxp.mytcc.bean;

import java.io.Serializable;

/**
 * tcc调用链路上下文
 *
 * @author tangyu
 */
public class TccContext implements Serializable {

    /**
     * 全局id
     */
    private String tccId;

    /**
     * 父app名
     */
    private String parentAppName;

    /**
     * 全属性构造器
     */
    public TccContext(String tccId, String parentAppName) {
        this.tccId = tccId;
        this.parentAppName = parentAppName;
    }

    public String getParentAppName() {
        return parentAppName;
    }

    public void setParentAppName(String parentAppName) {
        this.parentAppName = parentAppName;
    }

    public String getTccId() {
        return tccId;
    }

    public void setTccId(String tccId) {
        this.tccId = tccId;
    }

    @Override
    public String toString() {
        return "TccContext{" +
                "tccId='" + tccId + '\'' +
                ", parentAppName='" + parentAppName + '\'' +
                '}';
    }
}
