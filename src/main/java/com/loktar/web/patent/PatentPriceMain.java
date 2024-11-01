package com.loktar.web.patent;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
//处理费减数据
public class PatentPriceMain {

    public static void main(String[] args) {
        String directoryPath = "F:\\OneDrive\\Patent\\quotation"; // 替换为你的文件夹路径
        File folder = new File(directoryPath);
        File[] listOfFiles = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".xlsx"));

        if (listOfFiles != null) {
            for (File file : listOfFiles) {
                if (file.isFile()) {
                    try (FileInputStream fis = new FileInputStream(file);
                         Workbook workbook = new XSSFWorkbook(fis)) {

                        Sheet sheet = workbook.getSheetAt(0); // 获取第一个工作表
                        int count = 0;
                        boolean found = false;

                        for (int rowIndex = 0; rowIndex < Math.min(10, sheet.getPhysicalNumberOfRows()); rowIndex++) {
                            Row row = sheet.getRow(rowIndex);
                            if (row != null) {
                                for (int colIndex = 0; colIndex < Math.min(15, row.getPhysicalNumberOfCells()); colIndex++) {
                                    Cell cell = row.getCell(colIndex);
                                    if (cell != null && cell.getCellType() == CellType.STRING) {
                                        String cellValue = cell.getStringCellValue();
                                        if (cellValue.contains("费减")) {
                                            count++;
                                            if (count >= 3) {
                                                System.out.println(file.getName().replace(".xlsx",""));
                                                found = true;
                                                break;
                                            }
                                        }
                                    }
                                }
                            }
                            if (found) break;
                        }

                    } catch (IOException e) {
                        System.err.println("Error reading file: " + file.getName());
                        e.printStackTrace();
                    }
                }
            }
        } else {
            System.out.println("No files found in the directory.");
        }
    }
}
