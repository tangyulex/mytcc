package com.tyxxp.mytcc.dao;

import com.tyxxp.mytcc.bean.Execution;
import com.tyxxp.mytcc.common.enumeration.StatusEnum;
import com.tyxxp.mytcc.common.util.CollectionUtil;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * my_tcc_execution表访问接口
 *
 * @author tangyu
 */
@Repository
public class ExecutionMapper extends BaseMapper {

    /**
     * my_tcc_execution所有字段
     */
    private static final String ALL_COLUMNS= "execution_id,tcc_id,app_name,parent_app_name," +
            "interface_name,try_address,cancel_address,confirm_address,args," +
            "status,remote_type,create_time,update_time,version,is_deleted";

    /**
     * 获取需要恢复执行的tccId
     */
    public List<String> selectNeedRecoverTccIds(String appName) {
        String sql = "select distinct tcc_id from my_tcc_execution where tcc_id in (" +
                "       select distinct tcc_id from my_tcc_execution" +
                "       where ((parent_app_name is null or parent_app_name='') and app_name=?)" +
                "     ) and is_deleted=0 and update_time < DATE_SUB(now(), INTERVAL 30 SECOND) and update_time > DATE_SUB(now(), INTERVAL 1 HOUR)";
        return select(String.class, sql, appName);
    }

    /**
     * 筛选出tcc调用链路上包含某些状态的tccId
     */
    public List<String> screenOutIncludeAnyStatusTccIds(List<String> tccIds, StatusEnum... statusEnums) {
        if (CollectionUtil.isNotEmpty(tccIds) && statusEnums != null && statusEnums.length > 0) {

            StringBuilder tccIdSb = new StringBuilder();
            for (String tccId : tccIds) {
                tccIdSb.append(",'").append(tccId).append("'");
            }

            StringBuilder statusSb = new StringBuilder();
            for (StatusEnum statusEnum : statusEnums) {
                statusSb.append(",").append(statusEnum.getCode());
            }

            String sql = "select distinct tcc_id from my_tcc_execution" +
                    "     where tcc_id in (" + tccIdSb.toString().replaceFirst(",", "") + ")" +
                    "     group by tcc_id, status" +
                    "     having status in (" + statusSb.toString().replaceFirst(",", "") + ")";
            return select(String.class, sql);

        }
        return new ArrayList<>(0);
    }

    /**
     * 获取直接子节点
     */
    public List<Execution> selectDirectSubNodeExecution(String tccId, String parentAppName) {
        String sql = "select * from my_tcc_execution where tcc_id = ? and is_deleted = 0 and parent_app_name = ?";
        return select(Execution.class, sql, tccId, parentAppName);
    }

    /**
     * 通过tccId获取发起节点的Execution
     */
    public Execution selectSponsorExecutionByTccId(String tccId) {
        String sql = "select " + ALL_COLUMNS + " from my_tcc_execution where tcc_id = ? and is_deleted = 0 and (parent_app_name is null or parent_app_name = '')";
        List<Execution> list = select(Execution.class, sql, tccId);
        if (CollectionUtil.isNotEmpty(list)) {
            return list.get(0);
        }
        return null;
    }

    /**
     * 通过tccId获取所有execution️
     */
    public List<Execution> selectAllExecutionsByTccId(String tccId) {
        String sql = "select " + ALL_COLUMNS + " from my_tcc_execution where tcc_id = ? and is_deleted = 0";
        return select(Execution.class, sql, tccId);
    }

    /**
     * 插入Execution
     */
    public int insertExecution(Execution execution) {
        return insert(true, false, execution, "my_tcc_execution");
    }

    /**
     * 根据主键更新Execution状态
     */
    public void updateExecutionStatusById(Long executionId, StatusEnum statusEnum, boolean doLogicDelete) {
        String sql = "update my_tcc_execution set status = ? " + (doLogicDelete ? ", is_deleted = 1" : "")
                + "   where execution_id = ?";
        execute(sql, statusEnum.getCode(), executionId);
    }
}
