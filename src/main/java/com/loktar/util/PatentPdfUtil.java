package com.loktar.util;

import com.loktar.domain.patent.PatentPdf;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PatentPdfUtil {
    private static Map<String, String> IPC_MAP = new HashMap<>();
    private static Map<String, String> PATENT_MAP = new HashMap<>();
    private static Map<String, String> APPLY_MAP = new HashMap<>();
    private static String date = null;

    @SneakyThrows
    public static void main(String[] args) {
        String filePath = "F:/loktar/patent/2022/实用新型专利公报（2022.07.12）.pdf";
        String type = "实用新型";
        List<PatentPdf> patentPdfs = dealPatentPdf(filePath, type);
    }

    public static List<PatentPdf> dealPatentPdf(String filePath, String type) {
        intData(filePath);
        List<PatentPdf> patentPdfs = new ArrayList<>();
        PATENT_MAP.forEach((k, v) -> {
            PatentPdf patentPdf = new PatentPdf();
            patentPdf.setPatentId(v);
            patentPdf.setAuthNoticeNum(k);
            patentPdf.setApplyName(APPLY_MAP.get(k));
            patentPdf.setStatus(0);
            patentPdf.setType(type);
            patentPdf.setAuthNoticeDate(date);
            patentPdf.setMainCategoryNum(IPC_MAP.get(k));
            patentPdfs.add(patentPdf);
        });
        System.out.println(PATENT_MAP.size());
        System.out.println(APPLY_MAP.size());
        System.out.println(IPC_MAP.size());
        return patentPdfs;
    }

    @SneakyThrows
    public static void intData(String filePath) {
        IPC_MAP = new HashMap<>();
        PATENT_MAP = new HashMap<>();
        APPLY_MAP = new HashMap<>();
        date = null;
        File pdf = new File(filePath);
        PDDocument document = Loader.loadPDF(pdf);
        PDFTextStripper pdfStripper = new PDFTextStripper();
        int numPages = document.getNumberOfPages();
        boolean end = false;
        for (int i = 1; i <= numPages; i++) {
            if (end) {
                break;
            }
            pdfStripper.setStartPage(i);
            pdfStripper.setEndPage(i);
            String text = pdfStripper.getText(document);
            String[] lines = text.split("\r\n");
            boolean startIPC = false;
            boolean startPatent = false;
            boolean startApply = false;
            System.out.println("i:" + i);
            for (int j = 0; j < lines.length; j++) {
                String line = lines[j];
                if (StringUtils.isEmpty(date) && line.indexOf("授权公告日") != -1) {
                    date = getDate(line);
                }
//                System.out.println("j:"+j);
                if (line.matches("·\\d+·")) {
                    continue;
                }
                if (line.equals("1.IPC索引") || line.equals("2.专利号索引") || line.equals("3.专利权人索引")) {
                    continue;
                }
                if (line.equals("4.授权公告号/专利号对照表索引")) {
                    end = true;
                    break;
                }
                if (line.equals("IPC 授权公告号")) {
                    startIPC = true;
                    continue;
                }
                if (line.equals("专利号 授权公告号")) {
                    startPatent = true;
                    startIPC = false;
                    continue;
                }
                if (line.equals("专利权人 授权公告号")) {
                    startApply = true;
                    startPatent = false;
                    startIPC = false;
                    continue;
                }
                if (startIPC) {
                    dealIPC(line);
                }
                if (startPatent) {
                    dealPatent(line);
                }
                if (startApply) {
                    j = dealApply(line, lines, j);
                }
            }
        }
    }

    private static String getDate(String line) {
        String regex = "授权公告日.*?(\\d{4})\\s*年\\s*(\\d{1,2})\\s*月\\s*(\\d{1,2})\\s*日";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(line);
        if (matcher.find()) {
            String year = matcher.group(1);
            String month = matcher.group(2);
            String day = matcher.group(3);
            String formattedDate = String.format("%s-%02d-%02d", year, Integer.parseInt(month), Integer.parseInt(day));
            return formattedDate;
        }
        return null;
    }


    private static int dealApply(String line, String[] lines, int j) {
        String newLine = line.replace(" ", "");
        //TODO 打印
        //System.out.println(newLine);
        if (newLine.equals("专利权人授权公告号专利权人授权公告号")) {
            return j;
        }
        while (!newLine.endsWith("U")) {
            newLine = newLine + lines[j + 1].replace(" ", "");
            j = j + 1;
        }

        String[] strs = newLine.split("CN");
        String key = "CN" + strs[1];
        String value = strs[0];
        if (APPLY_MAP.containsKey(key)) {
            value = value + "," + APPLY_MAP.get(key);
        }
        APPLY_MAP.put(key, value);
        return j;
    }


    private static void dealPatent(String line) {
        //TODO 打印
        //System.out.println(line);
        String[] strs = line.replaceAll(" ", "").split("CN");
        if (strs.length <= 1) {
            return;
        }
        String key = "CN" + strs[1];
        String value = strs[0].replace("ZL", "");
        PATENT_MAP.put(key, value);
    }

    private static void dealIPC(String line) {
        String[] strs = line.replaceAll("\\s+", " ").split(" ");
        String key = strs[3].trim() + strs[4].trim() + strs[5].trim();
        String value = strs[0].trim() + strs[1].trim();
        if (IPC_MAP.containsKey(key)) {
            value = value + "," + IPC_MAP.get(key);
        }
        IPC_MAP.put(key, value);
    }

}

