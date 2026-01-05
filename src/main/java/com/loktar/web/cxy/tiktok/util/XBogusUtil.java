package com.loktar.web.cxy.tiktok.util;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class XBogusUtil {

    private static final String STANDARD_B64 = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
    // 注意：根据你提供的 JS 代码，该表共 64 位，最后一位是 'e'
    private static final String CUSTOM_B64   = "Dkdpgh4ZKsQB80/Mfvw36XI1R25-WUAlEi7NLboqYTOPuzmFjJnryx9HVGcaStCe";

    private static final Map<Character, Character> ENC_TRANS = new HashMap<>();

    static {
        for (int i = 0; i < STANDARD_B64.length(); i++) {
            ENC_TRANS.put(STANDARD_B64.charAt(i), CUSTOM_B64.charAt(i));
        }
    }

    /**
     * 对应 JS 的 encrypt 函数
     */
    public static String encrypt(String params, String postData, String userAgent, long timestamp) {
        try {
            byte[] uaKey = {0x00, 0x01, 0x0e};
            byte[] listKey = {(byte) 0xff};
            int fixedVal = 0x4a41279f;

            // 1. Double MD5
            byte[] md5Params = doubleMd5(params.getBytes(StandardCharsets.UTF_8));
            byte[] md5Post   = doubleMd5(postData.getBytes(StandardCharsets.UTF_8));

            // 2. UA -> RC4 -> Base64 -> MD5
            byte[] uaRc4 = rc4(uaKey, userAgent.getBytes(StandardCharsets.UTF_8));
            String uaB64 = Base64.getEncoder().encodeToString(uaRc4);
            byte[] md5Ua  = singleMd5(uaB64.getBytes(StandardCharsets.US_ASCII));

            // 3. 构建 18 字节 Buffer
            ByteBuffer buffer = ByteBuffer.allocate(19); // 18 字节 + 1 字节 Checksum
            buffer.put((byte) 0x40);
            buffer.put(uaKey);
            // JS: subarray(14, 16) -> 第 14 和 15 字节
            buffer.put(md5Params, 14, 2);
            buffer.put(md5Post, 14, 2);
            buffer.put(md5Ua, 14, 2);
            buffer.putInt((int) (timestamp & 0xFFFFFFFFL));
            buffer.putInt(fixedVal);

            // 4. 计算 Checksum (XOR)
            byte checksum = 0;
            for (int i = 0; i < 18; i++) {
                checksum ^= buffer.get(i);
            }
            buffer.put(checksum);

            // 5. 最终 RC4 封装
            byte[] innerData = buffer.array();
            byte[] encryptedInner = rc4(listKey, innerData);

            ByteBuffer finalBuf = ByteBuffer.allocate(2 + encryptedInner.length);
            finalBuf.put((byte) 0x02);
            finalBuf.put(listKey);
            finalBuf.put(encryptedInner);

            // 6. 自定义 Base64 编码
            return customBase64Encode(finalBuf.array());

        } catch (Exception e) {
            throw new RuntimeException("Encryption failed", e);
        }
    }

    // --- 工具方法 ---

    private static byte[] singleMd5(byte[] input) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        return md.digest(input);
    }

    private static byte[] doubleMd5(byte[] input) throws NoSuchAlgorithmException {
        return singleMd5(singleMd5(input));
    }

    private static byte[] rc4(byte[] key, byte[] data) {
        int[] s = new int[256];
        for (int i = 0; i < 256; i++) s[i] = i;

        int j = 0;
        for (int i = 0; i < 256; i++) {
            j = (j + s[i] + (key[i % key.length] & 0xFF)) & 0xFF;
            int temp = s[i];
            s[i] = s[j];
            s[j] = temp;
        }

        byte[] out = new byte[data.length];
        int i = 0;
        j = 0;
        for (int n = 0; n < data.length; n++) {
            i = (i + 1) & 0xFF;
            j = (j + s[i]) & 0xFF;
            int temp = s[i];
            s[i] = s[j];
            s[j] = temp;
            int k = s[(s[i] + s[j]) & 0xFF];
            out[n] = (byte) ((data[n] & 0xFF) ^ k);
        }
        return out;
    }

    private static String customBase64Encode(byte[] input) {
        String stdB64 = Base64.getEncoder().encodeToString(input);
        StringBuilder sb = new StringBuilder();
        for (char c : stdB64.toCharArray()) {
            sb.append(ENC_TRANS.getOrDefault(c, c));
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        // 测试
        String result = encrypt("param_test", "post_test", "Mozilla/5.0", 1735620000L);
        System.out.println("Signature: " + result);
    }
}