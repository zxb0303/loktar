package com.loktar.web.patent;

import com.loktar.dto.patent.PatentCertDTO;
import com.loktar.util.PDFPdfCertUtil1;
import lombok.SneakyThrows;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PatentCertMain {
    public static String NEED_CREATE_CERT_EXCEL_PATH = "F:/OneDrive/Patent/cert/cert.xlsx";

    //public static String DETAIL_URL = "http://epub.cnipa.gov.cn/cred/{0}";

    @SneakyThrows
    public static void main(String[] args) {
        //1.读取excel 获取数据
        List<PatentCertDTO> patentCertDTOs = readExcel(NEED_CREATE_CERT_EXCEL_PATH);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
        for (PatentCertDTO patentCertDTO : patentCertDTOs){
            System.out.println(patentCertDTO.getAuthNoticeDate());

            Date authNoticeDate = sdf.parse(patentCertDTO.getAuthNoticeDate());

            if(authNoticeDate.before(sdf.parse("2022.06.15"))){
                //2018-11-20~2022-06-14 pdf图片+印花税标志
                System.out.println("template1");
                PDFPdfCertUtil1.generatePDF(patentCertDTO);
                continue;
            }
            if(authNoticeDate.after(sdf.parse("2022.06.16"))&&authNoticeDate.before(sdf.parse("2023.01.31"))){
                //2022-06-17~2023-01-31 pdf图片
                System.out.println("template2");
            }
            if(authNoticeDate.after(sdf.parse("2023.04.07"))&&authNoticeDate.before(sdf.parse("2024.05.31"))){
                System.out.println("template3");
                //2022-06-17~2024-05-31 pdf文字版
                //PDFPdfCertUtil.generatePDF(patentCertDTO);
            }
            if(authNoticeDate.after(sdf.parse("2024.06.01"))){
                System.out.println("template4");
                //2022-06-17~2024-05-31 pdf文字版
            }
        }
    }

    @SneakyThrows
    public static List<PatentCertDTO> readExcel(String filePath) {
        List<PatentCertDTO> patentCertDTOs = new ArrayList<>();
        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheetAt(0);
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row != null) {
                    PatentCertDTO patentCertDTO = new PatentCertDTO();
                    patentCertDTO.setPatentId(getCellValueAsString(row.getCell(0)));
                    patentCertDTO.setCertId(getCellValueAsString(row.getCell(1)));
                    patentCertDTO.setName(getCellValueAsString(row.getCell(2)));
                    patentCertDTO.setAuthNoticeNum(getCellValueAsString(row.getCell(3)));
                    patentCertDTO.setAuthNoticeDate(getCellValueAsString(row.getCell(4)));
                    patentCertDTO.setApplyDate(getCellValueAsString(row.getCell(5)));
                    patentCertDTO.setApplyName(getCellValueAsString(row.getCell(6)));
                    patentCertDTO.setInventorName(getCellValueAsString(row.getCell(7)));
                    patentCertDTO.setAddress(getCellValueAsString(row.getCell(8)));
                    patentCertDTOs.add(patentCertDTO);
                }
            }
        }
        return patentCertDTOs;
    }

    private static String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    return String.valueOf((long) cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }

//    @SneakyThrows
//    private static List<PatentCertDTO> getDetailData(List<PatentCertDTO> patentCertDTOs) {
//        for (PatentCertDTO patentCertDTO : patentCertDTOs) {
//            HttpClient httpClient = HttpClient.newHttpClient();
//            URI uri = URI.create(MessageFormat.format(DETAIL_URL, patentCertDTO.getAuthNoticeNum()));
//            HttpRequest httpRequest = HttpRequest.newBuilder()
//                    .uri(uri)
//                    .timeout(Duration.ofSeconds(60))
//                    .header(LokTarConstant.HTTP_HEADER_USER_AGENT_NAME, LokTarConstant.HTTP_HEADER_USER_AGENT_VALUE)
//                    .header(LokTarConstant.HTTP_HEADER_ACCEPT_NAME, LokTarConstant.HTTP_HEADER_ACCEPT_VALUE_HTML)
//                    .header(LokTarConstant.HTTP_HEADER_ACCEPT_LANGUAGE_NAME, LokTarConstant.HTTP_HEADER_ACCEPT_LANGUAGE_VALUE_CN)
//                    .header(LokTarConstant.HTTP_HEADER_COOKIE_NAME, COOKIE)
//                    .header(LokTarConstant.HTTP_HEADER_ACCEPT_ENCODING_NAME, LokTarConstant.HTTP_HEADER_ACCEPT_ENCODING_VALUE_GZIP)
//                    .GET()
//                    .build();
//            HttpResponse<byte[]> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofByteArray());
//
//
//            byte[] responseBody = response.body();
//            String contentEncoding = response.headers().firstValue("Content-Encoding").orElse("");
//            if ("gzip".equalsIgnoreCase(contentEncoding)) {
//                try (GZIPInputStream gzipInputStream = new GZIPInputStream(new ByteArrayInputStream(responseBody))) {
//                    responseBody = gzipInputStream.readAllBytes();
//                }
//            }
//            // 将解压缩后的数据转换为字符串
//            String responseString = new String(responseBody);
//            Document document = Jsoup.parse(responseString);
//            Element infoDiv = document.selectFirst("[class=info]");
//            List<Element> dds = infoDiv.select("dd");
//            for (Element dd : dds) {
//                String text = dd.text();
//                System.out.println(text);
//            }
//
//        }
//        return patentCertDTOs;
//    }
}
