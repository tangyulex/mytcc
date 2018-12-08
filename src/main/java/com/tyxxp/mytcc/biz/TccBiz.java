package com.tyxxp.mytcc.biz;

import com.tyxxp.mytcc.annotation.Try;
import com.tyxxp.mytcc.bean.Execution;
import com.tyxxp.mytcc.bean.TccContext;
import com.tyxxp.mytcc.common.config.TccConfig;
import com.tyxxp.mytcc.common.enumeration.CallTypeEnum;
import com.tyxxp.mytcc.common.enumeration.MethodTypeEnum;
import com.tyxxp.mytcc.common.util.ClassUtil;
import com.tyxxp.mytcc.common.util.CollectionUtil;
import com.tyxxp.mytcc.common.util.StringUtil;
import com.tyxxp.mytcc.dao.ExecutionMapper;
import com.tyxxp.mytcc.factory.RemoteFactory;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.tyxxp.mytcc.common.enumeration.StatusEnum.*;

/**
 * 核心业务类
 *
 * @author tangyu
 */
@Service
public class TccBiz {
    private static final Logger logger = LoggerFactory.getLogger(TccBiz.class);

    /**
     * Execution数据访问接口
     */
    private ExecutionMapper executionMapper;

    /**
     * 框架全局配置
     */
    private TccConfig tccConfig;

    /**
     * 注入ExecutionMapper
     */
    @Autowired
    public void setExecutionMapper(ExecutionMapper executionMapper) {
        this.executionMapper = executionMapper;
    }

    /**
     * 注入tccConfig
     */
    @Autowired
    public void setTccConfig(TccConfig tccConfig) {
        this.tccConfig = tccConfig;
    }

    /**
     * tcc切面执行
     */
    public Object doTccAspect(ProceedingJoinPoint joinPoint, MethodTypeEnum methodTypeEnum) throws Throwable {
        checkParam(joinPoint);
        CallTypeEnum callTypeEnum = tccConfig.getCallTypeEnum();
        Object proceedResult = null;
        if (methodTypeEnum == MethodTypeEnum.CANCEL || methodTypeEnum == MethodTypeEnum.CONFIRM) {
            try {
                proceedResult = joinPoint.proceed();
            } finally {
                if (callTypeEnum == CallTypeEnum.BY_PARENT) {
                    Object[] args = joinPoint.getArgs();
                    TccContext tccContext = (TccContext) args[0];
                    invokeAllDirectSubNodeExecutions(tccContext.getTccId(), methodTypeEnum);
                }
            }
        } else if (methodTypeEnum == MethodTypeEnum.TRY) {
            Execution execution = createExecution(joinPoint);
            boolean isSponsor = StringUtil.isBlank(execution.getParentAppName());
            try {
                executionMapper.insertExecution(execution);
                proceedResult = joinPoint.proceed(execution.getArgs());
                executionMapper.updateExecutionStatusById(execution.getExecutionId(), TRIED, false);
            } catch (Throwable e) {
                if (isSponsor) {
                    if (callTypeEnum == CallTypeEnum.BY_SPONSOR) {
                        invokeAllNodeExecutions(execution.getTccId(), MethodTypeEnum.CANCEL);
                    } else if (callTypeEnum == CallTypeEnum.BY_PARENT) {
                        invokeSponsorNodeExecution(execution.getTccId(), MethodTypeEnum.CANCEL);
                    }
                }
                throw e;
            }
            if (isSponsor) {
                if (callTypeEnum == CallTypeEnum.BY_SPONSOR) {
                    invokeAllNodeExecutions(execution.getTccId(), MethodTypeEnum.CONFIRM);
                } else if (callTypeEnum == CallTypeEnum.BY_PARENT) {
                    invokeSponsorNodeExecution(execution.getTccId(), MethodTypeEnum.CONFIRM);
                }
            }
        }

        return proceedResult;
    }

    /**
     * 恢复tcc任务执行
     */
    public void recoverTcc() {
        // 获取本应用需要恢复的tcc任务
        List<String> tccIds = executionMapper.selectNeedRecoverTccIds(tccConfig.getAppName());
        if (CollectionUtil.isNotEmpty(tccIds)) {
            logger.info("本次需要恢复的tcc任务一共有{}个", tccIds.size());
            CallTypeEnum callTypeEnum = tccConfig.getCallTypeEnum();
            // 如果该tcc调用链路中有一个执行过CANCEL，那么全部都要执行CANCEL
            List<String> toHandleList = executionMapper.screenOutIncludeAnyStatusTccIds(tccIds, CANCEL, CANCELED);
            if (callTypeEnum == CallTypeEnum.BY_PARENT) {
                invokeSponsorNodeExecution(toHandleList, MethodTypeEnum.CANCEL);
            } else if (callTypeEnum == CallTypeEnum.BY_SPONSOR) {
                invokeAllNodeExecutions(toHandleList, MethodTypeEnum.CANCEL);
            }
            // 如果该tcc调用链路中有一个执行过CONFIRM，那么全部都要执行CONFIRM
            CollectionUtil.substract(tccIds, toHandleList);
            toHandleList = executionMapper.screenOutIncludeAnyStatusTccIds(tccIds, CONFIRM, CONFIRMED);
            if (callTypeEnum == CallTypeEnum.BY_PARENT) {
                invokeSponsorNodeExecution(toHandleList, MethodTypeEnum.CONFIRM);
            } else {
                invokeAllNodeExecutions(toHandleList, MethodTypeEnum.CONFIRM);
            }

            // 剩余的都是在try阶段的，执行CANCEL
            CollectionUtil.substract(tccIds, toHandleList);
            invokeSponsorNodeExecution(tccIds, MethodTypeEnum.CANCEL);
        } else {
            logger.info("没有需要恢复的tcc");
        }
    }

    /**
     * 调用发起节点
     */
    public void invokeSponsorNodeExecution(List<String> tccIds, MethodTypeEnum methodTypeEnum) {
        if (CollectionUtil.isNotEmpty(tccIds)) {
            for (String tccId : tccIds) {
                invokeSponsorNodeExecution(tccId, methodTypeEnum);
            }
        }
    }

    /**
     * 调用发起节点
     */
    public void invokeSponsorNodeExecution(String tccId, MethodTypeEnum methodTypeEnum) {
        Execution exe = executionMapper.selectSponsorExecutionByTccId(tccId);
        if (exe != null) {
            List<Execution> list = new ArrayList<>(1);
            list.add(exe);
            invokeAllExecutions(list, methodTypeEnum);
        } else {
            logger.warn("无法通过此tccId:{}获取发起节点的Execution", tccId);
        }
    }

    /**
     * 调用所有直接子节点
     */
    public void invokeAllDirectSubNodeExecutions(String tccId, MethodTypeEnum methodTypeEnum) {
        List<Execution> executionList = executionMapper.selectDirectSubNodeExecution(tccId, tccConfig.getAppName());
        invokeAllExecutions(executionList, methodTypeEnum);
    }

    /**
     * 调用指定tcc调用链路上的所有节点
     */
    public void invokeAllNodeExecutions(List<String> tccIds, MethodTypeEnum methodTypeEnum) {
        if (CollectionUtil.isNotEmpty(tccIds)) {
            for (String tccId : tccIds) {
                invokeAllNodeExecutions(tccId, methodTypeEnum);
            }
        }
    }

    /**
     * 调用指定tcc调用链路上的所有节点
     */
    public void invokeAllNodeExecutions(String tccId, MethodTypeEnum methodTypeEnum) {
        List<Execution> executionList = executionMapper.selectAllExecutionsByTccId(tccId);
        invokeAllExecutions(executionList, methodTypeEnum);
    }

    /**
     * confirm或cancel所有Execution
     */
    public void invokeAllExecutions(List<Execution> executionList, MethodTypeEnum methodTypeEnum) {
        if (methodTypeEnum == MethodTypeEnum.CONFIRM) {
            for (Execution exe : executionList) {
                try {
                    executionMapper.updateExecutionStatusById(exe.getExecutionId(), CONFIRM, true);
                    RemoteFactory.get(exe).confirm(exe);
                    executionMapper.updateExecutionStatusById(exe.getExecutionId(), CONFIRMED, true);
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            }
        } else if (methodTypeEnum == MethodTypeEnum.CANCEL) {
            for (Execution exe : executionList) {
                try {
                    executionMapper.updateExecutionStatusById(exe.getExecutionId(), CANCEL, true);
                    RemoteFactory.get(exe).cancel(exe);
                    executionMapper.updateExecutionStatusById(exe.getExecutionId(), CANCELED, true);
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
    }

    /**
     * 创建一个Execution，记录当前执行方法的执行信息。如果当前节点是发起节点则生成tccId，否则复用tccId
     */
    private Execution createExecution(JoinPoint joinPoint) {
        // 获取目标信息，校验第一个参数必须是TccId类型的
        Object target = joinPoint.getTarget();
        Class targetCls = target.getClass();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method tryMethod = signature.getMethod();

        // 创建Execution
        Try aTry = tryMethod.getAnnotation(Try.class);
        Execution execution = new Execution();
        execution.setAppName(tccConfig.getAppName());
        Class matchInterface = ClassUtil.findEqualsMethodInterface(targetCls.getInterfaces(), tryMethod);
        String interfaceName = matchInterface != null ? matchInterface.getName() : targetCls.getName();
        execution.setInterfaceName(interfaceName);
        execution.setTryAddress(tryMethod.getName());
        execution.setCancelAddress(aTry.cancelAddress());
        execution.setConfirmAddress(aTry.confirmAddress());
        execution.setRemoteType(aTry.remoteType());
        execution.setStatus(TRY.getCode());

        Object[] args = joinPoint.getArgs();
        TccContext tccContext = (TccContext) (args[0]);
        boolean isSponsor = tccContext == null || StringUtil.isBlank(tccContext.getTccId());

        // 创建或复用tccId
        if (isSponsor) {
            execution.setTccId(UUID.randomUUID().toString().replace("-", ""));
        } else {
            tccContext = (TccContext) (args[0]);
            execution.setTccId(tccContext.getTccId());
            execution.setParentAppName(tccContext.getParentAppName());
        }
        args[0] = new TccContext(execution.getTccId(), execution.getAppName());
        execution.setArgs(args);
        return execution;
    }

    /**
     * 参数校验
     */
    private void checkParam(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method tryMethod = signature.getMethod();
        Class<?>[] parameterTypes = tryMethod.getParameterTypes();
        if (parameterTypes == null
                || parameterTypes.length == 0
                || !TccContext.class.isAssignableFrom(parameterTypes[0])) {
            throw new RuntimeException("tcc方法的第一个参数必须是" + TccContext.class.getName() + "类型");
        }
    }
}
