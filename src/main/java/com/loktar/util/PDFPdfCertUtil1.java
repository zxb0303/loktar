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
import java.awt.*;
import java.awt.font.TextAttribute;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PDFPdfCertUtil1 {
    private static String TEMPLATE_PDF_FILE_PATH = "F:/OneDrive/Patent/cert/template/template1.pdf";
    private static String OUTPUT_FOLD_PATH = "F:/OneDrive/Patent/cert/output/";
    private static String FONT_SONG_PATH = "F:/OneDrive/Patent/cert/template/SimSun.ttc";
    private static String FONT_FANGZHEN_PATH = "F:/OneDrive/Patent/cert/template/SURSONG.ttc";
    private static String FONT_KAI_PATH = "F:/OneDrive/Patent/cert/template/simkai.ttc";
    private static String FONT_HUAWEN_KAI_PATH = "F:/OneDrive/Patent/cert/template/STKAITI.ttc";

    private static String CERD_URL = "http://epub.cnipa.gov.cn/cred/{0}";

    @SneakyThrows
    public static void generatePDF(PatentCertDTO patentCertDTO) {
        PdfReader reader = new PdfReader(TEMPLATE_PDF_FILE_PATH);
        String outputFileName = OUTPUT_FOLD_PATH + patentCertDTO.getApplyName() + "-" + patentCertDTO.getPatentId() + ".pdf";
        PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(outputFileName));
        BaseFont songtiFont = BaseFont.createFont(FONT_SONG_PATH + ",0", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        BaseFont fangzhenFont = BaseFont.createFont(FONT_FANGZHEN_PATH + ",0", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        BaseFont kaitiFont = BaseFont.createFont(FONT_KAI_PATH + ",0", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        BaseFont huawenkaitiFont = BaseFont.createFont(FONT_HUAWEN_KAI_PATH + ",0", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        //第一页
        PdfContentByte canvas1 = stamper.getOverContent(1);
        //添加证书号
        writeText(canvas1, songtiFont, 575f, 3074f, 54f, patentCertDTO.getCertId(), 1f, 1f, 1f, 0f,1f,1f);
//        //添加实用新型名称
        writeText(canvas1, songtiFont, 672f, 2463f, 55f, patentCertDTO.getName(), 1f, 1f, 1f, 0f,1f,1f);
//        //添加发明人
        writeText(canvas1, songtiFont, 672f, 2316f, 55f, patentCertDTO.getInventorName(), 1f, 1f, 1f, 0f,1f,1f);
//        //专 利 号
        writeText(canvas1, songtiFont, 672f, 2167f, 54f, formatPatentId(patentCertDTO.getPatentId()), 1f, 1f, 1f, 0f,1f,1f);
//        //专 利 申 请 日formatChineseDate(patentCertDTO.getApplyDate())
        writeText(canvas1, songtiFont, 672f, 2020f, 54f, formatChineseDate(patentCertDTO.getApplyDate()), 1f, 1f, 1f, 15f,1f,1f);
//        //专 利 权 人
        writeText(canvas1, songtiFont, 672f, 1873f, 54f, patentCertDTO.getApplyName(), 1f, 1f, 1f, 0f,1f,1f);
        String str = patentCertDTO.getAddress();
        String[] addressStrings = processString(str, 30);
        if (addressStrings.length > 2) {
            throw new RuntimeException("地址过长");
        }
        //地 址1
        writeText(canvas1, songtiFont, 672f, 1725f, 54f, addressStrings[0], 1f, 1f, 1f, 15f,1f,1f);
        //地 址2
        if (addressStrings.length == 2) {
            //TODO
            // writeText(canvas1, songtiFont, 163.9f, 397.4f, 12f, addressStrings[1], 0, 1f);
        }
//        //授 权 公 告 日
        writeText(canvas1, songtiFont, 672f, 1578f, 54f, formatChineseDate(patentCertDTO.getAuthNoticeDate()), 1f, 1f, 1f, 15f,1f,1f);
//        //授 权 公 告 号
        writeText(canvas1, songtiFont, 1598f, 1578f, 54f, formatAuthPubNum(patentCertDTO.getAuthNoticeNum()), 1f, 1f,1f,0f,1f,1f);
//        //添加条形码 x=85.6 y=213.465f
        writeBarcode(canvas1, 358f, 830f, patentCertDTO.getPatentId(), 44.55f, 3.22f);
//        writeText(canvas1, fangzhenFont, 85.4f, 213.465f, 16.5f, "*" + patentCertDTO.getPatentId() + "*", 2.1f, 0f);
//
//        //添加右下角的授权公告日
        writeText(canvas1, huawenkaitiFont, 1564f, 510f, 76f, formatChineseDate(patentCertDTO.getAuthNoticeDate()), 1f, 1f,1.9f,12f,1f,1.1f);
//        //添加二维码 x=457.32 y=688.8f
        addQRCodeToPDF(canvas1, MessageFormat.format(CERD_URL, patentCertDTO.getAuthNoticeNum()), 1988f, 2866f, 206f, 206f);

//        //第二页
        PdfContentByte canvas2 = stamper.getOverContent(2);
//        //添加证书号
        writeText(canvas2, songtiFont, 560f, 3080f, 54f, patentCertDTO.getCertId(), 1f, 1f, 1f, 0f,1f,1f);
//        //申请人 TODO 不同的情况申请人不一样 还要处理一下
        writeText(canvas2, huawenkaitiFont, 556f, 2338f, 56f, patentCertDTO.getInventorName(), 1f, 1f, 1f, 0f,1f,1f);
//        //发明人
        writeText(canvas2, huawenkaitiFont, 556f, 1984f, 56f, patentCertDTO.getInventorName(), 1f, 1f, 1f, 0f,1f,1f);
//        //缴费日期
        writeText(canvas2, huawenkaitiFont, 1981f, 2759f, 58f, formatMonth(patentCertDTO.getApplyDate()), 1f, 1f, 1.2f, 0f,1f,1f);
        writeText(canvas2, huawenkaitiFont, 2096f, 2759f, 58f, formatDay(patentCertDTO.getApplyDate()), 1f, 1f, 1.2f, 0f,1f,1f);


        // 关闭文档
        stamper.close();
        reader.close();
        System.out.println(patentCertDTO.getPatentId() + "-" + patentCertDTO.getName() + " 创建成功");
    }

    @SneakyThrows
    private static void writeText(PdfContentByte canvas, BaseFont baseFont, float x, float y, float size, String text, float charSpacing, float opacity, float boldValue, float extraDigitSpacing, float horizontalScale, float verticalScale) {
        // Create a temporary Font object to measure the text
        Font awtFont = new Font(baseFont.getPostscriptFontName(), Font.PLAIN, (int) size);
        Map<TextAttribute, Object> attributes = new HashMap<>();
        attributes.put(TextAttribute.WEIGHT, boldValue);
        awtFont = awtFont.deriveFont(attributes);

        // Create a temporary BufferedImage to get FontMetrics
        BufferedImage tempImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D tempG2d = tempImage.createGraphics();
        tempG2d.setFont(awtFont);
        FontMetrics fontMetrics = tempG2d.getFontMetrics();

        // Calculate the width of the text
        int textWidth = 0;
        boolean inDigitSequence = false;
        boolean isFirstChar = true;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (Character.isDigit(c)) {
                if (!inDigitSequence && !isFirstChar) {
                    textWidth += extraDigitSpacing; // Add extra spacing before the digit sequence
                }
                inDigitSequence = true;
            } else {
                if (inDigitSequence) {
                    if (c != ' ') {
                        textWidth += extraDigitSpacing; // Add extra spacing after the digit sequence unless followed by a space
                    }
                    inDigitSequence = false;
                }
            }
            textWidth += fontMetrics.charWidth(c) + charSpacing;
            isFirstChar = false;
        }
        if (inDigitSequence) {
            textWidth += extraDigitSpacing; // Add extra spacing after the last digit sequence
        }
        textWidth -= charSpacing; // Remove the extra spacing after the last character

        // Calculate the height of the text
        int textHeight = fontMetrics.getHeight();

        // Create a BufferedImage with the calculated width and height
        BufferedImage bufferedImage = new BufferedImage((int)(textWidth * horizontalScale), (int)(textHeight * verticalScale), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = bufferedImage.createGraphics();

        // Set rendering hints for better text quality
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        // Set the font and color
        g2d.setFont(awtFont);
        g2d.setColor(Color.BLACK);

        // Apply scaling
        AffineTransform originalTransform = g2d.getTransform();
        g2d.scale(horizontalScale, verticalScale);

        // Draw the text with charSpacing
        float currentX = 0;
        int baseline = fontMetrics.getAscent();
        inDigitSequence = false;
        isFirstChar = true;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (Character.isDigit(c)) {
                if (!inDigitSequence && !isFirstChar) {
                    currentX += extraDigitSpacing; // Add extra spacing before the digit sequence
                }
                inDigitSequence = true;
            } else {
                if (inDigitSequence) {
                    if (c != ' ') {
                        currentX += extraDigitSpacing; // Add extra spacing after the digit sequence unless followed by a space
                    }
                    inDigitSequence = false;
                }
            }
            g2d.drawString(String.valueOf(c), currentX / horizontalScale, baseline / verticalScale);
            currentX += fontMetrics.charWidth(c) + charSpacing;
            isFirstChar = false;
        }
        if (inDigitSequence) {
            currentX += extraDigitSpacing; // Add extra spacing after the last digit sequence
        }
        g2d.setTransform(originalTransform); // Restore the original transform
        g2d.dispose();

        // Convert BufferedImage to byte array
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "png", baos);
        baos.flush();
        byte[] imageInByte = baos.toByteArray();
        baos.close();

        // Create an iText Image from the byte array
        Image image = Image.getInstance(imageInByte);
        image.setAbsolutePosition(x, y - (int)(textHeight * verticalScale)); // Adjust position to match text baseline
        image.scalePercent(100f * size / bufferedImage.getHeight()); // Scale image to match font size

        // Set opacity
        PdfGState gState = new PdfGState();
        gState.setFillOpacity(opacity);
        canvas.setGState(gState);

        // Add the image to the canvas
        canvas.addImage(image);
    }
    @SneakyThrows
    private static void writeText2(PdfContentByte canvas, BaseFont baseFont, BaseFont chineseFont, float x, float y, float size, String text, float charSpacing, float opacity, float boldValue, float extraDigitSpacing) {
        // Create temporary Font objects to measure the text
        Font awtFont = new Font(baseFont.getPostscriptFontName(), Font.PLAIN, (int) size);
        Font awtChineseFont = new Font(chineseFont.getPostscriptFontName(), Font.PLAIN, (int) size);
        Map<TextAttribute, Object> attributes = new HashMap<>();
        attributes.put(TextAttribute.WEIGHT, boldValue);
        awtFont = awtFont.deriveFont(attributes);
        awtChineseFont = awtChineseFont.deriveFont(attributes);

        // Create temporary BufferedImage to get FontMetrics
        BufferedImage tempImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D tempG2d = tempImage.createGraphics();
        tempG2d.setFont(awtFont);
        FontMetrics fontMetrics = tempG2d.getFontMetrics();
        tempG2d.setFont(awtChineseFont);
        FontMetrics chineseFontMetrics = tempG2d.getFontMetrics();

        // Calculate the width of the text
        int textWidth = 0;
        boolean inDigitSequence = false;
        boolean isFirstChar = true;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            FontMetrics currentFontMetrics = Character.isDigit(c) ? fontMetrics : (Character.UnicodeScript.of(c) == Character.UnicodeScript.HAN ? chineseFontMetrics : fontMetrics);
            if (Character.isDigit(c)) {
                if (!inDigitSequence && !isFirstChar) {
                    textWidth += extraDigitSpacing; // Add extra spacing before the digit sequence
                }
                inDigitSequence = true;
            } else {
                if (inDigitSequence) {
                    if (c != ' ') {
                        textWidth += extraDigitSpacing; // Add extra spacing after the digit sequence unless followed by a space
                    }
                    inDigitSequence = false;
                }
            }
            textWidth += currentFontMetrics.charWidth(c) + charSpacing;
            isFirstChar = false;
        }
        if (inDigitSequence) {
            textWidth += extraDigitSpacing; // Add extra spacing after the last digit sequence
        }
        textWidth -= charSpacing; // Remove the extra spacing after the last character

        // Calculate the height of the text
        int textHeight = Math.max(fontMetrics.getHeight(), chineseFontMetrics.getHeight());

        // Create a BufferedImage with the calculated width and height
        BufferedImage bufferedImage = new BufferedImage(textWidth, textHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = bufferedImage.createGraphics();

        // Set rendering hints for better text quality
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        // Set the font and color
        g2d.setColor(Color.BLACK);

        // Draw the text with charSpacing
        float currentX = 0;
        int baseline = Math.max(fontMetrics.getAscent(), chineseFontMetrics.getAscent());
        inDigitSequence = false;
        isFirstChar = true;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            Font currentFont = Character.isDigit(c) ? awtFont : (Character.UnicodeScript.of(c) == Character.UnicodeScript.HAN ? awtChineseFont : awtFont);
            FontMetrics currentFontMetrics = Character.isDigit(c) ? fontMetrics : (Character.UnicodeScript.of(c) == Character.UnicodeScript.HAN ? chineseFontMetrics : fontMetrics);
            g2d.setFont(currentFont);
            if (Character.isDigit(c)) {
                if (!inDigitSequence && !isFirstChar) {
                    currentX += extraDigitSpacing; // Add extra spacing before the digit sequence
                }
                inDigitSequence = true;
            } else {
                if (inDigitSequence) {
                    if (c != ' ') {
                        currentX += extraDigitSpacing; // Add extra spacing after the digit sequence unless followed by a space
                    }
                    inDigitSequence = false;
                }
            }
            g2d.drawString(String.valueOf(c), currentX, baseline);
            currentX += currentFontMetrics.charWidth(c) + charSpacing;
            isFirstChar = false;
        }
        if (inDigitSequence) {
            currentX += extraDigitSpacing; // Add extra spacing after the last digit sequence
        }
        g2d.dispose();

        // Convert BufferedImage to byte array
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "png", baos);
        baos.flush();
        byte[] imageInByte = baos.toByteArray();
        baos.close();

        // Create an iText Image from the byte array
        Image image = Image.getInstance(imageInByte);
        image.setAbsolutePosition(x, y - textHeight); // Adjust position to match text baseline
        image.scalePercent(100f * size / bufferedImage.getHeight()); // Scale image to match font size

        // Set opacity
        PdfGState gState = new PdfGState();
        gState.setFillOpacity(opacity);
        canvas.setGState(gState);

        // Add the image to the canvas
        canvas.addImage(image);
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