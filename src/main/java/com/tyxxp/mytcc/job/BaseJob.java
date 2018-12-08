package com.tyxxp.mytcc.job;

import com.tyxxp.mytcc.bean.JobExecution;
import com.tyxxp.mytcc.common.config.TccConfig;
import com.tyxxp.mytcc.common.enumeration.JobStatusEnum;
import com.tyxxp.mytcc.common.enumeration.SwitchStatusEnum;
import com.tyxxp.mytcc.common.util.NetUtil;
import com.tyxxp.mytcc.dao.JobExecutionMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.tyxxp.mytcc.common.enumeration.JobStatusEnum.*;
import static com.tyxxp.mytcc.common.enumeration.SwitchStatusEnum.OPEN;

/**
 * Job基类
 *
 * @author tangyu
 */
public abstract class BaseJob implements InitializingBean, BeanNameAware {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 当前bean名
     */
    private String beanName;

    /**
     * job执行记录DAO
     */
    private JobExecutionMapper jobExecutionMapper;

    /**
     * tcc配置
     */
    protected TccConfig tccConfig;

    /**
     * SpringBean属性设置完毕后由Spring调用
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        doExecute();
    }

    /**
     * 任务执行
     */
    protected void doExecute() {
        getExecutor().scheduleAtFixedRate(this::runTask, getPeriodSeconds(), getPeriodSeconds(), TimeUnit.SECONDS);
    }

    /**
     * 任务执行
     */
    private void runTask() {
        JobExecution jobExecution = null;
        try {
            if ((jobExecution = lockJobExecution()) != null) {
                execute();
                releaseJobExecution(jobExecution, SUCCESS);
            } else {
                logger.info("appName[{}], taskName[{}], 正在执行", tccConfig.getAppName(), beanName);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            Assert.notNull(jobExecution);
            // TODO：记录异常
            releaseJobExecution(jobExecution, FAIL);
        }
    }

    /**
     * 任务执行核心逻辑，由子类重写
     */
    protected abstract void execute() throws Exception;

    /**
     * 获取定时任务执行线程池，由子类重写
     */
    protected abstract ScheduledExecutorService getExecutor();

    /**
     * 获取任务执行周期，由子类重写
     */
    protected abstract long getPeriodSeconds();

    /**
     * 获取BeanName作为TaskName
     */
    @Override
    public void setBeanName(String name) {
        this.beanName = name;
    }

    /**
     * 获取DAO
     */
    @Autowired
    public void setJobExecutionMapper(JobExecutionMapper jobExecutionMapper) {
        this.jobExecutionMapper = jobExecutionMapper;
    }

    /**
     * 获取全局配置
     */
    @Autowired
    public void setTccConfig(TccConfig tccConfig) {
        this.tccConfig = tccConfig;
    }

    /**
     * 锁定并返回JobExecution，如果返回null则表示锁定失败
     */
    private JobExecution lockJobExecution() {
        String appName = tccConfig.getAppName();
        JobExecution exe = jobExecutionMapper.select(beanName, appName);
        // 初始化JobExecution，并将状态设置为正在执行
        if (exe == null) {
            logger.info("初始化jobExecution：appName[{}]，taskName[{}]", tccConfig.getAppName(), beanName);
            exe = new JobExecution();
            exe.setAppName(appName);
            exe.setTaskName(beanName);
            exe.setStatus(DOING.getCode());
            exe.setSwitchStatus(OPEN.getCode());
            exe.setIp(NetUtil.getLocalHost());
            int count = jobExecutionMapper.insertIgnoreSelective(exe);
            if (count != 0) {
                exe = this.jobExecutionMapper.select(this.beanName, appName);
                return exe;
            }
        } else {
            // 设置状态为正在执行
            if (getByCode(exe.getStatus()) != DOING) {
                if (SwitchStatusEnum.getByCode(exe.getSwitchStatus()) == SwitchStatusEnum.OPEN) {
                    int count = jobExecutionMapper.updateJobStatus(
                            DOING, NetUtil.getLocalHost(),
                            exe.getJobExecutionId(),
                            exe.getVersion());
                    if (count != 0) {
                        exe.setVersion(exe.getVersion() + 1);
                        return exe;
                    }
                } else {
                    logger.info("无法锁定JobExecution，因为该job处于关闭状态：appName[{}]，taskName[{}]", appName, beanName);
                }
            }
        }
        return null;
    }

    /**
     * 更新状态
     */
    private void releaseJobExecution(JobExecution jobExecution, JobStatusEnum jobStatusEnum) {
        jobExecutionMapper.updateJobStatus(jobStatusEnum,
                NetUtil.getLocalHost(),
                jobExecution.getJobExecutionId(),
                jobExecution.getVersion());
    }
}
