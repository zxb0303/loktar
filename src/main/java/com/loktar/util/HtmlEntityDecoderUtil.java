package com.loktar.util;

import org.springframework.web.util.HtmlUtils;

import java.lang.reflect.Field;

public class HtmlEntityDecoderUtil {
    public static void decodeHtmlEntities(Object obj) {
        if (obj == null) {
            return;
        }

        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.getType().equals(String.class)) {
                field.setAccessible(true);
                try {
                    String original = (String) field.get(obj);
                    if (original != null) {
                        String decoded = HtmlUtils.htmlUnescape(original);
                        field.set(obj, decoded);
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
