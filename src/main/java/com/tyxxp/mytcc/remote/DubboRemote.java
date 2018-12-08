package com.tyxxp.mytcc.remote;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.spring.ReferenceBean;
import com.alibaba.dubbo.rpc.service.GenericService;
import com.tyxxp.mytcc.bean.Execution;
import com.tyxxp.mytcc.common.util.SpringContextDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * dubbo远程调用对象
 *
 * @author tangyu
 */
public class DubboRemote implements IRemote {

    private static final Logger logger = LoggerFactory.getLogger(DubboRemote.class);

    /**
     * 缓存ReferenceBean
     */
    private static final ConcurrentHashMap<String, ReferenceBean<GenericService>> referenceCache = new ConcurrentHashMap<>();

    /**
     * 提交预留资源
     */
    @Override
    public void confirm(Execution execution) {
        invoke(execution.getInterfaceName(), execution.getConfirmAddress(), execution.getArgs());
    }

    /**
     * 回滚预留资源
     */
    @Override
    public void cancel(Execution execution) {
        invoke(execution.getInterfaceName(), execution.getCancelAddress(), execution.getArgs());
    }

    /**
     * 根据接口名获取GenericService
     */
    public GenericService getService(String interfaceName) throws Exception {
        // TODO：interfaceName/group/version 作为ID
        ReferenceBean<GenericService> referenceBean = referenceCache.get(interfaceName);
        if (referenceBean == null) {
            synchronized (referenceCache) {
                referenceBean = referenceCache.get(interfaceName);
                if (referenceBean == null) {
                    // TODO：Reference参数配置化，包括ApplicationConfig等配置
                    referenceBean = new ReferenceBean<>();
                    referenceBean.setInterface(interfaceName);
                    //referenceConfig.setVersion("1.0.0");
                    referenceBean.setGeneric(true);
                    referenceBean.setApplicationContext(SpringContextDelegate.getApplicationContext());
                    referenceBean.afterPropertiesSet();
                    if (referenceBean.getApplication() == null) {
                        ApplicationConfig applicationConfig = new ApplicationConfig();
                        applicationConfig.setName(UUID.randomUUID().toString().replace("-", ""));
                        referenceBean.setApplication(applicationConfig);
                    }
                    referenceCache.put(interfaceName, referenceBean);
                }
            }
        }
        return referenceBean.get();
    }

    /**
     * 调用指定接口
     */
    private void invoke(String interfaceName, String methodName, Object[] args) {
        GenericService genericService;
        try {
            genericService = getService(interfaceName);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (genericService != null) {
            if (args != null) {
                String[] pt = new String[args.length];
                for (int i = 0; i < args.length; i++) {
                    pt[i] = args[i].getClass().getName();
                }
                genericService.$invoke(methodName, pt, args);
            } else {
                genericService.$invoke(methodName, new String[]{}, new Object[]{});
            }
        } else {
            logger.warn("interfaceName={}, get generic service = null", methodName);
            throw new RuntimeException(String.format("无法获取服务：%s", interfaceName));
        }
    }
}
