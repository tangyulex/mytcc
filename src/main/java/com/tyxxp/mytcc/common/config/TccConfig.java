package com.tyxxp.mytcc.common.config;

import com.tyxxp.mytcc.common.enumeration.CallTypeEnum;
import com.tyxxp.mytcc.common.exception.MyTccException;

/**
 * 配置类
 *
 * @author tangyu
 */
public class TccConfig {

    /**
     * app名
     */
    private String appName;

    /**
     * 0：由tcc发起节点cancel或confirm所有节点
     * 1：由父节点cancel或confirm所有节点
     *
     * @see CallTypeEnum
     */
    private int callType = CallTypeEnum.BY_PARENT.getCode();

    /**
     * tcc任务恢复job每隔多少秒执行一次
     */
    private long recoverJobExecutePeriodSeconds = 30;


    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public int getCallType() {
        return callType;
    }

    public void setCallType(int callType) {
        this.callType = callType;
    }

    public CallTypeEnum getCallTypeEnum() {
        CallTypeEnum callTypeEnum = CallTypeEnum.getByCode(callType);
        if (callTypeEnum != null) {
            return callTypeEnum;
        }
        throw new MyTccException("不支持的CallType");
    }

    public long getRecoverJobExecutePeriodSeconds() {
        return recoverJobExecutePeriodSeconds;
    }

    public void setRecoverJobExecutePeriodSeconds(long recoverJobExecutePeriodSeconds) {
        this.recoverJobExecutePeriodSeconds = recoverJobExecutePeriodSeconds;
    }

    @Override
    public String toString() {
        return "TccConfig{" +
                "appName='" + appName + '\'' +
                ", callType=" + callType +
                ", recoverJobExecutePeriodSeconds=" + recoverJobExecutePeriodSeconds +
                '}';
    }
}
