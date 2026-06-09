package com.loktar.web.child;

import lombok.SneakyThrows;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.util.Matrix;

import java.io.File;

public class PDF {

    @SneakyThrows
    public static void main(String[] args) {
        String sourcePath = "F:/周末作业0522(1).pdf";
        String outputPath = sourcePath.replace(".pdf", "_split.pdf");
        splitPdfHorizontal(sourcePath, outputPath);
    }

    /**
     * 将PDF每页横向拆分为两页（适用于A3试卷拆分为A4打印）
     * 每页的左半部分和右半部分分别生成新的一页
     *
     * @param sourcePath 源PDF文件路径
     * @param outputPath 输出PDF文件路径
     */
    @SneakyThrows
    public static void splitPdfHorizontal(String sourcePath, String outputPath) {
        File sourceFile = new File(sourcePath);

        try (PDDocument sourceDoc = Loader.loadPDF(sourceFile);
             PDDocument outputDoc = new PDDocument()) {

            int totalPages = sourceDoc.getNumberOfPages();
            System.out.println("源PDF共 " + totalPages + " 页");

            for (int i = 0; i < totalPages; i++) {
                PDPage sourcePage = sourceDoc.getPage(i);
                PDRectangle mediaBox = sourcePage.getMediaBox();
                float originalWidth = mediaBox.getWidth();
                float originalHeight = mediaBox.getHeight();
                float halfWidth = originalWidth / 2;

                System.out.println("处理第 " + (i + 1) + " 页，原始尺寸: " + originalWidth + " x " + originalHeight);

                // 左半页 - 裁剪到左半区域
                PDPage leftPage = outputDoc.importPage(sourcePage);
                leftPage.setMediaBox(new PDRectangle(0, 0, halfWidth, originalHeight));
                leftPage.setCropBox(new PDRectangle(0, 0, halfWidth, originalHeight));

                // 右半页 - 裁剪到右半区域，并通过平移变换将右半内容移至页面左侧
                PDPage rightPage = outputDoc.importPage(sourcePage);
                rightPage.setMediaBox(new PDRectangle(0, 0, halfWidth, originalHeight));
                rightPage.setCropBox(new PDRectangle(0, 0, halfWidth, originalHeight));

                try (PDPageContentStream cs = new PDPageContentStream(outputDoc, rightPage,
                        PDPageContentStream.AppendMode.PREPEND, true)) {
                    cs.transform(Matrix.getTranslateInstance(-halfWidth, 0));
                }
            }

            outputDoc.save(outputPath);
            System.out.println("拆分完成，输出文件: " + outputPath);
            System.out.println("输出PDF共 " + outputDoc.getNumberOfPages() + " 页");
        }
    }
}
