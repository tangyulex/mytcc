package com.tyxxp.mytcc.aspect;

import com.tyxxp.mytcc.biz.TccBiz;
import com.tyxxp.mytcc.common.enumeration.MethodTypeEnum;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * cancel切面
 *
 * @author tangyu
 */
@Component
@Aspect
public class CancelAspect {

    /**
     * tcc核心逻辑
     */
    private TccBiz tccBiz;

    /**
     * 切点
     */
    @Pointcut("@annotation(com.tyxxp.mytcc.annotation.Cancel)")
    public void pointcut() {
    }

    /**
     * 环绕通知
     */
    @Around("pointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        return tccBiz.doTccAspect(joinPoint, MethodTypeEnum.CANCEL);
    }

    /**
     * 注入tccBiz
     */
    @Autowired
    public void setTccBiz(TccBiz tccBiz) {
        this.tccBiz = tccBiz;
    }
}
