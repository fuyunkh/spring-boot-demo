package com.example.util;

/**
 * Created by Zhangkh on 2017/9/27.
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * FileName：EncryptUtil.java
 * <p>
 * Description：DES加密解密工具类
 */
public class EncryptClientUtil {
    private static Logger logger = LoggerFactory.getLogger(EncryptClientUtil.class);

    /**
     * 加密逻辑方法
     *
     * @param message
     * @param key
     * @return
     * @throws Exception
     */
    private static byte[] encryptProcess(String message, String key) throws Exception {
        Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
        DESKeySpec desKeySpec = new DESKeySpec(key.getBytes("UTF-8"));
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
        IvParameterSpec iv = new IvParameterSpec(key.getBytes("UTF-8"));
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
        return cipher.doFinal(message.getBytes("UTF-8"));
    }

    /**
     * 十六进制数转化
     *
     * @param b
     * @return
     * @throws Exception
     */
    private static String toHexString(byte[] b) throws Exception {
        StringBuffer hexString = new StringBuffer();
        for (int i = 0; i < b.length; i++) {
            String plainText = Integer.toHexString(0xff & b[i]);
            if (plainText.length() < 2)
                plainText = "0" + plainText;
            hexString.append(plainText);
        }
        return hexString.toString();
    }

    /**
     * 加密方法
     */
    private static String encrypt(String message, String key) {
        String enStr = null;
        try {
            String orignStr = java.net.URLEncoder.encode(message, "utf-8");
            enStr = toHexString(encryptProcess(orignStr, key));
        } catch (Exception e) {
            logger.error("参数加密异常！", e);
        }
        return enStr;
    }

    /**
     * @param userAccount 业务系统账号
     * @param sysCode     业务系统代码
     * @return
     */
    public static String getToken(String userAccount, String sysCode) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dt = formatter.format(new Date());
        String token = userAccount + "&" + dt + "&" + sysCode;  //+ "&M0013";
        return encrypt(token, "1q2w3e4r");
    }
}