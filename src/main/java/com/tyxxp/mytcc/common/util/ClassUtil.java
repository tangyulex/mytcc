package com.tyxxp.mytcc.common.util;

import java.lang.reflect.Method;
import java.util.Objects;

/**
 * Class工具类
 *
 * @author tangyu
 */
public class ClassUtil {

    /**
     * 寻找与targetMethod方法签名一致的接口
     */
    public static Class findEqualsMethodInterface(Class[] interfaces, Method targetMethod) {
        Class<?>[] parameterTypes = targetMethod.getParameterTypes();
        for (Class anInterface : interfaces) {
            Method[] methods = anInterface.getMethods();
            for (Method method : methods) {
                if (Objects.equals(method.getName(), targetMethod.getName())
                        && Objects.equals(method.getReturnType(), targetMethod.getReturnType())) {
                    Class<?>[] tmpPts = method.getParameterTypes();
                    if (tmpPts != null && tmpPts.length > 0) {
                        boolean isEquals = true;
                        for (int i = 0; i < parameterTypes.length; i++) {
                            if (!Objects.equals(parameterTypes[i], tmpPts[i])) {
                                isEquals = false;
                                break;
                            }
                        }
                        if (isEquals) {
                            return anInterface;
                        }
                    }
                }
            }
        }
        return null;
    }

}
