package com.loktar.web.cxy.tiktok.util;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.*;

public class StrDataUtil {

    private static final long MASK32 = 0xFFFFFFFFL;
    private static final String CS = "Dkdpgh4ZKsQB80/Mfvw36XI1R25+WUAlEi7NLboqYTOPuzmFjJnryx9HVGcaStCe=";
    private static final char PAD = CS.charAt(64);
    private static final int[] INV = new int[256];

    static {
        Arrays.fill(INV, -1);
        for (int i = 0; i < CS.length(); i++) INV[CS.charAt(i)] = i;
    }

    private static final long[] AA = {
            73,110,149,151,103,107,13,5,4294967296L,154,2718276124L,211147047,2931180889L,142,
            185100057,17,37,7,3212677781L,217618912,16,79,4294967295L,4,120,175,133,2,
            0, 600974999,200,188,14,36,3,124,156,
            2633865432L,163,1451689750L,3863347763L,8,2157053261L,112,28,138,288,258,3732962506L,
            172,101,1,116,83,203,11,1196819126,1498001188,15,122,118,77,159,136,2903579748L,
            147,92,12,193,6,18,10,114,32,9,0,131,128,42,2517678443L
    };

    private static final int[] ME = new int[16];
    private static final int[] GE = {(int)AA[56], (int)AA[29], (int)AA[40], (int)AA[39]};
    private static int ye = (int)AA[75];

    static {
        SecureRandom sr = new SecureRandom();
        ME[0] = (int)AA[79]; ME[1] = (int)AA[10]; ME[2] = (int)AA[18]; ME[3] = (int)AA[37];
        ME[4] = (int)AA[19]; ME[5] = (int)AA[12]; ME[6] = (int)AA[57]; ME[7] = (int)AA[42];
        ME[8] = (int)AA[11]; ME[9] = (int)AA[14]; ME[10] = (int)AA[64]; ME[11] = (int)AA[48];
        ME[12] = (int)(AA[22] & System.currentTimeMillis());
        ME[13] = sr.nextInt(); ME[14] = sr.nextInt(); ME[15] = sr.nextInt();
        // 临时修改用于对比
//        ME[12] = 12345678;
//        ME[13] = 1; ME[14] = 2; ME[15] = 3;
    }

    private static int u32(long x) { return (int)(x & MASK32); }
    private static int rotl32(int x, int n) { return (x << n) | (x >>> (32 - n)); }

    private static void q(int[] s, int a, int b, int c, int d) {
        s[a] = u32(Integer.toUnsignedLong(s[a]) + Integer.toUnsignedLong(s[b]));
        s[d] = rotl32(s[d] ^ s[a], 16);
        s[c] = u32(Integer.toUnsignedLong(s[c]) + Integer.toUnsignedLong(s[d]));
        s[b] = rotl32(s[b] ^ s[c], 12);
        s[a] = u32(Integer.toUnsignedLong(s[a]) + Integer.toUnsignedLong(s[b]));
        s[d] = rotl32(s[d] ^ s[a], 8);
        s[c] = u32(Integer.toUnsignedLong(s[c]) + Integer.toUnsignedLong(s[d]));
        s[b] = rotl32(s[b] ^ s[c], 7);
    }

    private static int[] chachaBlock(int[] st, int rounds) {
        int[] x = st.clone();
        for (int r = 0; r < rounds; ) {
            q(x, 0, 4, 8, 12); q(x, 1, 5, 9, 13); q(x, 2, 6, 10, 14); q(x, 3, 7, 11, 15);
            if (++r >= rounds) break;
            q(x, 0, 5, 10, 15); q(x, 1, 6, 11, 12); q(x, 2, 7, 12, 13); q(x, 3, 4, 13, 14);
            ++r;
        }
        for (int i = 0; i < 16; i++) x[i] = u32(Integer.toUnsignedLong(x[i]) + Integer.toUnsignedLong(st[i]));
        return x;
    }

    private static void bump(int[] s) { s[12] = u32(Integer.toUnsignedLong(s[12]) + 1); }

    private static double _rand() {
        long[] rf = {4294965248L, 0, 4294967296L, 2, 8, 11, 53, 7};
        int[] blk = chachaBlock(ME, (int)rf[4]);
        long t = Integer.toUnsignedLong(blk[ye]);
        long r = (rf[0] & Integer.toUnsignedLong(blk[ye + (int)rf[4]])) >>> rf[5];
        if (ye == (int)rf[7]) { bump(ME); ye = 0; } else ye++;
        return (t + (double)rf[2] * r) / Math.pow(2, rf[6]);
    }

    // --- LZW 核心，确保位处理与 JS 100% 一致 ---
    public static byte[] lzwEncode(byte[] data) {
        Map<String, Integer> dict = new HashMap<>();
        for (int i = 0; i < 256; i++) dict.put("" + (char)i, i);
        int next = 256, width = 8, bucket = 0, filled = 0;
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        String w = "";
        for (byte b : data) {
            char c = (char)(b & 0xFF);
            String wc = w + c;
            if (dict.containsKey(wc)) {
                w = wc;
            } else {
                int code = dict.get(w);
                bucket |= (code << filled);
                filled += width;
                while (filled >= 8) {
                    out.write(bucket & 0xFF);
                    bucket >>= 8; // JS encode 使用 >>
                    filled -= 8;
                }
                dict.put(wc, next++);
                if (next > (1 << width)) width++;
                w = "" + c;
            }
        }
        if (!w.isEmpty()) {
            bucket |= (dict.get(w) << filled);
            filled += width;
            while (filled > 0) {
                out.write(bucket & 0xFF);
                bucket >>= 8;
                filled -= 8;
            }
        }
        return out.toByteArray();
    }

    public static byte[] lzwDecode(byte[] bytes) {
        if (bytes.length == 0) return new byte[0];
        Map<Integer, byte[]> dict = new HashMap<>();
        for (int i = 0; i < 256; i++) dict.put(i, new byte[]{(byte)i});
        int next = 256, width = 8, bucket = 0, filled = 0, idx = 0;

        // Helper to read bits from stream
        Runnable fillBucket = () -> { }; // dummy

        // Read first code
        while (filled < width && idx < bytes.length) {
            bucket |= (bytes[idx++] & 0xFF) << filled;
            filled += 8;
        }
        if (filled < width) return new byte[0];
        int first = bucket & ((1 << width) - 1);
        bucket >>>= width; // JS decode 使用 >>>
        filled -= width;

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] w = dict.get(first);
        out.write(w, 0, w.length);

        while (true) {
            if (next == (1 << width)) width++;
            while (filled < width && idx < bytes.length) {
                bucket |= (bytes[idx++] & 0xFF) << filled;
                filled += 8;
            }
            if (filled < width) break;
            int k = bucket & ((1 << width) - 1);
            bucket >>>= width;
            filled -= width;

            byte[] entry;
            if (dict.containsKey(k)) {
                entry = dict.get(k);
            } else if (k == next) {
                entry = new byte[w.length + 1];
                System.arraycopy(w, 0, entry, 0, w.length);
                entry[w.length] = w[0];
            } else throw new RuntimeException("Corrupt");

            out.write(entry, 0, entry.length);
            byte[] combo = new byte[w.length + 1];
            System.arraycopy(w, 0, combo, 0, w.length);
            combo[w.length] = entry[0];
            dict.put(next++, combo);
            w = entry;
        }
        return out.toByteArray();
    }

    private static void xorChaCha(int[] keyWords, int rounds, byte[] bytes) {
        int[] words = new int[(bytes.length + 3) / 4];
        for (int i = 0; i < bytes.length; i++) {
            words[i >>> 2] |= (bytes[i] & 0xFF) << ((i & 3) << 3);
        }
        int[] st = keyWords.clone();
        int off = 0;
        while (off + 16 < words.length) {
            int[] ks = chachaBlock(st, rounds);
            bump(st);
            for (int j = 0; j < 16; j++) words[off + j] ^= ks[j];
            off += 16;
        }
        int[] ksTail = chachaBlock(st, rounds);
        for (int j = off; j < words.length; j++) words[j] ^= ksTail[j - off];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte)((words[i >>> 2] >>> ((i & 3) << 3)) & 0xFF);
        }
    }

    public static String encryptStr(String plain) {
        int[] key = new int[12];
        byte[] keyBytes = new byte[48];
        int acc = 0;
        for (int i = 0; i < 12; i++) {
            int num = u32((long)(_rand() * Math.pow(2, 32)));
            key[i] = num;
            acc = (acc + (num & 15)) & 15;
            keyBytes[i*4] = (byte)(num & 0xFF);
            keyBytes[i*4+1] = (byte)((num >>> 8) & 0xFF);
            keyBytes[i*4+2] = (byte)((num >>> 16) & 0xFF);
            keyBytes[i*4+3] = (byte)((num >>> 24) & 0xFF);
        }
        int rounds = acc + 5;

        byte[] lzwBytes = lzwEncode(plain.getBytes(StandardCharsets.UTF_8));
        int[] st = new int[16];
        System.arraycopy(GE, 0, st, 0, 4);
        System.arraycopy(key, 0, st, 4, 12);
        xorChaCha(st, rounds, lzwBytes);

        int split = 0;
        for (byte b : keyBytes) split = (split + (b & 0xFF)) % (lzwBytes.length + 1);
        for (byte b : lzwBytes) split = (split + (b & 0xFF)) % (lzwBytes.length + 1);

        byte[] res = new byte[1 + lzwBytes.length + 48];
        res[0] = 'L';
        System.arraycopy(lzwBytes, 0, res, 1, split);
        System.arraycopy(keyBytes, 0, res, 1 + split, 48);
        System.arraycopy(lzwBytes, split, res, 1 + split + 48, lzwBytes.length - split);

        return b64enc(res);
    }

    public static String decryptStr(String token) {
        byte[] raw = b64dec(token);
        if (raw.length == 0 || raw[0] != 'L') return "Error: Malformed";
        byte[] merged = Arrays.copyOfRange(raw, 1, raw.length);
        int total = merged.length - 48;

        for (int split = 0; split <= total; split++) {
            byte[] keyChunk = Arrays.copyOfRange(merged, split, split + 48);
            int[] key = new int[12];
            int acc = 0;
            for (int i = 0; i < 12; i++) {
                int w = (keyChunk[i*4] & 0xFF) | ((keyChunk[i*4+1] & 0xFF) << 8) |
                        ((keyChunk[i*4+2] & 0xFF) << 16) | ((keyChunk[i*4+3] & 0xFF) << 24);
                key[i] = w;
                acc = (acc + (w & 15)) & 15;
            }
            int rounds = acc + 5;
            if (rounds > 20) continue;

            byte[] cipher = new byte[total];
            System.arraycopy(merged, 0, cipher, 0, split);
            System.arraycopy(merged, split + 48, cipher, split, total - split);

            try {
                int[] st = new int[16];
                System.arraycopy(GE, 0, st, 0, 4);
                System.arraycopy(key, 0, st, 4, 12);
                xorChaCha(st, rounds, cipher);
                byte[] decoded = lzwDecode(cipher);
                String res = new String(decoded, StandardCharsets.UTF_8);
                // 校验：是否是正常的 ASCII
                if (res.length() > 0 && res.chars().limit(10).allMatch(c -> (c >= 32 && c < 127) || c == '\n')) {
                    return res;
                }
            } catch (Exception ignored) {}
        }
        return "Error: Key not found";
    }

    private static String b64enc(byte[] raw) {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (; i + 2 < raw.length; i += 3) {
            int v = ((raw[i] & 0xFF) << 16) | ((raw[i+1] & 0xFF) << 8) | (raw[i+2] & 0xFF);
            sb.append(CS.charAt((v >> 18) & 63)).append(CS.charAt((v >> 12) & 63))
                    .append(CS.charAt((v >> 6) & 63)).append(CS.charAt(v & 63));
        }
        if (i < raw.length) {
            int rem = raw.length - i;
            int v = (raw[i] & 0xFF) << 16 | (rem == 2 ? (raw[i+1] & 0xFF) << 8 : 0);
            sb.append(CS.charAt((v >> 18) & 63)).append(CS.charAt((v >> 12) & 63))
                    .append(rem == 1 ? PAD : CS.charAt((v >> 6) & 63)).append(PAD);
        }
        return sb.toString();
    }

    private static byte[] b64dec(String str) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        for (int i = 0; i < str.length(); i += 4) {
            int a = INV[str.charAt(i)], b = INV[str.charAt(i+1)];
            int c = str.charAt(i+2) == PAD ? 0 : INV[str.charAt(i+2)];
            int d = str.charAt(i+3) == PAD ? 0 : INV[str.charAt(i+3)];
            int v = (a << 18) | (b << 12) | (c << 6) | d;
            out.write((v >> 16) & 0xFF);
            if (str.charAt(i+2) != PAD) out.write((v >> 8) & 0xFF);
            if (str.charAt(i+3) != PAD) out.write(v & 0xFF);
        }
        return out.toByteArray();
    }

    public static void main(String[] args) {
        String original = "Hello";
        String enc = encryptStr(original);
        System.out.println("Encrypted: " + enc);
        System.out.println("Decrypted: " + decryptStr(enc));
    }
}