package com.tyxxp.mytcc.dao;

import com.tyxxp.mytcc.bean.JobExecution;
import com.tyxxp.mytcc.common.enumeration.JobStatusEnum;
import com.tyxxp.mytcc.common.util.CollectionUtil;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * my_tcc_job_execution表访问接口
 *
 * @author tangyu
 */
@Repository
public class JobExecutionMapper extends BaseMapper {

    /**
     * my_tcc_job_execution所有字段
     */
    private static final String ALL_COLUMNS = "job_execution_id,task_name,app_name,ip,status," +
            "switch_status,create_time,update_time,version,message";

    /**
     * 根据任务名和应用名获取job记录
     */
    public JobExecution select(String taskName, String appName) {
        String sql = "select " + ALL_COLUMNS + " from my_tcc_job_execution where task_name= ? and app_name = ?";
        List<JobExecution> list = select(JobExecution.class, sql, taskName, appName);
        if (CollectionUtil.isNotEmpty(list)) {
            return list.get(0);
        }
        return null;
    }

    /**
     * 插入JobExecution
     */
    public int insertIgnoreSelective(JobExecution jobExecution) {
        return insert(true, true, jobExecution, "my_tcc_job_execution");
    }

    /**
     * 更新JobExecution
     */
    public int updateJobStatus(JobStatusEnum toStatus, String ip, String taskName, String appName, Long version) {
        String sql = "update my_tcc_job_execution set status = ?, version = ?, ip = ? " +
                "where task_name = ? and app_name = ? and version = ?";
        return executeUpdate(sql, toStatus.getCode(), version + 1, ip, taskName, appName, version);
    }

    /**
     * 更新JobExecution
     */
    public int updateJobStatus(JobStatusEnum toStatus, String ip, Long jobExecutionId, Long version) {
        String sql = "update my_tcc_job_execution set status = ?, version = ?, ip = ? " +
                "where job_execution_id = ? and version = ?";
        return executeUpdate(sql, toStatus.getCode(), version + 1, ip, jobExecutionId, version);
    }
}
