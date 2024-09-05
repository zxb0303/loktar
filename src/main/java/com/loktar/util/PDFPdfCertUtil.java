package com.loktar.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.*;
import com.loktar.dto.patent.PatentCertDTO;
import lombok.SneakyThrows;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PDFPdfCertUtil {
    private static String TEMPLATE_PDF_FILE_PATH = "F:/OneDrive/Patent/cert/template/template.pdf";
    private static String OUTPUT_FOLD_PATH = "F:/OneDrive/Patent/cert/output/";
    private static String FONT_SONG_PATH = "F:/OneDrive/Patent/cert/template/SimSun.ttc";
    private static String FONT_FANGZHEN_PATH = "F:/OneDrive/Patent/cert/template/SURSONG.ttc";
    private static String FONT_KAI_PATH = "F:/OneDrive/Patent/cert/template/simkai.ttc";
    private static String CERD_URL = "http://epub.cnipa.gov.cn/cred/{0}";

    @SneakyThrows
    public static void generatePDF(PatentCertDTO patentCertDTO) {
        PdfReader reader = new PdfReader(TEMPLATE_PDF_FILE_PATH);
        String outputFileName = OUTPUT_FOLD_PATH + patentCertDTO.getApplyName()+"-"+patentCertDTO.getPatentId() + ".pdf";
        PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(outputFileName));
        BaseFont songtiFont = BaseFont.createFont(FONT_SONG_PATH + ",0", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        BaseFont fangzhenFont = BaseFont.createFont(FONT_FANGZHEN_PATH + ",0", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        BaseFont kaitiFont = BaseFont.createFont(FONT_KAI_PATH + ",0", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        //第一页
        PdfContentByte canvas1 = stamper.getOverContent(1);
        //添加证书号
        writeText(canvas1, songtiFont, 129.8f, 725.5f, 11.495f, patentCertDTO.getCertId(), 0, 1f);
        //添加实用新型名称
        writeText(canvas1, fangzhenFont, 163.9f, 590.2f, 12f, patentCertDTO.getName(), 0, 1f);
        //添加发明人
        writeText(canvas1, fangzhenFont, 163.9f, 554.8f, 12f, patentCertDTO.getInventorName(), 0, 1f);
        //专 利 号
        writeText(canvas1, fangzhenFont, 163.9f, 518.8f, 12f, formatPatentId(patentCertDTO.getPatentId()), 0, 1f);
        //专 利 申 请 日
        writeText(canvas1, fangzhenFont, 163.9f, 483.2f, 12f, formatChineseDate(patentCertDTO.getApplyDate()), 0, 1f);
        //专 利 权 人
        writeText(canvas1, fangzhenFont, 163.9f, 448.6f, 12f, patentCertDTO.getApplyName(), 0, 1f);

        String str = patentCertDTO.getAddress();
        String[] addressStrings = processString(str, 30);
        if (addressStrings.length > 2) {
            throw new RuntimeException("地址过长");
        }
        //地 址1
        writeText(canvas1, fangzhenFont, 163.9f, 412.5f, 12f, addressStrings[0], 0, 1f);
        //地 址2
        if (addressStrings.length == 2) {
            writeText(canvas1, fangzhenFont, 163.9f, 397.4f, 12f, addressStrings[1], 0, 1f);
        }
        //授 权 公 告 日
        writeText(canvas1, fangzhenFont, 163.9f, 377f, 12f, formatChineseDate(patentCertDTO.getAuthNoticeDate()), 0, 1f);
        //授 权 公 告 号
        writeText(canvas1, fangzhenFont, 378.5f, 377f, 12f, formatAuthPubNum(patentCertDTO.getAuthNoticeNum()), 0, 1f);
        //添加条形码 x=85.6 y=213.465f
        writeBarcode(canvas1, 85.6f, 213.465f, patentCertDTO.getPatentId(), 10.57f, 0.775f);
        writeText(canvas1, fangzhenFont, 85.4f, 213.465f, 16.5f, "*" + patentCertDTO.getPatentId() + "*", 2.1f, 0f);

        //添加右下角的授权公告日
        writeText(canvas1, kaitiFont, 408f, 114.2f, 13.8f, formatChineseDate(patentCertDTO.getAuthNoticeDate()), -0.9f, 1f);
        //添加二维码 x=457.32 y=688.8f
        addQRCodeToPDF(canvas1, MessageFormat.format(CERD_URL, patentCertDTO.getAuthNoticeNum()), 454.2f, 685.6f, 47.27f, 46.75f);

        //第二页
        PdfContentByte canvas2 = stamper.getOverContent(2);
        //添加证书号
        writeText(canvas2, songtiFont, 130.2f, 728f, 11.495f, patentCertDTO.getCertId(), 0, 1f);
        //申请人
        writeText(canvas2, fangzhenFont, 145.8f, 541f, 10.17f, patentCertDTO.getApplyName(), 0, 1f);
        //发明人
        writeText(canvas2, fangzhenFont, 145.7f, 459.5f, 10.17f, patentCertDTO.getInventorName(), 0, 1f);
        //缴费日期
        writeText(canvas2, kaitiFont, 463.8f, 637f, 10f, formatMonth(patentCertDTO.getApplyDate()), 0.2f, 1f);
        writeText(canvas2, kaitiFont, 484.4f, 637f, 10f, formatDay(patentCertDTO.getApplyDate()), 0.2f, 1f);


        // 关闭文档
        stamper.close();
        reader.close();
        System.out.println(patentCertDTO.getPatentId() + "-" + patentCertDTO.getName() + " 创建成功");
    }

    private static void writeText(PdfContentByte canvas, BaseFont font, float x, float y, float size, String text, float charSpacing, float opacity) {
        PdfGState gState = new PdfGState();
        gState.setFillOpacity(opacity);
        canvas.setGState(gState);
        canvas.beginText();
        canvas.setFontAndSize(font, size);
        canvas.setTextMatrix(x, y);
        canvas.setCharacterSpacing(charSpacing);
        canvas.showText(text);
        canvas.endText();
    }

    @SneakyThrows
    private static void writeBarcode(PdfContentByte canvas, float x, float y, String text, float height, float width) {
        Barcode39 barcode = new Barcode39();
        barcode.setCode(text);
        barcode.setBarHeight(height);
        barcode.setX(width);
        barcode.setFont(null);
        Image barcodeImage = barcode.createImageWithBarcode(canvas, null, null);
        barcodeImage.setAbsolutePosition(x, y);
        canvas.addImage(barcodeImage);
    }

    @SneakyThrows
    private static void addQRCodeToPDF(PdfContentByte canvas, String url, float x, float y, float targetWidth, float targetHeight) {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8"); // 设置字符集
        hints.put(EncodeHintType.MARGIN, 0); // 设置边距
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L); // 设置错误纠正级别

        // 生成二维码
        BitMatrix bitMatrix = qrCodeWriter.encode(url, BarcodeFormat.QR_CODE, 200, 200, hints);
        BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix);

        // 将图像写入字节数组输出流
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "png", baos);
        baos.flush();

        // 将字节数组转换为iText的Image对象
        Image qrImage = Image.getInstance(baos.toByteArray());

        // 设置二维码图像的位置和尺寸
        qrImage.setAbsolutePosition(x, y);
        qrImage.scaleAbsolute(targetWidth, targetHeight);

        // 将二维码图像添加到PDF页面
        canvas.addImage(qrImage);
    }


    @SneakyThrows
    public static String formatChineseDate(String inputDate) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy.MM.dd");
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy年MM月dd日");
        Date date = inputFormat.parse(inputDate);
        return outputFormat.format(date);
    }

    @SneakyThrows
    public static String formatMonth(String inputDate) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy.MM.dd");
        SimpleDateFormat outputFormat = new SimpleDateFormat("MM");
        Date date = inputFormat.parse(inputDate);
        return outputFormat.format(date);
    }
    @SneakyThrows
    public static String formatDay(String inputDate) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy.MM.dd");
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd");
        Date date = inputFormat.parse(inputDate);
        return outputFormat.format(date);
    }


    public static String formatAuthPubNum(String authPubNum) {
        String result = authPubNum.replace("CN", "CN ").replace("U", " U");
        return result;
    }

    public static String formatPatentId(String patentId) {
        return "ZL " + patentId.substring(0, 4) + " " + patentId.substring(4, 5) + " " + patentId.substring(5, 12) + "." + patentId.substring(12, 13);
    }

    public static String[] processString(String str, int length) {
        String newStr = str.substring(0, 6) + " " + str.substring(6);
        if (newStr.length() <= length) {
            return new String[]{newStr};
        }
        int numParts = (newStr.length() + length - 1) / length;
        String[] parts = new String[numParts];
        for (int i = 0; i < numParts; i++) {
            int start = i * length;
            int end = Math.min(start + length, newStr.length());
            parts[i] = newStr.substring(start, end);
        }
        return parts;
    }
}