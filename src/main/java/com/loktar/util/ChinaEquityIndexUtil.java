package com.loktar.util;

import com.loktar.domain.investment.EquityIndexDividendYieldDaily;
import lombok.SneakyThrows;
import org.apache.poi.ss.usermodel.*;

import java.io.InputStream;
import java.net.URI;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class ChinaEquityIndexUtil {
    public final static List<String> EQUITY_INDEXS = List.of("930955", "H30269", "931446", "932422");//, "930914", "931233"
    public final static String INDICATOR_URL = "https://oss-ch.csindex.com.cn/static/html/csindex/public/uploads/file/autofile/indicator/{0}indicator.xls";

    @SneakyThrows
    public static List<EquityIndexDividendYieldDaily> readExcelFromUrl(String fileUrl) {
        List<EquityIndexDividendYieldDaily> result = new ArrayList<>();
        URI uri = URI.create(fileUrl);
        URLConnection connection = uri.toURL().openConnection();
        connection.setConnectTimeout(10000);
        connection.setReadTimeout(10000);
        try (InputStream inputStream = connection.getInputStream();
             Workbook workbook = WorkbookFactory.create(inputStream)) {

            DataFormatter formatter = new DataFormatter();
            Sheet sheet = workbook.getSheetAt(0);
            for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                if (row == null) {
                    continue;
                }
                String date = formatter.formatCellValue(
                        row.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                );
                String equityIndex = formatter.formatCellValue(
                        row.getCell(1, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                );
                String equityIndexName = formatter.formatCellValue(
                        row.getCell(3, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                );
                String dividendYield = formatter.formatCellValue(
                        row.getCell(9, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                );
                if (equityIndex.isBlank() && date.isBlank() && dividendYield.isBlank()) {
                    continue;
                }
                EquityIndexDividendYieldDaily entity = new EquityIndexDividendYieldDaily();
                entity.setEquityIndex(equityIndex);
                entity.setEquityIndexName(equityIndexName);
                entity.setDate(date);
                entity.setDividendYield(Float.parseFloat(dividendYield));
                result.add(entity);
            }
        }
        return result;
    }
}
