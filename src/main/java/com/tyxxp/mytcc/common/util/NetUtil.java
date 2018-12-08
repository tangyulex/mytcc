package com.tyxxp.mytcc.common.util;

import com.alibaba.dubbo.common.utils.NetUtils;
import com.tyxxp.mytcc.common.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

/**
 * Net工具类
 *
 * @author tangyu
 */
public class NetUtil {

    private static final Logger logger = LoggerFactory.getLogger(NetUtils.getLocalHost());

    /**
     * 缓存本地InetAddress
     */
    private static volatile InetAddress LOCAL_ADDRESS = null;

    /**
     * 获取ip地址
     */
    public static InetAddress getLocalHostAddress() {
        if(LOCAL_ADDRESS != null) {
            return LOCAL_ADDRESS;
        }
        try {
            InetAddress candidateAddress = null;
            for (Enumeration ifaces = NetworkInterface.getNetworkInterfaces(); ifaces.hasMoreElements(); ) {
                NetworkInterface iface = (NetworkInterface) ifaces.nextElement();
                for (Enumeration inetAddrs = iface.getInetAddresses(); inetAddrs.hasMoreElements(); ) {
                    InetAddress inetAddr = (InetAddress) inetAddrs.nextElement();
                    if (!inetAddr.isLoopbackAddress()) {
                        if (inetAddr.isSiteLocalAddress()) {
                            return LOCAL_ADDRESS = inetAddr;
                        } else if (candidateAddress == null) {
                            candidateAddress = inetAddr;
                        }
                    }
                }
            }
            if (candidateAddress != null) {
                return LOCAL_ADDRESS = candidateAddress;
            }
            return LOCAL_ADDRESS = InetAddress.getLocalHost();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return null;
    }

    /**
     * 获取本机ip
     */
    public static String getLocalHost() {
        InetAddress address = getLocalHostAddress();
        return address == null ? Constant.DEFAULT_LOCAL_HOST : address.getHostAddress();
    }
}
