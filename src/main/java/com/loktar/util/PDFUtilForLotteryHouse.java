package com.loktar.util;


import com.loktar.dto.lottery.HZLotteryPeopleDTOV2;
import com.spire.pdf.PdfDocument;
import com.spire.pdf.PdfPageBase;
import com.spire.pdf.utilities.PdfTable;
import com.spire.pdf.utilities.PdfTableExtractor;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.*;

public class PDFUtilForLotteryHouse {
    public static List<HZLotteryPeopleDTOV2> getTableContentFromPDFUrl(String pdfUrl, String pdfPath) {
        int getTableCellsNum = -1;
        List<HZLotteryPeopleDTOV2> hZLotteryPeopleDTOV2s = new ArrayList<>();
        String fileFold = PDFBoxUtil.splitPDFFromUrl(pdfUrl, pdfPath);
        File file = new File(pdfPath + fileFold);
        File[] pdfFiles = file.listFiles();
        Arrays.sort(pdfFiles, Comparator.comparing(File::getName));
        for (int k = 0; k < pdfFiles.length; k++) {
            File pdfFile = pdfFiles[k];
            //System.out.println(pdfFile.getName());
            try {
                //TODO 试试能不能替换成pdfbox
                PdfDocument pdf = new PdfDocument();
                pdf.loadFromFile(pdfFile.getPath());
                StringBuilder builder = new StringBuilder();
                PdfTableExtractor extractor = new PdfTableExtractor(pdf);
                for (int page = 0; page < pdf.getPages().getCount(); page++) {
                    PdfTable[] tableLists = extractor.extractTable(page);
                    if (tableLists != null) {
                        for (PdfTable table : tableLists) {
                            int row = table.getRowCount();
                            int column = table.getColumnCount();
                            for (int i = 0; i < row; i++) {
                                for (int j = 0; j < column; j++) {
                                    String text = table.getText(i, j);
                                    text = text.replace(";", ",");
                                    builder.append(text).append(";");
                                }
                            }
                        }
                    }
                }
                String result = builder.toString();
                result = result.replaceAll("\n", "").replaceAll("\r", "");
                String[] strs = result.split(";");
                //System.out.println(result);
                if (k == 0) {
                    getTableCellsNum = 7;
//                    getTableCellsNum = getTableCells(strs);
                    System.out.println("pdf表格列数num:" + getTableCellsNum);
                    if (getTableCellsNum == 0) {
                        System.out.println("需要手动处理");
                        return hZLotteryPeopleDTOV2s;
                    }
                }
                for (int i = 0; i < strs.length; i++) {
                    //System.out.print(strs[i] + ";");
                    if (i % getTableCellsNum == 0 && StringUtils.isNumeric(strs[i])) {
                        //System.out.println("");
                        HZLotteryPeopleDTOV2 hZLotteryPeopleDTOV2 = new HZLotteryPeopleDTOV2();
                        hZLotteryPeopleDTOV2.setPeopleId(strs[i]);
                        hZLotteryPeopleDTOV2.setSerialNum(strs[i + 1]);
                        hZLotteryPeopleDTOV2.setName(strs[i + 2]);
                        hZLotteryPeopleDTOV2.setIdentityNum(strs[i + 3]);
                        hZLotteryPeopleDTOV2.setFamilyType(Integer.valueOf(strs[i + 4]));
                        //hZLotteryPeopleDTOV2.setFamilyType(strs[i+3].equals("是")?1:0);
                        hZLotteryPeopleDTOV2.setHasOtherPeople(0);
                        if (i + 5 < strs.length && !StringUtils.isEmpty(strs[i + 5]) && !"/".equals(strs[i + 5])) {
                            hZLotteryPeopleDTOV2.setHasOtherPeople(1);
                            hZLotteryPeopleDTOV2.setOtherBuyersName(strs[i + 5]);
                            if (getTableCellsNum > 6) {
                                hZLotteryPeopleDTOV2.setOtherBuyersIdnumber(strs[i + 6]);
                            } else {
                                hZLotteryPeopleDTOV2.setOtherBuyersIdnumber("");
                            }

                        }
                        hZLotteryPeopleDTOV2s.add(hZLotteryPeopleDTOV2);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return hZLotteryPeopleDTOV2s;
    }

    private static int getTableCells(String[] strs) {
        for (int i = 0; i < strs.length; i++) {
            if (StringUtils.isNumeric(strs[i])) {
                return i;
            }
        }
        return -1;
    }


    public static Map<String, Integer> getTextContentFromPDFUrl(String pdfUrl, String pdfPath) {
        Map<String, Integer> map = new HashMap<>();
        String fileFold = PDFBoxUtil.splitPDFFromUrl(pdfUrl, pdfPath);
        File file = new File(pdfPath + fileFold);
        File[] pdfFiles = file.listFiles();
        Arrays.sort(pdfFiles, Comparator.comparing(File::getName));
        for (File pdfFile : pdfFiles) {
            try {
                PdfDocument pdf = new PdfDocument();
                pdf.loadFromFile(pdfFile.getPath());
                StringBuilder builder = new StringBuilder();
                for (int i = 0; i < pdf.getPages().getCount(); i++) {
                    PdfPageBase page = pdf.getPages().get(i);
                    String text = page.extractText(false);
                    builder.append(text);
                }
                String result = builder.toString();
                result = result.replace("\r\n", ";");
                result = result.replace(" ", ",");
                String[] strs = result.split(";");
                for (String str : strs) {
                    if (str.contains(",")) {
                        String[] ss = str.split(",");
                        if (StringUtils.isNumeric(ss[0])) {
                            map.put(ss[1], Integer.valueOf(ss[0]));
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return map;
    }
}
