package com.loktar.web.patent;


import lombok.SneakyThrows;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;


public class PatentDocMain {

    public static LinkedHashMap<String, String[]> reasonMap = new LinkedHashMap<>();
    public static Map<String, String> statusMap = new HashMap<>();
    public static String INSERT_OR_UPDATE_SQL = "INSERT INTO `patent_detail_yitong`(`patent_id`, `type`, `status`, `doc_name`) VALUES ('%s', '%s', '%s', '%s') ON DUPLICATE KEY UPDATE `type`=VALUES(`type`), `status`=VALUES(`status`), `doc_name`=VALUES(`doc_name`);";

    static {
        reasonMap.put("等待答复", new String[]{"答复期内", "答复期限内", "文本尚存在缺陷", "目前的文本不能被授权"});
        reasonMap.put("不具备授权前景，不能被授予专利权", new String[]{"不具备授权前景", "不具有授权前景", "不具备授予专利权的前景", "不具备被授予专利权的前景", "不能被授予专利权"});
        reasonMap.put("没有实质性内容，将被驳回", new String[]{"没有可授予专利权的实质性内容", "没有可以被授予专利权的实质性内容", "本申请将被驳回", "不具备创造性"});

        statusMap.put("等待答复", "00");
        statusMap.put("不具备授权前景，不能被授予专利权", "01");
        statusMap.put("没有实质性内容，将被驳回", "02");
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
        try (PDDocument document = Loader.loadPDF(pdfFile)) {
            int numberOfPages = document.getNumberOfPages();
            for (int i = 1; i <= numberOfPages; i++) {
                PDFTextStripper stripper = new PDFTextStripper();
                stripper.setStartPage(i);
                stripper.setEndPage(i);
                String pageContent = stripper.getText(document);
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
        }
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
        String sql = String.format(INSERT_OR_UPDATE_SQL, patentId, statusMap.get(docMsg), "0", fileName);
        System.out.println(sql);
    }
}
