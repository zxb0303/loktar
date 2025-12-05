package com.loktar.web.cxy;

import com.loktar.util.DateTimeUtil;
import lombok.Data;
import lombok.SneakyThrows;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.MessageFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ExchangeMain {
    private static final String URL = "https://chl.cn/huilv/?{0}-2025";
    private static final String EXCEL_PATH = "F:\\OneDrive\\Cxy\\汇率模版.xlsx";


    @SneakyThrows
    public static void main(String[] args) {

        try (FileInputStream fis = new FileInputStream(EXCEL_PATH);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);

            // 这里假设你已经拿到了第二行的 CellData 列表
            List<CellData> cellDataList = readSecondRow(EXCEL_PATH);

            for (CellData cellData : cellDataList) {
                if (cellData.getQueryVaule() == null) {
                    continue;
                }
                queryData(cellData, sheet);
            }
            String newFilePath = EXCEL_PATH.replace(".xlsx", DateTimeUtil.getDatetimeStr(LocalDateTime.now(),DateTimeUtil.FORMATTER_DATE)+".xlsx");
            // 写回文件（可写到新文件）
            try (FileOutputStream fos = new FileOutputStream(newFilePath)) {
                workbook.write(fos);
            }
        }
        //3.解析返回的html，获取汇率信息
        //4.写入excel文件，保存


    }

    @SneakyThrows
    private static void queryData(CellData cellData, Sheet sheet) {
        HttpClient httpClient = HttpClient.newBuilder().build();
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(MessageFormat.format(URL, cellData.getQueryVaule())))
                .timeout(Duration.ofSeconds(60))
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
                .GET()
                .build();
        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        String respBody = response.body();
        Document document = Jsoup.parse(respBody);
        Element leftBlock = document.selectFirst("div.open > div.L.t2g");
        Element l1 = leftBlock.select("div.L1").stream()
                .filter(e -> e.ownText().contains(" / 人民币"))
                .findFirst()
                .orElse(null);
        if (l1 == null) {
            return;
        }

        Elements monthDivs = l1.select("> div:has(a)");
        int startRowIndex = 2;  // 从第 3 行开始写

        int rowOffset = 0;
        for (Element div : monthDivs) {
            String str = div.text();
            // 例：2025年1月　7.1833（0.13921）
            String[] strs = str.split("　");
            if (strs.length < 2) {
                continue;
            }
            String month = strs[0];
            String exchanges = strs[1];

            String[] exs = exchanges.split("（");
            if (exs.length < 2) {
                continue;
            }
            String exchangeRateStr = exs[0].trim();                     // "7.1833"
            String inverseRateStr = exs[1].replace("）", "").trim();    // "0.13921"

            // 转成数字（必要时可加异常处理 / 判空）
            double exchangeRate = Double.parseDouble(exchangeRateStr);
            double inverseRate = Double.parseDouble(inverseRateStr);

            System.out.println(month + "," + exchangeRate + "," + inverseRate);

            int rowIndex = startRowIndex + rowOffset;
            Row row = sheet.getRow(rowIndex);
            if (row == null) {
                row = sheet.createRow(rowIndex);
            }

            int col = cellData.getColumnIndex();

            // 写入汇率（数字）
            Cell rateCell = row.getCell(col);
            if (rateCell == null) {
                rateCell = row.createCell(col);
            }
            rateCell.setCellValue(exchangeRate);

            // 写入逆汇率（数字）
//            Cell inverseCell = row.getCell(col + 1);
//            if (inverseCell == null) {
//                inverseCell = row.createCell(col + 1);
//            }
//            inverseCell.setCellValue(inverseRate);

            // 月份可以仍然用字符串
            Cell monthCell = row.getCell(0);
            if (monthCell == null) {
                monthCell = row.createCell(0);
            }
            monthCell.setCellValue(month);

            rowOffset++;
        }
    }

    @SneakyThrows
    public static List<CellData> readSecondRow(String filePath) {
        List<CellData> result = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            // 获取第一个 Sheet（下标从 0 开始）
            Sheet sheet = workbook.getSheetAt(0);

            // 第二行：行下标为 1
            Row row = sheet.getRow(1);
            if (row == null) {
                return result;
            }
            short firstCellNum = row.getFirstCellNum();
            short lastCellNum = row.getLastCellNum();

            for (int i = firstCellNum; i < lastCellNum; i++) {
                Cell cell = row.getCell(i);
                String value = getCellStringValue(cell);
                if (value.isBlank()) {
                    continue;
                }
                CellData cellData = new CellData();
                int start = value.indexOf('(');
                int end = value.indexOf(')');
                // 既有 ( 又有 ) 且顺序正确
                if (start >= 0 && end > start) {
                    String inner = value.substring(start + 1, end);
                    cellData.setQueryVaule(inner.toLowerCase());
                }
                cellData.setValue(value);
                cellData.setColumnIndex(i);
                result.add(cellData);
            }
        }

        return result;
    }

    @Data
    public static class CellData {
        private int columnIndex;   // 第几列（从 0 开始）
        private String value;      // 单元格内容
        private String queryVaule;

    }

    private static String getCellStringValue(Cell cell) {
        if (cell == null) {
            return "";
        }

        CellType cellType = cell.getCellType();
        if (cellType == CellType.FORMULA) {
            cellType = cell.getCachedFormulaResultType();
        }

        switch (cellType) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    // 如果需要转日期字符串，可根据需要格式化
                    return cell.getDateCellValue().toString();
                } else {
                    // 去掉多余小数点等，可根据需求定制
                    double val = cell.getNumericCellValue();
                    if (val == (long) val) {
                        return String.valueOf((long) val);
                    } else {
                        return String.valueOf(val);
                    }
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case BLANK:
                return "";
            default:
                return "";
        }
    }
}

