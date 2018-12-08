package com.tyxxp.mytcc.remote;

import com.tyxxp.mytcc.bean.Execution;

/**
 * 远程调用接口
 *
 * @author tangyu
 */
public interface IRemote {

    /**
     * 提交try阶段资源
     */
    void confirm(Execution execution);

    /**
     * 回滚try阶段资源
     */
    void cancel(Execution execution);
}
