package com.loktar.util;

import com.loktar.domain.patent.PatentDetail;
import lombok.SneakyThrows;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class PatentUtil {

    private static final String ALGORITHM = "AES/ECB/PKCS5Padding";
    private static final String KEY = "ABCDEF0123456789";
    @SneakyThrows
    public static String encrypt(String data)  {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        SecretKeySpec secretKey = new SecretKeySpec(KEY.getBytes("UTF-8"), "AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedBytes = cipher.doFinal(data.getBytes("UTF-8"));
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    @SneakyThrows
    public static String decrypt(String encryptedData)  {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        SecretKeySpec secretKey = new SecretKeySpec(KEY.getBytes("UTF-8"), "AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decodedBytes = Base64.getDecoder().decode(encryptedData);
        byte[] decryptedBytes = cipher.doFinal(decodedBytes);
        return new String(decryptedBytes, "UTF-8");
    }

    public static PatentDetail dealPatentContent(String text) {
        PatentDetail patentDetail = new PatentDetail();
        //patentDetail.setPatentId( getDetail("申请号/专利号：", text));
        patentDetail.setName(getDetail("发明名称：", text));
        patentDetail.setApplyName(getDetail("申请人：", text));
        patentDetail.setType(getDetail("专利类型：", text));
        patentDetail.setApplyDate(getDetail("申请日：", text));
        patentDetail.setPubNoticeNum(getDetail("发明专利申请公布号：", text));
        patentDetail.setAuthNoticeNum(getDetail("授权公告号：", text));
        patentDetail.setLegalStatus(getDetail("法律状态：", text));
        patentDetail.setCaseStatus(getDetail("案件状态：", text));
        patentDetail.setAuthNoticeDate(getDetail("授权公告日：", text));
        patentDetail.setMainCategoryNum(getDetail("主分类号：", text));
        patentDetail.setStatus(0);
        return patentDetail;
    }

    private static String getDetail(String prefix, String text) {
        // Find the position of the next prefix to determine the end of the current field
        String[] prefixes = {"申请号/专利号：", "发明名称：", "申请人：", "专利类型：", "申请日：", "发明专利申请公布号：", "授权公告号：", "法律状态：", "案件状态：", "授权公告日：", "主分类号："};
        int start = text.indexOf(prefix);
        if (start == -1) {
            String errorMSg = "信息不完整或未找到";
            return errorMSg;
        }

        int end = text.length();
        for (String nextPrefix : prefixes) {
            if (!nextPrefix.equals(prefix)) {
                int nextIndex = text.indexOf(nextPrefix, start + prefix.length());
                if (nextIndex != -1 && nextIndex < end) {
                    end = nextIndex;
                }
            }
        }

        String detail = text.substring(start + prefix.length(), end).trim();
        return detail;
    }

    public static boolean isValidPatentNumber(String patentNumber) {
        if (patentNumber == null || !patentNumber.contains(".")) {
            return false;
        }

        String[] parts = patentNumber.split("\\.");
        if (parts.length != 2) {
            return false;
        }

        String numberPart = parts[0];
        String checkDigit = parts[1];

        if (numberPart.length() != 12) {
            return false;
        }

        String coefficients = "234567892345";
        int sum = 0;

        try {
            for (int i = 0; i < numberPart.length(); i++) {
                int digit = Character.digit(numberPart.charAt(i), 10);
                int coefficient = Character.digit(coefficients.charAt(i), 10);
                sum += digit * coefficient;
            }
        } catch (NumberFormatException e) {
            return false;
        }
        int remainder = sum % 11;
        String expectedCheckDigit = (remainder == 10) ? "X" : Integer.toString(remainder);
        return checkDigit.equals(expectedCheckDigit);
    }

    public static List<String> generatePatentNumbers(int year, int type, int startSequence, int count) {
        List<String> patentNumbers = new ArrayList<>();
        String coefficients = "234567892345";
        int sequence = startSequence;

        for (int j = 0; j < count; j++) {
            String numberPart = String.format("%d%d%07d", year, type, sequence++);
            int sum = 0;
            for (int i = 0; i < numberPart.length(); i++) {
                int digit = Character.digit(numberPart.charAt(i), 10);
                int coefficient = Character.digit(coefficients.charAt(i), 10);
                sum += digit * coefficient;
            }

            int remainder = sum % 11;
            String checkDigit = (remainder == 10) ? "X" : Integer.toString(remainder);
            patentNumbers.add(numberPart + "." + checkDigit);
        }
        return patentNumbers;
    }



}
