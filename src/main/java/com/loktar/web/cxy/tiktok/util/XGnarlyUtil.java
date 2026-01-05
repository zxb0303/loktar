package com.loktar.web.cxy.tiktok.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class XGnarlyUtil {

    private static final long[] AA = {
            0xFFFFFFFFL, 138, 1498001188, 211147047, 253, 0, 203, 288, 9,
            1196819126, 3212677781L, 135, 263, 193, 58, 18, 244, 2931180889L, 240, 173,
            268, 2157053261L, 261, 175, 14, 5, 171, 270, 156, 258, 13, 15, 3732962506L,
            185, 169, 2, 6, 132, 162, 200, 3, 160, 217618912, 62, 2517678443L, 44, 164,
            4, 96, 183, 2903579748L, 3863347763L, 119, 181, 10, 190, 8, 2654435769L, 259,
            104, 230, 128, 2633865432L, 225, 1, 257, 143, 179, 16, 600974999, 185100057,
            32, 188, 53, 2718276124L, 177, 196, 4294967296L, 147, 117, 17, 49, 7, 28, 12,
            266, 216, 11, 0, 45, 166, 247, 1451689750L
    };

    private static final int[] OT = {(int) AA[9], (int) AA[69], (int) AA[51], (int) AA[92]};

    // PRNG 状态需要每次加密时重置，以匹配 JS 的初次运行结果
    private static int[] kt;
    private static int st_ptr;

    private static void resetPrng() {
        SecureRandom sr = new SecureRandom();
        kt = new int[16];
        kt[0] = (int) AA[44];
        kt[1] = (int) AA[74];
        kt[2] = (int) AA[10];
        kt[3] = (int) AA[62];
        kt[4] = (int) AA[42];
        kt[5] = (int) AA[17];
        kt[6] = (int) AA[2];
        kt[7] = (int) AA[21];
        kt[8] = (int) AA[3];
        kt[9] = (int) AA[70];
        kt[10] = (int) AA[50];
        kt[11] = (int) AA[32];
        kt[12] = (int) (AA[0] & System.currentTimeMillis());
        kt[13] = sr.nextInt();
        kt[14] = sr.nextInt();
        kt[15] = sr.nextInt();
        // 固定种子测试
//        kt[12] = 12345678;
//        kt[13] = 111;
//        kt[14] = 222;
//        kt[15] = 333;
        st_ptr = (int) AA[88];
    }

    private static int u32(long x) {
        return (int) (x & 0xFFFFFFFFL);
    }

    private static int rotl(int x, int n) {
        return (x << n) | (x >>> (32 - n));
    }

    private static void quarter(int[] st, int a, int b, int c, int d) {
        st[a] = u32(Integer.toUnsignedLong(st[a]) + Integer.toUnsignedLong(st[b]));
        st[d] = rotl(st[d] ^ st[a], 16);
        st[c] = u32(Integer.toUnsignedLong(st[c]) + Integer.toUnsignedLong(st[d]));
        st[b] = rotl(st[b] ^ st[c], 12);
        st[a] = u32(Integer.toUnsignedLong(st[a]) + Integer.toUnsignedLong(st[b]));
        st[d] = rotl(st[d] ^ st[a], 8);
        st[c] = u32(Integer.toUnsignedLong(st[c]) + Integer.toUnsignedLong(st[d]));
        st[b] = rotl(st[b] ^ st[c], 7);
    }

    private static int[] chachaBlock(int[] state, int rounds) {
        int[] w = state.clone();
        for (int r = 0; r < rounds; ) {
            quarter(w, 0, 4, 8, 12); quarter(w, 1, 5, 9, 13);
            quarter(w, 2, 6, 10, 14); quarter(w, 3, 7, 11, 15);
            if (++r >= rounds) break;
            quarter(w, 0, 5, 10, 15); quarter(w, 1, 6, 11, 12);
            quarter(w, 2, 7, 12, 13); quarter(w, 3, 4, 13, 14);
            ++r;
        }
        for (int i = 0; i < 16; i++) {
            w[i] = u32(Integer.toUnsignedLong(w[i]) + Integer.toUnsignedLong(state[i]));
        }
        return w;
    }

    private static double rand() {
        int[] e = chachaBlock(kt, 8);
        long t = Integer.toUnsignedLong(e[st_ptr]);
        long r = (Integer.toUnsignedLong(e[st_ptr + 8]) & 0xFFFFFFF0L) >>> 11;
        if (st_ptr == 7) {
            kt[12] = u32(Integer.toUnsignedLong(kt[12]) + 1);
            st_ptr = 0;
        } else {
            st_ptr++;
        }
        return (t + 4294967296.0 * r) / Math.pow(2, 53);
    }

    private static byte[] numToBytes(int val) {
        if (val < 65025) {
            return new byte[]{(byte) ((val >> 8) & 0xFF), (byte) (val & 0xFF)};
        } else {
            return new byte[]{
                    (byte) ((val >> 24) & 0xFF), (byte) ((val >> 16) & 0xFF),
                    (byte) ((val >> 8) & 0xFF), (byte) (val & 0xFF)
            };
        }
    }

    private static long beIntFromStr(String str) {
        byte[] buf = str.getBytes(StandardCharsets.UTF_8);
        long acc = 0;
        int len = Math.min(buf.length, 4);
        for (int i = 0; i < len; i++) {
            acc = (acc << 8) | (buf[i] & 0xFF);
        }
        return acc & 0xFFFFFFFFL;
    }

    private static String md5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            return "";
        }
    }

    private static byte[] encryptChaCha(int[] keyWords, int rounds, byte[] bytes) {
        int nFull = bytes.length / 4;
        int leftover = bytes.length % 4;
        int[] words = new int[(int) Math.ceil(bytes.length / 4.0)];

        for (int i = 0; i < nFull; i++) {
            int j = 4 * i;
            words[i] = (bytes[j] & 0xFF) | ((bytes[j + 1] & 0xFF) << 8) |
                    ((bytes[j + 2] & 0xFF) << 16) | ((bytes[j + 3] & 0xFF) << 24);
        }
        if (leftover > 0) {
            int v = 0;
            int base = 4 * nFull;
            for (int c = 0; c < leftover; c++) v |= (bytes[base + c] & 0xFF) << (8 * c);
            words[nFull] = v;
        }

        int o = 0;
        int[] state = keyWords.clone();
        while (o + 16 < words.length) {
            int[] stream = chachaBlock(state, rounds);
            state[12] = u32(Integer.toUnsignedLong(state[12]) + 1);
            for (int k = 0; k < 16; k++) words[o + k] ^= stream[k];
            o += 16;
        }
        int remain = words.length - o;
        int[] streamTail = chachaBlock(state, rounds);
        for (int k = 0; k < remain; k++) words[o + k] ^= streamTail[k];

        byte[] out = new byte[bytes.length];
        for (int i = 0; i < nFull; i++) {
            int w = words[i];
            int j = 4 * i;
            out[j] = (byte) (w & 0xFF);
            out[j + 1] = (byte) ((w >> 8) & 0xFF);
            out[j + 2] = (byte) ((w >> 16) & 0xFF);
            out[j + 3] = (byte) ((w >> 24) & 0xFF);
        }
        if (leftover > 0) {
            int w = words[nFull];
            int base = 4 * nFull;
            for (int c = 0; c < leftover; c++) out[base + c] = (byte) ((w >> (8 * c)) & 0xFF);
        }
        return out;
    }

    public static String encrypt(String queryString, String body, String userAgent, int envcode, String version, Long timestampOverride) {
        resetPrng(); // 关键：确保每次生成的 keyWords 一致
        long timestampMs = (timestampOverride != null) ? timestampOverride : 1735620000000L;

        Map<Integer, Object> obj = new LinkedHashMap<>();
        obj.put(1, 1);
        obj.put(2, envcode);
        obj.put(3, md5(queryString));
        obj.put(4, md5(body));
        obj.put(5, md5(userAgent));
        obj.put(6, (int) (timestampMs / 1000));
        obj.put(7, 1508145731);
        obj.put(8, (int) ((timestampMs * 1000) % 2147483648L));
        obj.put(9, version);

        if ("5.1.1".equals(version)) {
            obj.put(10, "1.0.0.314");
            obj.put(11, 1);
            long v12 = 0;
            for (int i = 1; i <= 11; i++) {
                Object v = obj.get(i);
                long toXor = (v instanceof Number) ? ((Number) v).longValue() : beIntFromStr((String) v);
                v12 ^= toXor;
            }
            obj.put(12, (int) (v12 & 0xFFFFFFFFL));
        }

        // v0 应该在 key 10,11,12 之后计算
        long v0 = 0;
        for (int i = 1; i <= obj.size(); i++) {
            Object v = obj.get(i);
            if (v instanceof Number) v0 ^= ((Number) v).longValue();
        }
        obj.put(0, (int) (v0 & 0xFFFFFFFFL));

        // 序列化
        List<Byte> payload = new ArrayList<>();
        payload.add((byte) obj.size());
        for (Map.Entry<Integer, Object> entry : obj.entrySet()) {
            payload.add(entry.getKey().byteValue());
            byte[] valBytes;
            if (entry.getValue() instanceof Number) {
                valBytes = numToBytes(((Number) entry.getValue()).intValue());
            } else {
                valBytes = ((String) entry.getValue()).getBytes(StandardCharsets.UTF_8);
            }
            for (byte b : numToBytes(valBytes.length)) payload.add(b);
            for (byte b : valBytes) payload.add(b);
        }

        byte[] payloadArr = new byte[payload.size()];
        for (int i = 0; i < payload.size(); i++) payloadArr[i] = payload.get(i);

        // 生成 KeyWords 和 Rounds
        int[] keyWords = new int[12];
        byte[] keyBytes = new byte[48];
        int roundAccum = 0;
        for (int i = 0; i < 12; i++) {
            long rndWord = (long) (rand() * 4294967296.0);
            keyWords[i] = u32(rndWord);
            roundAccum = (roundAccum + (keyWords[i] & 15)) & 15;
            keyBytes[i * 4] = (byte) (keyWords[i] & 0xFF);
            keyBytes[i * 4 + 1] = (byte) ((keyWords[i] >> 8) & 0xFF);
            keyBytes[i * 4 + 2] = (byte) ((keyWords[i] >> 16) & 0xFF);
            keyBytes[i * 4 + 3] = (byte) ((keyWords[i] >> 24) & 0xFF);
        }
        int rounds = roundAccum + 5;

        // ChaCha 加密 (对应 JS 的 Ab22)
        int[] fullState = new int[16];
        System.arraycopy(OT, 0, fullState, 0, 4);
        System.arraycopy(keyWords, 0, fullState, 4, 12);
        byte[] enc = encryptChaCha(fullState, rounds, payloadArr);

        // Splice 拼接
        int insertPos = 0;
        for (byte b : keyBytes) insertPos = (insertPos + (b & 0xFF)) % (enc.length + 1);
        for (byte b : enc) insertPos = (insertPos + (b & 0xFF)) % (enc.length + 1);

        byte[] finalBytes = new byte[1 + enc.length + 48];
        finalBytes[0] = (byte) 75; // 'K'
        System.arraycopy(enc, 0, finalBytes, 1, insertPos);
        System.arraycopy(keyBytes, 0, finalBytes, 1 + insertPos, 48);
        System.arraycopy(enc, insertPos, finalBytes, 1 + insertPos + 48, enc.length - insertPos);

        // Custom Base64
        String alphabet = "u09tbS3UvgDEe6r-ZVMXzLpsAohTn7mdINQlW412GqBjfYiyk8JORCF5/xKHwacP=";
        StringBuilder out = new StringBuilder();
        int fullLen = (finalBytes.length / 3) * 3;
        for (int i = 0; i < fullLen; i += 3) {
            int block = ((finalBytes[i] & 0xFF) << 16) | ((finalBytes[i + 1] & 0xFF) << 8) | (finalBytes[i + 2] & 0xFF);
            out.append(alphabet.charAt((block >> 18) & 63));
            out.append(alphabet.charAt((block >> 12) & 63));
            out.append(alphabet.charAt((block >> 6) & 63));
            out.append(alphabet.charAt(block & 63));
        }
        return out.toString();
    }

    public static void main(String[] args) {
        String qs = "device_id=123456";
        String body = "{}";
        String ua = "Mozilla/5.0";

        // JS 中的默认 timestamp 是 1735620000000
        String token = encrypt(qs, body, ua, 0, "5.1.1", 1735620000000L);
        System.out.println("[+] X-Gnarly: " + token);
    }
}