package com.loktar.web.cxy;

import lombok.SneakyThrows;
import org.apache.poi.openxml4j.util.ZipSecureFile;
import org.apache.poi.ss.usermodel.*;

import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

public class ExcelMain {

    @SneakyThrows
    public static void main(String[] args) {
        Stream<Path> paths = Files.walk(Paths.get("F:/excel/"));
        List<String> fileList = paths
                .filter(Files::isRegularFile)
                .map(Path::toString)
                .toList();
        for (String filepath : fileList) {
            ZipSecureFile.setMinInflateRatio(-1.0d);
            InputStream oldInp = new FileInputStream(filepath);
            Workbook oldWb = WorkbookFactory.create(oldInp);
            Sheet oldSheet = oldWb.getSheetAt(0);
            Row oldRow = oldSheet.getRow(1);
            Cell oldCell = oldRow.getCell(5);
            System.out.println(filepath+","+oldCell.getNumericCellValue());
        }


    }
}
