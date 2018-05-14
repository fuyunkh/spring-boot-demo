package com.example.util.ip;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.regex.Pattern;

/**
 * Created by Zhangkh on 2018/5/10.
 */
public class IPUtil {

    public static String getLocalHostLANAddress() {
        try {
            InetAddress candidateAddress = null;
            // 遍历所有的网络接口
            for (Enumeration ifaces = NetworkInterface.getNetworkInterfaces(); ifaces.hasMoreElements(); ) {
                NetworkInterface iface = (NetworkInterface) ifaces.nextElement();

                // 在所有的接口下再遍历IP
                for (Enumeration inetAddrs = iface.getInetAddresses(); inetAddrs.hasMoreElements(); ) {
                    InetAddress inetAddr = (InetAddress) inetAddrs.nextElement();
                    if (!inetAddr.isLoopbackAddress()) {// 排除loopback类型地址
                        if (inetAddr.isSiteLocalAddress()) {
                            // 如果是site-local地址，就是它了
                            System.out.println("ip:" + inetAddr.getHostAddress());
                            return inetAddr.getHostAddress();
                        } else if (candidateAddress == null) {
                            // site-local类型的地址未被发现，先记录候选地址
                            candidateAddress = inetAddr;
                        }
                    }
                }
            }
            if (candidateAddress != null) {
                return candidateAddress.getHostAddress();
            }
            // 如果没有发现 non-loopback地址.只能用最次选的方案
            InetAddress jdkSuppliedAddress = InetAddress.getLocalHost();
//            jdkSuppliedAddress.getHostAddress();
            return jdkSuppliedAddress.getHostAddress();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static void getallIP() {
        String regexp = "^[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}$";
        String ip;
        String mac;
        boolean isValid;
        try {
            Enumeration n = null;
            try {
                n = NetworkInterface.getNetworkInterfaces();
            } catch (SocketException e) {
                e.printStackTrace();
            }
            for (; n.hasMoreElements(); ) {
                NetworkInterface e = (NetworkInterface) n.nextElement();
                Enumeration a = e.getInetAddresses();
                for (; a.hasMoreElements(); ) {
                    InetAddress addr = (InetAddress) a.nextElement();
                    ip = addr.getHostAddress();
                    isValid = Pattern.matches(regexp, ip);
//                    addr.isMCSiteLocal()
                    if (isValid) {
                        byte[] m = e.getHardwareAddress();
                        StringBuilder sb = new StringBuilder(18);
//                        System.out.println(new String(m));
                        System.out.println("IP:" + ip);
                        mac = sb.toString();
                        if (ip.equals("127.0.0.1")) continue;
                    }
                }
            }
        } catch (SocketException ex) {

        }

    }

    public static void main(String[] args) throws Exception {
        System.out.println(IPUtil.getLocalHostLANAddress());
//        getallIP();

    }
}
