package com.tyxxp.mytcc;

import com.tyxxp.mytcc.common.util.SpringContextDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 配置类
 *
 * @author tangyu
 */
@Configuration
@ComponentScan
public class TccConfiguration {

    /**
     * 初始化Spring容器委托类
     */
    @Autowired
    public void setApplicationContext(ApplicationContext applicationContext) {
        SpringContextDelegate.init(applicationContext);
    }
}
