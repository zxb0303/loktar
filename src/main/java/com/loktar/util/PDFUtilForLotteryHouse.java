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
//        int getTableCellsNum = -1;
        List<HZLotteryPeopleDTOV2> hZLotteryPeopleDTOV2s = new ArrayList<>();
        String fileFold = PDFBoxUtil.splitPDFFromUrl(pdfUrl, pdfPath);
        File file = new File(pdfPath + fileFold);
        File[] pdfFiles = file.listFiles();
        Arrays.sort(pdfFiles, Comparator.comparing(File::getName));
        int index = 1;
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

                for (int i = 0; i < strs.length; i++) {
//                    System.out.println(strs[i]);
                    if (isNumeric(strs[i]) && Integer.valueOf(strs[i]) == index) {
                        HZLotteryPeopleDTOV2 hZLotteryPeopleDTOV2 = new HZLotteryPeopleDTOV2();
                        hZLotteryPeopleDTOV2.setPeopleId(strs[i]);
                        hZLotteryPeopleDTOV2.setSerialNum(strs[i + 1]);
                        hZLotteryPeopleDTOV2.setName(strs[i + 2]);
                        hZLotteryPeopleDTOV2.setIdentityNum(strs[i + 3]);

//                        String identityNum = strs[i + 3];
//                        String last4 = identityNum.length() >= 4 ? identityNum.substring(identityNum.length() - 4) : identityNum;
//                        if (last4.contains("*")) {
//                            identityNum = identityNum + strs[i + 4];
//                            hZLotteryPeopleDTOV2.setIdentityNum(identityNum);
//                            i = i + 1;
//                        }
                        if (strs[i + 4].equals("X")) {
                            hZLotteryPeopleDTOV2.setIdentityNum(strs[i + 3] + strs[i + 4]);
                            i = i + 1;
                        }
                        if (!isNumeric(strs[i + 4])) {
                            hZLotteryPeopleDTOV2.setSerialNum(strs[i + 1] + strs[i + 2]);
                            i = i + 1;
                            hZLotteryPeopleDTOV2.setName(strs[i + 2]);
                            if (strs[i + 4].equals("X")) {
                                hZLotteryPeopleDTOV2.setIdentityNum(strs[i + 3] + strs[i + 4]);
                                i = i + 1;
                            }
                        }
                        hZLotteryPeopleDTOV2.setFamilyType(Integer.valueOf(strs[i + 4]));
                        hZLotteryPeopleDTOV2.setHasOtherPeople(0);
                        if (i + 5 < strs.length && !isNumeric(strs[i + 5])) {
                            if (!StringUtils.isEmpty(strs[i + 5]) && !"/".equals(strs[i + 5])) {
                                hZLotteryPeopleDTOV2.setHasOtherPeople(1);
                                hZLotteryPeopleDTOV2.setOtherBuyersName(strs[i + 5]);
                                if (i + 6 < strs.length) {
                                    hZLotteryPeopleDTOV2.setOtherBuyersIdnumber(strs[i + 6]);
                                } else {
                                    hZLotteryPeopleDTOV2.setOtherBuyersIdnumber("");
                                }
                            }
                        }
                        i = i + 4;
                        //System.out.println(hZLotteryPeopleDTOV2.toString());
                        hZLotteryPeopleDTOV2s.add(hZLotteryPeopleDTOV2);
                        index = index + 1;
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

    public static boolean isNumeric(String str) {
        if (str == null || str.isEmpty()) return false;
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
