package com.tyxxp.mytcc.bean;

import com.tyxxp.mytcc.annotation.AutoIncrementId;
import com.tyxxp.mytcc.annotation.Serial;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;

/**
 * 执行记录
 *
 * @author tangyu
 */
public class Execution implements Serializable {

    /**
     * 执行ID
     */
    @AutoIncrementId
    private Long executionId;

    /**
     * TCC全局ID
     */
    private String tccId;

    /**
     * app名
     */
    private String appName;

    /**
     * tcc调用链路上父app名
     */
    private String parentAppName;

    /**
     * 接口名
     */
    private String interfaceName;

    /**
     * try方法名
     */
    private String tryAddress;

    /**
     * cancel方法名
     */
    private String cancelAddress;

    /**
     * confirm方法名
     */
    private String confirmAddress;

    /**
     * 方法参数
     */
    @Serial
    private Object[] args;

    /**
     * 状态（0：try，1：tried，2：cancel，3：canceled，4：confirm，5：confirmed）
     */
    private Integer status;

    /**
     * 远程调用方式
     */
    private String remoteType;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 版本号
     */
    private Long version;

    /**
     * 是否删除（0：未删除，1：已删除）
     */
    private Integer isDeleted;

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getParentAppName() {
        return parentAppName;
    }

    public void setParentAppName(String parentAppName) {
        this.parentAppName = parentAppName;
    }

    public boolean isDeleted() {
        return Objects.equals(isDeleted, 1);
    }

    public void isDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted ? 1 : 0;
    }

    public Long getExecutionId() {
        return executionId;
    }

    public void setExecutionId(Long executionId) {
        this.executionId = executionId;
    }

    public String getTccId() {
        return tccId;
    }

    public void setTccId(String tccId) {
        this.tccId = tccId;
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public Integer getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Integer isDeleted) {
        this.isDeleted = isDeleted;
    }

    public String getTryAddress() {
        return tryAddress;
    }

    public void setTryAddress(String tryAddress) {
        this.tryAddress = tryAddress;
    }

    public String getCancelAddress() {
        return cancelAddress;
    }

    public void setCancelAddress(String cancelAddress) {
        this.cancelAddress = cancelAddress;
    }

    public String getConfirmAddress() {
        return confirmAddress;
    }

    public void setConfirmAddress(String confirmAddress) {
        this.confirmAddress = confirmAddress;
    }

    public String getRemoteType() {
        return remoteType;
    }

    public void setRemoteType(String remoteType) {
        this.remoteType = remoteType;
    }

    @Override
    public String toString() {
        return "Execution{" +
                "executionId=" + executionId +
                ", tccId='" + tccId + '\'' +
                ", appName='" + appName + '\'' +
                ", parentAppName='" + parentAppName + '\'' +
                ", interfaceName='" + interfaceName + '\'' +
                ", tryAddress='" + tryAddress + '\'' +
                ", cancelAddress='" + cancelAddress + '\'' +
                ", confirmAddress='" + confirmAddress + '\'' +
                ", args=" + Arrays.toString(args) +
                ", status=" + status +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", version=" + version +
                ", isDeleted=" + isDeleted +
                ", remoteType='" + remoteType + '\'' +
                '}';
    }
}
