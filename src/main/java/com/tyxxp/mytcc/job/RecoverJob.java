package com.tyxxp.mytcc.job;

import com.tyxxp.mytcc.biz.TccBiz;
import com.tyxxp.mytcc.common.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Tcc恢复Job
 *
 * @author tangyu
 */
@Component
public class RecoverJob extends BaseJob {

    /**
     * job任务执行线程池
     */
    private ScheduledExecutorService executor;

    /**
     * tcc服务
     */
    protected TccBiz tccBiz;

    /**
     * job执行
     */
    @Override
    protected void execute() throws Exception {
        tccBiz.recoverTcc();
    }

    /**
     * 定时任务执行线程池
     */
    @Override
    protected ScheduledExecutorService getExecutor() {
        if (executor == null) {
            synchronized (this) {
                if (executor == null) {
                    executor = Executors.newScheduledThreadPool(3);
                }
            }
        }
        return executor;
    }

    /**
     * 执行周期
     */
    @Override
    protected long getPeriodSeconds() {
        return tccConfig.getRecoverJobExecutePeriodSeconds();
    }

    /**
     * 注入tccBiz
     */
    @Autowired
    public void setTccBiz(TccBiz tccBiz) {
        this.tccBiz = tccBiz;
    }

    /**
     * 允许用户代码配置任务执行线程池
     */
    @Autowired(required = false)
    @Qualifier(Constant.EXECUTOR_BEAN_ID)
    public void setExecutor(ScheduledExecutorService executor) {
        this.executor = executor;
    }
}
