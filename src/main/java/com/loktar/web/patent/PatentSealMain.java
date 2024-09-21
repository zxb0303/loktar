package com.loktar.web.patent;

import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import lombok.SneakyThrows;

import java.io.FileOutputStream;
import java.util.Random;

public class PatentSealMain {
    public static String BASE_FOLD_PATH = "F:/OneDrive/Patent/seal/";
    public static String CONTRACT_FILE_NAME = "收购合同-贺兰县双马农业科技有限责任公司";
    public static String AGREEMENT_FILE_NAME = "转让协议-贺兰县双马农业科技有限责任公司";
    public static String PDF_FILE_SUFFIX = ".pdf";
    public static String NEW_FILE_SUFFX = "-盖章.pdf";
    public static String IMAGE_0_PATH = BASE_FOLD_PATH + "seal.png";
    public static String[] IMAGES3_PATHS = new String[]{BASE_FOLD_PATH + "seal3_01.png", BASE_FOLD_PATH + "seal3_02.png", BASE_FOLD_PATH + "seal3_03.png"};
    public static String[] IMAGES4_PATHS = new String[]{BASE_FOLD_PATH + "seal4_01.png", BASE_FOLD_PATH + "seal4_02.png", BASE_FOLD_PATH + "seal4_03.png", BASE_FOLD_PATH + "seal4_04.png"};

    public static float SEAL_SIZE = 112f;
    //骑缝位置
    public static float CONTRACT123_HEIGHT = 400f;
    //合同最后一页位置
    public static float CONTRACT3_WEIGHT = 100f;
    public static float CONTRACT3_HEIGHT = 300f;
    //协议位置
    public static float AGREEMENT_WEIGHT = 100f;
    public static float AGREEMENT_HEIGHT = 150f;

    public static void main(String[] args) {
        createContract();
        createAgreement();
    }

    @SneakyThrows
    private static void createAgreement() {
        String contractFIlePath = BASE_FOLD_PATH + AGREEMENT_FILE_NAME + PDF_FILE_SUFFIX;
        PdfReader reader = new PdfReader(contractFIlePath);
        String outputFile = BASE_FOLD_PATH + AGREEMENT_FILE_NAME + NEW_FILE_SUFFX;
        PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(outputFile));
        PdfContentByte canvas1 = stamper.getOverContent(1);
        writeImageSeal0(canvas1, AGREEMENT_WEIGHT, AGREEMENT_HEIGHT);
        stamper.close();
        reader.close();
    }

    @SneakyThrows
    private static void createContract() {
        String contractFIlePath = BASE_FOLD_PATH + CONTRACT_FILE_NAME + PDF_FILE_SUFFIX;
        PdfReader reader = new PdfReader(contractFIlePath);
        int numberOfPages = reader.getNumberOfPages();
        String outputFile = BASE_FOLD_PATH + CONTRACT_FILE_NAME + NEW_FILE_SUFFX;
        PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(outputFile));
        String[] images = new String[0];
        if (numberOfPages == 3) {
            images = IMAGES3_PATHS;
        } else if (numberOfPages == 4) {
            images = IMAGES4_PATHS;
        }
        

        for (int pageNumber = 1; pageNumber <= numberOfPages; pageNumber++) {
            PdfContentByte canvas = stamper.getOverContent(pageNumber);
            writeImageSeal123(reader, canvas, images[pageNumber - 1], CONTRACT123_HEIGHT, 0);
            if (pageNumber == numberOfPages) {
                writeImageSeal0(canvas, CONTRACT3_WEIGHT, CONTRACT3_HEIGHT);
            }
        }
        stamper.close();
        reader.close();
    }

    @SneakyThrows
    private static void writeImageSeal123(PdfReader reader, PdfContentByte canvas, String imagePath, float y, float x_offset) {
        Image image = Image.getInstance(imagePath);
        image.scaleToFit(SEAL_SIZE / 3, SEAL_SIZE);
        float pageWidth = reader.getPageSize(1).getWidth();
        float imageWidth = image.getScaledWidth();
        float x = pageWidth - imageWidth - x_offset;
        image.setAbsolutePosition(x, y);
        canvas.addImage(image);
    }

    @SneakyThrows
    private static void writeImageSeal0(PdfContentByte canvas, float x, float y) {
        Image image = Image.getInstance(IMAGE_0_PATH);
        image.scaleToFit(SEAL_SIZE, SEAL_SIZE);
        image.setAbsolutePosition(x, y);
        Random random = new Random();
        float rotationAngle = (random.nextFloat() * 90) - 45;
        image.setRotationDegrees(rotationAngle);
        canvas.addImage(image);
    }
}

