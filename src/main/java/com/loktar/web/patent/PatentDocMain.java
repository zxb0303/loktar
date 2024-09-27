package com.loktar.web.patent;


import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import lombok.SneakyThrows;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;


public class PatentDocMain {


    public static LinkedHashMap<String, String[]> reasonMap = new LinkedHashMap<>();
    public static Map<String,String> statusMap = new HashMap<>();
    public static String INSERT_SQL = "INSERT INTO `patent_detail_yitong`(`patent_id`, `type`, `status`, `doc_name`) VALUES ('%s', '%s', '%s', '%s');";

    static {
        reasonMap.put("等待答复", new String[]{"答复期内", "答复期限内", "文本尚存在缺陷", "目前的文本不能被授权"});
        reasonMap.put("不具备授权前景，不能被授予专利权", new String[]{"不具备授权前景", "不具备被授予专利权的前景", "不能被授予专利权"});
        reasonMap.put("没有实质性内容，将被驳回", new String[]{"没有可授予专利权的实质性内容", "没有可以被授予专利权的实质性内容", "本申请将被驳回","不具备创造性"});

        statusMap.put("等待答复","00");
        statusMap.put("不具备授权前景，不能被授予专利权","01");
        statusMap.put("没有实质性内容，将被驳回","02");
    }


    public static void main(String[] args) {
        String pdfFolderPath = "F:/doc/";
        File pdfFolder = new File(pdfFolderPath);
        File[] pdfFiles = pdfFolder.listFiles((dir, name) -> name.toLowerCase().endsWith(".pdf"));
        for (File pdfFile : pdfFiles) {
            readPdf(pdfFile);
        }
    }

    @SneakyThrows
    private static void readPdf(File pdfFile) {
        StringBuilder extractedText = new StringBuilder();
        String fileName = pdfFile.getName();
        String patentId = "";
        String docMsg = "";
        PdfReader reader = new PdfReader(pdfFile.getPath());
        int numberOfPages = reader.getNumberOfPages();
        for (int i = 1; i <= numberOfPages; i++) {
            String pageContent = PdfTextExtractor.getTextFromPage(reader, i);
            String[] lines = pageContent.split("\n");
            for (String line : lines) {
                if (line.indexOf("申请号:") != -1) {
                    patentId = line.replace("申请号:", "").trim();
                }
                if (i >= 3) {
                    extractedText.append(line);
                }
            }
        }
        reader.close();
        //判断line的内容是否包含在reasonMap的value的数组中，是的话 则输出map的key
        for (Map.Entry<String, String[]> entry : reasonMap.entrySet()) {
            String key = entry.getKey();
            String[] values = entry.getValue();
            for (String value : values) {
                if (extractedText.toString().contains(value)) {
                    docMsg = key;
                    break;
                }
            }
        }
//        System.out.println(fileName + ";" + patentId + ";" + docMsg);
        String sql = String.format(INSERT_SQL, patentId, statusMap.get(docMsg), "0", fileName);
        System.out.println(sql);
    }

    @SneakyThrows
    private static void readPdf2(File pdfFile) {
        boolean start = false;
        PdfReader reader = new PdfReader(pdfFile.getPath());
        int numberOfPages = reader.getNumberOfPages();
        StringBuilder extractedText = new StringBuilder();
        for (int i = 1; i <= numberOfPages; i++) {
            String pageContent = PdfTextExtractor.getTextFromPage(reader, i);
            String[] lines = pageContent.split("\n");
            for (String line : lines) {
                if (line.indexOf("申请号:") != -1) {
                    System.out.println(line.replace("申请号:", "").trim());
                }
//                if (line.contains("基于上述理由")|| line.contains("综上")||line.contains("基于以上理由")
//                        ||line.contains("申请人应当在本通知书指定的答复期限内对本通知书提出的问题逐一进行答复")
//                        ||line.contains("申请人应在本通知书指定的答复期限内")
//                        ||line.contains("本申请不具备被授予专利权的前景")
//                        ||line.contains("本发明不能被授予专利权")
//                        ||line.contains("本申请将被驳回")) {
//                    start = true;
//                }
                if (line.contains("本申请不具备授权前景") || line.contains("本申请不具备被授予专利权的前景")
                        || line.contains("本申请将被驳回") || line.contains("本申请将被驳回")
                        || line.contains("申请人应当在本通知书指定的答复期限内对本通知书提出的问题逐一进行答复") || line.contains("申请人应在本通知书指定的答复期限内") || line.contains("本申请按照目前的文本尚存在缺陷")
                        || line.contains("不能被授予专利权")) {
                    start = true;
                }


                if (start) {
                    if (line.trim().matches("^\\d{6}.*")) {
                        start = false;
                        break;
                    }
                    extractedText.append(line);
                }
            }
        }
        reader.close();
        System.out.println(extractedText.toString().trim());
    }


    private static void itextpdf1() {
        String filePath = "F:/doc/00d619634759c1b7ad2830c57350a314.pdf";
        boolean start = false;
        try {
            PdfReader reader = new PdfReader(filePath);
            int numberOfPages = reader.getNumberOfPages();
            for (int i = 1; i <= numberOfPages; i++) {
                String pageContent = PdfTextExtractor.getTextFromPage(reader, i);
                String[] lines = pageContent.split("\n");
                for (String line : lines) {
                    System.out.println(line);
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void itextpdf2() {
        String filePath = "F:/doc/00d619634759c1b7ad2830c57350a314.pdf";
        boolean start = false;
        try {
            PdfReader reader = new PdfReader(filePath);
            int numberOfPages = reader.getNumberOfPages();
            StringBuilder extractedText = new StringBuilder();
            for (int i = 1; i <= numberOfPages; i++) {
                String pageContent = PdfTextExtractor.getTextFromPage(reader, i);
                String[] lines = pageContent.split("\n");
                for (String line : lines) {
                    if (line.indexOf("申请号:") != -1) {
                        System.out.println(line.replace("申请号:", "").trim());
                    }
                    if (line.contains("基于上述理由")) {
                        start = true;
                    }
                    if (start) {
                        if (line.trim().matches("^\\d{6}.*")) {
                            start = false;
                            break;
                        }
                        extractedText.append(line);
                    }
                }
            }
            reader.close();
            System.out.println(extractedText.toString().trim());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}

