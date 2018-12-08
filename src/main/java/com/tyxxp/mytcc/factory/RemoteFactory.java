package com.tyxxp.mytcc.factory;

import com.tyxxp.mytcc.bean.Execution;
import com.tyxxp.mytcc.common.exception.MyTccException;
import com.tyxxp.mytcc.common.util.SpringContextDelegate;
import com.tyxxp.mytcc.remote.DubboRemote;
import com.tyxxp.mytcc.remote.IRemote;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * remote工厂类
 *
 * @author tangyu
 */
public class RemoteFactory {

    /**
     * 缓存IRemote
     */
    private static final ConcurrentHashMap<String, IRemote> cache = new ConcurrentHashMap<>();

    /**
     * 根据remoteType获取IRemote，如果没有配置，默认使用DubboRemote
     */
    public static IRemote get(Execution exe) {
        return get(exe.getRemoteType());
    }

    /**
     * 根据remoteType获取IRemote，如果没有配置，默认使用DubboRemote
     */
    public static IRemote get(String remoteType) {
        IRemote iRemote = cache.get(remoteType);
        if (iRemote == null) {
            synchronized (cache) {
                iRemote = cache.get(remoteType);
                if (iRemote == null) {
                    boolean containsBean = SpringContextDelegate.containsBean(remoteType + "Remote");
                    if (containsBean) {
                        iRemote = SpringContextDelegate.getBean(remoteType + "Remote", IRemote.class);
                    } else {
                        if (Objects.equals("dubbo", remoteType)) {
                            iRemote = new DubboRemote();
                        } else {
                            throw new MyTccException(String.format("根据remoteType[%s]找不到相应的处理类", remoteType));
                        }
                    }
                    cache.put(remoteType, iRemote);
                }
            }
        }

        return iRemote;
    }

}
