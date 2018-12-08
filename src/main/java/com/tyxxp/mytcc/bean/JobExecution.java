package com.tyxxp.mytcc.bean;

import com.tyxxp.mytcc.annotation.AutoIncrementId;

import java.util.Date;

/**
 * 任务执行记录
 *
 * @author tangyu
 */
public class JobExecution {
    /**
     * 主键ID
     */
    @AutoIncrementId
    private Long jobExecutionId;

    /**
     * 任务名
     */
    private String taskName;

    /**
     * 应用名
     */
    private String appName;

    /**
     * ip地址
     */
    private String ip;

    /**
     * 状态（0：初始化，1：执行中，2：成功，3：失败）
     */
    private Integer status;

    /**
     * 开关状态（0：关闭，1：开启）
     */
    private Integer switchStatus;

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
     * 附加消息
     */
    private String message;

    public Long getJobExecutionId() {
        return jobExecutionId;
    }

    public void setJobExecutionId(Long jobExecutionId) {
        this.jobExecutionId = jobExecutionId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getSwitchStatus() {
        return switchStatus;
    }

    public void setSwitchStatus(Integer switchStatus) {
        this.switchStatus = switchStatus;
    }

    @Override
    public String toString() {
        return "JobExecution{" +
                "jobExecutionId=" + jobExecutionId +
                ", taskName='" + taskName + '\'' +
                ", appName='" + appName + '\'' +
                ", ip='" + ip + '\'' +
                ", status=" + status +
                ", switchStatus=" + switchStatus +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", version=" + version +
                ", message='" + message + '\'' +
                '}';
    }
}
