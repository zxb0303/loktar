package com.loktar.web.cxy;

import com.loktar.dto.openai.OpenAiRequest;
import com.loktar.dto.openai.OpenAiResponse;
import com.loktar.util.ChatGPTUtil;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ExcelTranslator {

    private final ChatGPTUtil chatGPTUtil;


    public void translateExcel(String inputPath, String outputPath) throws Exception {
        try (InputStream is = new FileInputStream(inputPath);
             Workbook workbook = new XSSFWorkbook(is)) {

            // 缓存，避免相同英文重复调用 GPT，节省费用 & 提高速度
            Map<String, String> cache = new HashMap<>();

            // 遍历所有 sheet（你说有 3 个，这里不限定数量，统一处理）
            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                Sheet sheet = workbook.getSheetAt(i);
                translateSheet(sheet, cache);
            }

            try (FileOutputStream fos = new FileOutputStream(outputPath)) {
                workbook.write(fos);
            }
        }
    }

    private void translateSheet(Sheet sheet, Map<String, String> cache) throws Exception {
        for (Row row : sheet) {
            for (Cell cell : row) {
                if (cell.getCellType() == CellType.STRING) {
                    String text = cell.getStringCellValue();
                    if (containsEnglish(text)) {
                        String translated = translateTextWithCache(text, cache);
                        cell.setCellValue(translated);
                    }
                }
            }
        }
    }

    /** 简单判断是否包含英文字母 */
    private boolean containsEnglish(String s) {
        if (s == null || s.isEmpty()) return false;
        for (char c : s.toCharArray()) {
            if ((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z')) {
                return true;
            }
        }
        return false;
    }

    private String translateTextWithCache(String text, Map<String, String> cache) throws Exception {
        // 去掉前后空白做缓存 key
        String key = text.trim();
        if (cache.containsKey(key)) {
            String cached = cache.get(key);
            System.out.println("[缓存命中] " + text + "  =>  " + cached);
            return cached;
        }

        // 调用 ChatGPT 翻译
        OpenAiRequest request = ChatGPTUtil.getTranslateRequest(text);
        OpenAiResponse response = chatGPTUtil.completions(request);
        if (response == null || response.getChoices() == null || response.getChoices().isEmpty()) {
            // 出错时，保留原文
            System.out.println("[翻译失败，保留原文] " + text);
            return text;
        }

        String translated = response.getChoices()
                .get(0)
                .getMessage()
                .getContent()
                .trim();

        // 关键打印：原文 -> 译文
        System.out.println("[翻译] " + text + "  =>  " + translated);

        cache.put(key, translated);
        return translated;
    }
}
