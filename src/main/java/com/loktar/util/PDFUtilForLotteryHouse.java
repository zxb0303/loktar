package com.loktar.util;

import com.loktar.dto.lottery.HZLotteryPeopleDTOV2;
import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.PDFTextStripperByArea;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class PDFUtilForLotteryHouse {

    public static List<HZLotteryPeopleDTOV2> getTableContentFromPDFUrl(String pdfUrl, String pdfPath) {
        int getTableCellsNum = -1;
        List<HZLotteryPeopleDTOV2> hZLotteryPeopleDTOV2s = new ArrayList<>();
        String fileFold = PDFBoxUtil.splitPDFFromUrl(pdfUrl, pdfPath);
        File file = new File(pdfPath + fileFold);
        File[] pdfFiles = file.listFiles();
        Arrays.sort(pdfFiles, Comparator.comparing(File::getName));

        for (File pdfFile : pdfFiles) {
            try (PDDocument document = Loader.loadPDF(pdfFile)) {
                PDFTextStripperByArea stripper = new PDFTextStripperByArea();
                stripper.setSortByPosition(true);

                PDFTextStripper tStripper = new PDFTextStripper();
                String pdfFileInText = tStripper.getText(document);

                String[] lines = pdfFileInText.split("\\r?\\n");
                StringBuilder builder = new StringBuilder();

                for (String line : lines) {
                    String[] cells = line.split("\\s+");
                    for (String cell : cells) {
                        builder.append(cell.replace(";", ",")).append(";");
                    }
                }

                String result = builder.toString().replaceAll("\n", "").replaceAll("\r", "");
                String[] strs = result.split(";");

                if (getTableCellsNum == -1) {
                    getTableCellsNum = 7; // 如果需要动态获取列数，可以实现getTableCells方法
                    System.out.println("pdf表格列数num:" + getTableCellsNum);
                    if (getTableCellsNum == 0) {
                        System.out.println("需要手动处理");
                        return hZLotteryPeopleDTOV2s;
                    }
                }

                for (int i = 0; i < strs.length; i++) {
                    if (i % getTableCellsNum == 0 && StringUtils.isNumeric(strs[i])) {
                        HZLotteryPeopleDTOV2 hZLotteryPeopleDTOV2 = new HZLotteryPeopleDTOV2();
                        hZLotteryPeopleDTOV2.setPeopleId(strs[i]);
                        hZLotteryPeopleDTOV2.setSerialNum(strs[i + 1]);
                        hZLotteryPeopleDTOV2.setName(strs[i + 2]);
                        hZLotteryPeopleDTOV2.setIdentityNum(strs[i + 3]);
                        hZLotteryPeopleDTOV2.setFamilyType(Integer.valueOf(strs[i + 4]));
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
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return hZLotteryPeopleDTOV2s;
    }

    public static Map<String, Integer> getTextContentFromPDFUrl(String pdfUrl, String pdfPath) {
        Map<String, Integer> map = new HashMap<>();
        String fileFold = PDFBoxUtil.splitPDFFromUrl(pdfUrl, pdfPath);
        File file = new File(pdfPath + fileFold);
        File[] pdfFiles = file.listFiles();
        Arrays.sort(pdfFiles, Comparator.comparing(File::getName));

        for (File pdfFile : pdfFiles) {
            try (PDDocument document = Loader.loadPDF(pdfFile)) {
                PDFTextStripper tStripper = new PDFTextStripper();
                String pdfFileInText = tStripper.getText(document);

                String[] lines = pdfFileInText.split("\\r?\\n");
                for (String line : lines) {
                    String[] parts = line.replace("\r\n", ";").replace(" ", ",").split(";");
                    for (String part : parts) {
                        String[] ss = part.split(",");
                        if (ss.length > 1 && StringUtils.isNumeric(ss[0])) {
                            map.put(ss[1], Integer.valueOf(ss[0]));
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return map;
    }
}
