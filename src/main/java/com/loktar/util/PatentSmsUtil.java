package com.loktar.util;

import com.loktar.domain.qywx.QywxPatentMsg;
import com.loktar.dto.patent.PatentQuotationDTO;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class PatentSmsUtil {

    public static String getSmsMsgForCj(String mobiles, QywxPatentMsg qywxPatentMsg, File file) {
        List<PatentQuotationDTO> patentQuotationDTOs = readExcelFile(file);
        int amountCount = 0;
        int sellCount = 0;
        int price = 0;
        for (PatentQuotationDTO patentQuotationDTO : patentQuotationDTOs) {
            if (patentQuotationDTO.getAmount() != 0) {
                amountCount++;
            }
            if (StringUtils.isEmpty(patentQuotationDTO.getRemark())) {
                sellCount++;
                price = price + patentQuotationDTO.getPrice();
            }
        }
        String str = mobiles + "\n您【" + qywxPatentMsg.getApplyName() + "】的专利，有" + amountCount + "个欠费快失效了。如果不想继续持有，可以考虑出售。目前" + sellCount + "个可出售专利的报价共计是" + price + "元。考虑的话建议尽快，否则滞纳金每个月都会增加，直到欠费6个月失效。您回复后，我将和您联系！";
        return str;
    }

    public static String getSmsMsgForCc(QywxPatentMsg qywxPatentMsg, File file) {
        List<PatentQuotationDTO> patentQuotationDTOs = readExcelFile(file);
        int amountCount = 0;
        int sellCount = 0;
        int price = 0;
        for (PatentQuotationDTO patentQuotationDTO : patentQuotationDTOs) {
            if (patentQuotationDTO.getAmount() != 0) {
                amountCount++;
            }
            if (StringUtils.isEmpty(patentQuotationDTO.getRemark())) {
                sellCount++;
                price = price + patentQuotationDTO.getPrice();
            }
        }
        String str = "您好，我们是收购专利的代理公司，您【" + qywxPatentMsg.getApplyName() + "】有" + sellCount + "个专利，有" + amountCount + "个快要到期失效了，您看您要是不要的话考虑出售吗？我们公司每个正常状态的专利收购单价是" + qywxPatentMsg.getPrice() + "元。如果您考虑的话麻烦回复一下，我会跟您取得联系！谢谢";
        return str;
    }

    @SneakyThrows
    public static List<PatentQuotationDTO> readExcelFile(File file) {
        List<PatentQuotationDTO> patents = new ArrayList<>();
        FileInputStream fis = new FileInputStream(file);
        Workbook workbook = new XSSFWorkbook(fis);
        Sheet sheet = workbook.getSheetAt(0);
        int rowCount = sheet.getPhysicalNumberOfRows();
        for (int i = 1; i < rowCount - 1; i++) {
            Row row = sheet.getRow(i);
            if (row == null) {
                continue;
            }
            PatentQuotationDTO patentQuotationDTO = new PatentQuotationDTO();
            patentQuotationDTO.setPatentId(getCellValueAsString(row.getCell(0)));
            patentQuotationDTO.setName(getCellValueAsString(row.getCell(1)));
            patentQuotationDTO.setCaseStatus(getCellValueAsString(row.getCell(2)));
            patentQuotationDTO.setExpirationDate(getCellValueAsString(row.getCell(3)));
            patentQuotationDTO.setAmount((int) row.getCell(4).getNumericCellValue());
            patentQuotationDTO.setLateFeeAmount((int) row.getCell(5).getNumericCellValue());
            patentQuotationDTO.setPrice((int) row.getCell(6).getNumericCellValue());
            patentQuotationDTO.setValidDate(getCellValueAsString(row.getCell(7)));
            patentQuotationDTO.setRemark(getCellValueAsString(row.getCell(8)));
            patents.add(patentQuotationDTO);
        }
        return patents;
    }


    private static String getCellValueAsString(Cell cell) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        if (cell == null) {
            return "";
        }
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return dateFormat.format(cell.getDateCellValue());
                } else {
                    return String.valueOf((int) cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }
}
