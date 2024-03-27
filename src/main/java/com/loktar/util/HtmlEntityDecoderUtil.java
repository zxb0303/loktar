package com.loktar.util;

import org.springframework.web.util.HtmlUtils;

import java.lang.reflect.Field;

public class HtmlEntityDecoderUtil {
    public static void decodeHtmlEntities(Object obj) {
        if (obj == null) {
            return;
        }

        Field[] fields = obj.getClass().getDeclaredFields(); // 获取所有字段
        for (Field field : fields) {
            if (field.getType().equals(String.class)) { // 只处理字符串类型的字段
                field.setAccessible(true); // 确保私有字段也可以访问
                try {
                    String original = (String) field.get(obj);
                    if (original != null) {
                        String decoded = HtmlUtils.htmlUnescape(original);
                        field.set(obj, decoded); // 设置解码后的值
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
