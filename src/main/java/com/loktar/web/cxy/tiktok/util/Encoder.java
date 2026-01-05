package com.loktar.web.cxy.tiktok.util;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Encoder {

    /** a3 = Object.assign (浅拷贝) */
    @SafeVarargs
    public static Map<String, Object> a3(Map<String, Object> target, Map<String, Object>... sources) {
        for (Map<String, Object> src : sources) {
            if (src != null) target.putAll(src);
        }
        return target;
    }

    /**
     * cm(t) - 你没给源码，这里先按“UTF-8 字节序列”实现：
     * JS 常见等价：new TextEncoder().encode(t)
     */
    public static int[] cm(String t) {
        if (t == null) return new int[0];
        byte[] bytes = t.getBytes(StandardCharsets.UTF_8);
        int[] out = new int[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            out[i] = bytes[i] & 0xFF; // 0..255
        }
        return out;
    }

    /** cg = function(t){ r=cm(t); e.push((5 ^ r[n]).toString(16)); return e.join("") } */
    public static String cg(String t) {
        if (t == null) return "";
        int[] r = cm(t);
        StringBuilder sb = new StringBuilder();
        for (int v : r) {
            int x = (5 ^ v);
            sb.append(Integer.toHexString(x)); // 等价 JS toString(16)，不补0
        }
        return sb.toString();
    }

    /** ch = function(t,e){ ... } */
    public static Map<String, Object> ch(Map<String, Object> t, List<String> keys) {
        int mixMode = 0;
        int fixedMixMode = 0;

        if (t == null || keys == null || keys.isEmpty()) return t;

        Map<String, Object> i = a3(new LinkedHashMap<>(), Map.of("mix_mode", mixMode), t);

        for (String k : keys) {
            Object v = i.get(k);
            if (v != null) {
                mixMode |= 1;
                fixedMixMode |= 1;
                i.put(k, cg(String.valueOf(v)));
            }
        }

        i.put("mix_mode", mixMode);
        i.put("fixed_mix_mode", fixedMixMode);
        return i;
    }

    /** 对应你上层 emailLogin 里 data=i 的构造 */
    public static Map<String, Object> encodeForEmailLogin(String email, String password, Map<String, Object> extraParams) {
        Map<String, Object> base = new LinkedHashMap<>();
        base.put("email", email);
        base.put("password", password);

        Map<String, Object> merged = a3(new LinkedHashMap<>(), base, extraParams == null ? Map.of() : extraParams);
        return ch(merged, Arrays.asList("email", "password"));
    }
    
}
