package com.loktar.web.patent;

import lombok.SneakyThrows;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.util.Matrix;

import java.awt.geom.AffineTransform;
import java.io.File;
import java.util.Random;

public class PatentSealMain {
    public static String BASE_FOLD_PATH = "F:/OneDrive/Patent/seal/";
    public static String CONTRACT_FILE_NAME = "专利买卖合同(1)";
    public static String AGREEMENT_FILE_NAME = "专利权变更证明_20260129_0001";
    public static String PDF_FILE_SUFFIX = ".pdf";
    public static String NEW_FILE_SUFFX = "-盖章.pdf";
    public static String IMAGE_0_PATH = BASE_FOLD_PATH + "seal.png";
    public static String[] IMAGES3_PATHS = new String[]{BASE_FOLD_PATH + "seal3_01.png", BASE_FOLD_PATH + "seal3_02.png", BASE_FOLD_PATH + "seal3_03.png"};
    public static String[] IMAGES4_PATHS = new String[]{BASE_FOLD_PATH + "seal4_01.png", BASE_FOLD_PATH + "seal4_02.png", BASE_FOLD_PATH + "seal4_03.png", BASE_FOLD_PATH + "seal4_04.png"};

    public static float SEAL_SIZE = 120f;
    //骑缝位置
    public static float CONTRACT123_HEIGHT = 300f;
    //合同最后一页位置
    public static float CONTRACT3_WEIGHT = 100f;
    public static float CONTRACT3_HEIGHT = 400f;
    //协议位置
    public static float AGREEMENT_WEIGHT = 100f;
    public static float AGREEMENT_HEIGHT = 100f;

    public static void main(String[] args) {
        createContract();
//        createAgreement();
    }

    @SneakyThrows
    private static void createAgreement() {
        String contractFIlePath = BASE_FOLD_PATH + AGREEMENT_FILE_NAME + PDF_FILE_SUFFIX;
        String outputFile = BASE_FOLD_PATH + AGREEMENT_FILE_NAME + NEW_FILE_SUFFX;
        try (PDDocument document = Loader.loadPDF(new File(contractFIlePath))) {
            PDPage page = document.getPage(0);
            try (PDPageContentStream canvas = new PDPageContentStream(document, page,
                    PDPageContentStream.AppendMode.APPEND, true, true)) {
                writeImageSeal0(document, canvas, AGREEMENT_WEIGHT, AGREEMENT_HEIGHT);
            }
            document.save(outputFile);
        }
    }

    @SneakyThrows
    private static void createContract() {
        String contractFIlePath = BASE_FOLD_PATH + CONTRACT_FILE_NAME + PDF_FILE_SUFFIX;
        String outputFile = BASE_FOLD_PATH + CONTRACT_FILE_NAME + NEW_FILE_SUFFX;
        try (PDDocument document = Loader.loadPDF(new File(contractFIlePath))) {
            int numberOfPages = document.getNumberOfPages();
            String[] images = new String[0];
            if (numberOfPages == 3) {
                images = IMAGES3_PATHS;
            } else if (numberOfPages == 4) {
                images = IMAGES4_PATHS;
            }

            for (int pageIndex = 0; pageIndex < numberOfPages; pageIndex++) {
                PDPage page = document.getPage(pageIndex);
                try (PDPageContentStream canvas = new PDPageContentStream(document, page,
                        PDPageContentStream.AppendMode.APPEND, true, true)) {
                    writeImageSeal123(document, page, canvas, images[pageIndex], CONTRACT123_HEIGHT, 0);
                    if (pageIndex == numberOfPages - 1) {
                        writeImageSeal0(document, canvas, CONTRACT3_WEIGHT, CONTRACT3_HEIGHT);
                    }
                    // 只有1个pdf，第1页或者最后1页是协议时
//                    if (pageIndex == 0) {
//                        writeImageSeal0(document, canvas, CONTRACT3_WEIGHT, CONTRACT3_HEIGHT);
//                    }
//                    if (pageIndex == 3) {
//                        writeImageSeal0(document, canvas, CONTRACT3_WEIGHT, CONTRACT3_HEIGHT);
//                    }
                }
            }
            document.save(outputFile);
        }
    }

    @SneakyThrows
    private static void writeImageSeal123(PDDocument document, PDPage page, PDPageContentStream canvas,
                                          String imagePath, float y, float x_offset) {
        PDImageXObject image = PDImageXObject.createFromFile(imagePath, document);
        float ratio = Math.min((SEAL_SIZE / 3f) / image.getWidth(), SEAL_SIZE / image.getHeight());
        float scaledWidth = image.getWidth() * ratio;
        float scaledHeight = image.getHeight() * ratio;
        float pageWidth = page.getMediaBox().getWidth();
        float x = pageWidth - scaledWidth - x_offset;
        canvas.drawImage(image, x, y, scaledWidth, scaledHeight);
    }

    @SneakyThrows
    private static void writeImageSeal0(PDDocument document, PDPageContentStream canvas, float x, float y) {
        PDImageXObject image = PDImageXObject.createFromFile(IMAGE_0_PATH, document);
        float ratio = Math.min(SEAL_SIZE / image.getWidth(), SEAL_SIZE / image.getHeight());
        float scaledWidth = image.getWidth() * ratio;
        float scaledHeight = image.getHeight() * ratio;
        Random random = new Random();
        float rotationAngle = (random.nextFloat() * 90) - 45;
        // 围绕图像中心旋转
        float centerX = x + scaledWidth / 2f;
        float centerY = y + scaledHeight / 2f;
        AffineTransform at = new AffineTransform();
        at.translate(centerX, centerY);
        at.rotate(Math.toRadians(rotationAngle));
        at.translate(-scaledWidth / 2f, -scaledHeight / 2f);
        at.scale(scaledWidth, scaledHeight);
        canvas.drawImage(image, new Matrix(at));
    }
}
