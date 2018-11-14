package com.example.util;

import com.example.commons.encrypt.TranscodeUtil;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;


public final class AEScipher {

    public static final int AES_IV_LENGTH = 16;

    private AEScipher() {
    }

    private static final String KEY_AES = "AES";

    private static final String ALGORITHM_AES = "AES/CBC/PKCS5Padding";

    public static String generateAESKey(int keySize, String seed) {
        KeyGenerator kgen;
        SecureRandom secureRandom;

        try {
            kgen = KeyGenerator.getInstance(KEY_AES);
            secureRandom = SecureRandom.getInstance("SHA1PRNG");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        secureRandom.setSeed(seed.getBytes());
        kgen.init(keySize, secureRandom);
        SecretKey key = kgen.generateKey();
        return TranscodeUtil.byteArrayToBase64Str(key.getEncoded());
    }

    public static String encryptAES(String data, String key) {
        return cipherAES(data, key, Cipher.ENCRYPT_MODE, null);
    }

    public static String decryptAES(String data, String key) {
        return cipherAES(data, key, Cipher.DECRYPT_MODE, null);
    }

    public static String encryptAES(String data, String key, AlgorithmParameterSpec paramSpec) {
        return cipherAES(data, key, Cipher.ENCRYPT_MODE, paramSpec);
    }

    public static String decryptAES(String data, String key, AlgorithmParameterSpec paramSpec) {
        return cipherAES(data, key, Cipher.DECRYPT_MODE, paramSpec);
    }


    /**
     * Generates a random IV to be used in the encryption process
     *
     * @return The IV's byte representation
     */
    public static byte[] genereteRandomIV() {
        SecureRandom random = new SecureRandom();
        byte[] ivBytes = new byte[16];
        random.nextBytes(ivBytes);
        return ivBytes;
    }


    private static String cipherAES(String data, String key, int mode, AlgorithmParameterSpec paramSpec) {
        Key k = toKey(key, KEY_AES);
        if (paramSpec == null) {
            paramSpec = new IvParameterSpec(k.getEncoded(), 0, AES_IV_LENGTH);
        }
        try {
            Cipher ecipher = Cipher.getInstance(ALGORITHM_AES);
            ecipher.init(mode, k, paramSpec);
            return mode == Cipher.DECRYPT_MODE ? new String(ecipher.doFinal(TranscodeUtil
                    .base64StrToByteArray(data))) : TranscodeUtil.byteArrayToBase64Str(ecipher
                    .doFinal(data.getBytes()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    private static Key toKey(String key, String algorithm) {
        return new SecretKeySpec(TranscodeUtil.base64StrToByteArray(key), algorithm);
    }

}
