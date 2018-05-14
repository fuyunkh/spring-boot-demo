package com.example.util;

/**
 * Created by Zhangkh on 2017/9/27.
 */

import com.google.common.base.Strings;
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
public class EncryptUtil {
    private static  Logger logger = LoggerFactory.getLogger(EncryptUtil.class);
    private static int TOKEN_VALID_TIME = 60 * 1000; //单位是毫秒

    static {
        String validTime = System.getenv("TOKEN_VALID_TIME"); //单位是秒
        if (Strings.isNullOrEmpty(validTime)) {
            logger.info("TOKEN_VALID_TIME does not exist,use default(60)!");
        } else {
            try {
                int tvt = Integer.parseInt(validTime);
                logger.debug("TOKEN_VALID_TIME:" + tvt);
                TOKEN_VALID_TIME = tvt > 0 ? tvt * 1000 : TOKEN_VALID_TIME;
            } catch (NumberFormatException e) {
                logger.error("TOKEN_VALID_TIME(" + validTime + " )is not int type,use default(60)!");
            }
        }
    }

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
     * 解密逻辑方法
     *
     * @param message
     * @param key
     * @return
     * @throws Exception
     */
    private static String decryptProcess(String message, String key) throws Exception {
        byte[] bytesrc = convertHexString(message);
        Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
        DESKeySpec desKeySpec = new DESKeySpec(key.getBytes("UTF-8"));
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
        IvParameterSpec iv = new IvParameterSpec(key.getBytes("UTF-8"));
        cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
        byte[] retByte = cipher.doFinal(bytesrc);
        return new String(retByte);
    }

    /**
     * 16进制数组数转化
     *
     * @param ss
     * @return
     */
    private static byte[] convertHexString(String ss) throws Exception {
        byte[] digest = new byte[ss.length() / 2];
        for (int i = 0; i < digest.length; i++) {
            String byteString = ss.substring(2 * i, 2 * i + 2);
            int byteValue = Integer.parseInt(byteString, 16);
            digest[i] = (byte) byteValue;
        }
        return digest;
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
    public static String encrypt(String message, String key) {
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
     * 解密方法
     */
    public static String decrypt(String message, String key) {
        String decStr = null;
        try {
            decStr = java.net.URLDecoder.decode(decryptProcess(message, key), "utf-8");
        } catch (Exception e) {
            logger.error("参数解密异常！", e);
        }
        return decStr;
    }

    public static boolean verifyToken(String accessToken) {
        String key = "1q2w3e4r";
        logger.debug("accessToken：" + accessToken);
        try {
            String message = decrypt(accessToken, key); //user+"&"+yyyy-mm-dd hh:mm:ss
            String time = message.split("&")[1];
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date clientTime = df.parse(time);
            Date nowTime = new Date();
            Long deltaTime = nowTime.getTime() - clientTime.getTime();
            logger.debug("nowTime：" + nowTime.getTime() + ", clientTime：" + clientTime.getTime());
            if (deltaTime < TOKEN_VALID_TIME) {
                logger.debug("less than " + TOKEN_VALID_TIME / 1000 + " seconds");
                return true;
            }
        } catch (Exception e) {
            logger.error("Error verifying Token", e);
        }

        return false;
    }

    public static String getToken(String id) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dt = formatter.format(new Date());
        String token = id + "&" + dt;  //+ "&M0010";
        return encrypt(token, "1q2w3e4r");
    }

    public static void genToken(int num) {
        int cnt = num > 0 ? num : 3;
        String userId = "lijy03";
        for (int i = 0; i < cnt; i++) {
            System.out.println(getToken(userId));
            try {
                Thread.sleep(50 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] argc) {
        genToken(5);
    }

}