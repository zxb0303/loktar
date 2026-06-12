package com.loktar.web.child;

import lombok.SneakyThrows;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.util.Matrix;

import java.awt.image.BufferedImage;
import java.io.File;

public class PDF {

    // 自适应放大后保留的边距比例：1.0 表示让黑色内容完全填满 A4 页面， 小于 1 则四周留出对应比例的空白。可按打印机/装订需求自行调整。
    private static final float MARGIN_RATIO = 0.98f;
    //用于"找黑边"的内容检测渲染分辨率（DPI）。值越大检测越精确但越耗时；试卷类一般 100 DPI 足够。
    private static final int DETECT_DPI = 100;
    //灰度阈值：像素灰度 ≤ 此值视为"黑色内容"，> 此值视为空白。 试卷扫描件偏灰时可适当调高（如 230），干净电子稿可调低（如 200）。
    private static final int DARK_THRESHOLD = 220;
    //水印所在角落 枚举
    public enum WatermarkPosition {
        NONE, TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT
    }
    //拆分方向枚举
    public enum SplitDirection {
        /** 横向拆分：每页从中间垂直切开，分为左右两页 */
        HORIZONTAL,
        /** 纵向拆分：每页从中间水平切开，分为上下两页 */
        VERTICAL
    }
    //TODO 水印所在角落
    private static final WatermarkPosition WATERMARK_POSITION = WatermarkPosition.BOTTOM_RIGHT;
    //TODO 水印宽度（PDF 点，1 pt ≈ 0.353 mm；A4 宽约 595pt，A3 横向宽约 1190pt）
    private static final float WATERMARK_WIDTH = 140f;
    //TODO 水印高度（PDF 点）
    private static final float WATERMARK_HEIGHT = 70f;

    //swapOrder 拆分后是否对换顺序：false 时横向为 左→右、纵向为 上→下；true 时横向为 右→左、纵向为 下→上
    @SneakyThrows
    public static void main(String[] args) {
        String sourcePath1 = "F:/周末作业0522(1).pdf";
        String outputPath1 = sourcePath1.replace(".pdf", "_split.pdf");
        //复杂拆分：去除水印，放大内容
        splitPdf(sourcePath1, outputPath1, SplitDirection.HORIZONTAL, false);
        //简单拆分：纯几何切分，不处理水印，不放大内容
        String sourcePath2 = "F:/一年级下册数学高频考点提分卷.pdf";
        String outputPath2 = sourcePath2.replace(".pdf", "_split.pdf");
        splitPdfSimple(sourcePath2, outputPath2, SplitDirection.VERTICAL, true);
    }
    
    /**
     * 简单对半拆分：纯几何拆分，不做黑色内容检测、不做缩放居中。
     * 横向（HORIZONTAL）：每页从中间垂直切开为左右两页；
     * 纵向（VERTICAL）：每页从中间水平切开为上下两页。
     *
     * @param sourcePath 源PDF文件路径
     * @param outputPath 输出PDF文件路径
     * @param direction  拆分方向
     * @param swapOrder  是否对换两页顺序：false 时横向为 左→右、纵向为 上→下；true 时横向为 右→左、纵向为 下→上
     */
    @SneakyThrows
    public static void splitPdfSimple(String sourcePath, String outputPath, SplitDirection direction, boolean swapOrder) {
        File sourceFile = new File(sourcePath);

        try (PDDocument sourceDoc = Loader.loadPDF(sourceFile);
             PDDocument outputDoc = new PDDocument()) {

            int totalPages = sourceDoc.getNumberOfPages();
            System.out.println("源PDF共 " + totalPages + " 页（简单拆分），拆分方向: " + direction + "，对换顺序: " + swapOrder);

            for (int i = 0; i < totalPages; i++) {
                PDPage sourcePage = sourceDoc.getPage(i);
                PDRectangle mediaBox = sourcePage.getMediaBox();
                float originalWidth = mediaBox.getWidth();
                float originalHeight = mediaBox.getHeight();

                System.out.println("处理第 " + (i + 1) + " 页，原始尺寸: " + originalWidth + " x " + originalHeight);

                if (direction == SplitDirection.HORIZONTAL) {
                    float halfWidth = originalWidth / 2f;
                    if (!swapOrder) {
                        appendLeftHalf(outputDoc, sourcePage, halfWidth, originalHeight);
                        appendRightHalf(outputDoc, sourcePage, halfWidth, originalHeight);
                    } else {
                        appendRightHalf(outputDoc, sourcePage, halfWidth, originalHeight);
                        appendLeftHalf(outputDoc, sourcePage, halfWidth, originalHeight);
                    }
                } else {
                    float halfHeight = originalHeight / 2f;
                    if (!swapOrder) {
                        appendTopHalf(outputDoc, sourcePage, originalWidth, halfHeight);
                        appendBottomHalf(outputDoc, sourcePage, originalWidth, halfHeight);
                    } else {
                        appendBottomHalf(outputDoc, sourcePage, originalWidth, halfHeight);
                        appendTopHalf(outputDoc, sourcePage, originalWidth, halfHeight);
                    }
                }
            }

            outputDoc.save(outputPath);
            System.out.println("简单拆分完成，输出文件: " + outputPath
                    + "，共 " + outputDoc.getNumberOfPages() + " 页");
        }
    }

    /**
     * 追加左半页（裁剪到左半区域）
     */
    @SneakyThrows
    private static void appendLeftHalf(PDDocument outputDoc, PDPage sourcePage, float halfWidth, float originalHeight) {
        PDPage leftPage = outputDoc.importPage(sourcePage);
        leftPage.setMediaBox(new PDRectangle(0, 0, halfWidth, originalHeight));
        leftPage.setCropBox(new PDRectangle(0, 0, halfWidth, originalHeight));
    }

    /**
     * 追加右半页（裁剪到右半区域，并通过平移变换将右半内容移至页面左侧）
     */
    @SneakyThrows
    private static void appendRightHalf(PDDocument outputDoc, PDPage sourcePage, float halfWidth, float originalHeight) {
        PDPage rightPage = outputDoc.importPage(sourcePage);
        rightPage.setMediaBox(new PDRectangle(0, 0, halfWidth, originalHeight));
        rightPage.setCropBox(new PDRectangle(0, 0, halfWidth, originalHeight));

        try (PDPageContentStream cs = new PDPageContentStream(outputDoc, rightPage,
                PDPageContentStream.AppendMode.PREPEND, true)) {
            cs.transform(Matrix.getTranslateInstance(-halfWidth, 0));
        }
    }

    /**
     * 追加上半页（PDF坐标原点在左下角，上半部分Y范围为[halfHeight, originalHeight]）
     */
    @SneakyThrows
    private static void appendTopHalf(PDDocument outputDoc, PDPage sourcePage, float originalWidth, float halfHeight) {
        PDPage topPage = outputDoc.importPage(sourcePage);
        topPage.setMediaBox(new PDRectangle(0, halfHeight, originalWidth, halfHeight));
        topPage.setCropBox(new PDRectangle(0, halfHeight, originalWidth, halfHeight));
    }

    /**
     * 追加下半页（裁剪到下半区域）
     */
    @SneakyThrows
    private static void appendBottomHalf(PDDocument outputDoc, PDPage sourcePage, float originalWidth, float halfHeight) {
        PDPage bottomPage = outputDoc.importPage(sourcePage);
        bottomPage.setMediaBox(new PDRectangle(0, 0, originalWidth, halfHeight));
        bottomPage.setCropBox(new PDRectangle(0, 0, originalWidth, halfHeight));
    }

    /**
     * 将PDF每页拆分为两页（适用于A3试卷拆分为A4打印）。
     * <p>
     * 对每一半内容会先识别黑色像素的最小包围盒（即试卷上文字/线条的真实范围），
     * 然后按比例放大并居中到目标页面，多余的白边自动去掉，
     * 内容溢出范围的部分也会被自动裁切。
     *
     * @param sourcePath 源PDF文件路径
     * @param outputPath 输出PDF文件路径
     * @param direction  拆分方向：{@link SplitDirection#HORIZONTAL} 横向（左右拆）；{@link SplitDirection#VERTICAL} 纵向（上下拆）
     * @param swapOrder  是否对换两页顺序：false 时横向为 左→右、纵向为 上→下；true 时横向为 右→左、纵向为 下→上
     */
    @SneakyThrows
    public static void splitPdf(String sourcePath, String outputPath, SplitDirection direction, boolean swapOrder) {
        File sourceFile = new File(sourcePath);

        try (PDDocument sourceDoc = Loader.loadPDF(sourceFile);
             PDDocument outputDoc = new PDDocument()) {

            PDFRenderer renderer = new PDFRenderer(sourceDoc);
            int totalPages = sourceDoc.getNumberOfPages();
            System.out.println("源PDF共 " + totalPages + " 页，拆分方向: " + direction
                    + "，对换顺序: " + swapOrder
                    + "，边距比例: " + MARGIN_RATIO
                    + "，检测DPI: " + DETECT_DPI + "，黑色阈值: " + DARK_THRESHOLD);

            for (int i = 0; i < totalPages; i++) {
                PDPage sourcePage = sourceDoc.getPage(i);
                PDRectangle mediaBox = sourcePage.getMediaBox();
                float originalWidth = mediaBox.getWidth();
                float originalHeight = mediaBox.getHeight();

                // 渲染整页用于扫描黑色像素范围
                BufferedImage img = renderer.renderImageWithDPI(i, DETECT_DPI);
                int iw = img.getWidth();
                int ih = img.getHeight();

                // 计算水印在像素坐标系下的矩形（左上 (x1,y1)，右下 (x2,y2)）；null 表示无需过滤
                int[] watermarkPxRect = computeWatermarkPixelRect(iw, ih, originalWidth, originalHeight);

                if (direction == SplitDirection.HORIZONTAL) {
                    float halfWidth = originalWidth / 2f;
                    int halfIw = iw / 2;

                    PDRectangle leftBbox = detectContentBox(img, 0, halfIw, 0, ih, originalWidth, originalHeight, watermarkPxRect);
                    PDRectangle rightBbox = detectContentBox(img, halfIw, iw, 0, ih, originalWidth, originalHeight, watermarkPxRect);

                    System.out.println("第 " + (i + 1) + " 页 左半黑色范围: " + describe(leftBbox)
                            + "，右半黑色范围: " + describe(rightBbox));

                    // 根据 swapOrder 决定左右顺序
                    if (!swapOrder) {
                        appendFittedHalfPage(outputDoc, sourcePage, leftBbox, halfWidth, originalHeight);
                        appendFittedHalfPage(outputDoc, sourcePage, rightBbox, halfWidth, originalHeight);
                    } else {
                        appendFittedHalfPage(outputDoc, sourcePage, rightBbox, halfWidth, originalHeight);
                        appendFittedHalfPage(outputDoc, sourcePage, leftBbox, halfWidth, originalHeight);
                    }
                } else {
                    float halfHeight = originalHeight / 2f;
                    int halfIh = ih / 2;

                    // 像素坐标 y=0 对应 PDF 顶部，所以上半页对应像素 [0, halfIh)
                    PDRectangle topBbox = detectContentBox(img, 0, iw, 0, halfIh, originalWidth, originalHeight, watermarkPxRect);
                    PDRectangle bottomBbox = detectContentBox(img, 0, iw, halfIh, ih, originalWidth, originalHeight, watermarkPxRect);

                    System.out.println("第 " + (i + 1) + " 页 上半黑色范围: " + describe(topBbox)
                            + "，下半黑色范围: " + describe(bottomBbox));

                    // 根据 swapOrder 决定上下顺序
                    if (!swapOrder) {
                        appendFittedHalfPage(outputDoc, sourcePage, topBbox, originalWidth, halfHeight);
                        appendFittedHalfPage(outputDoc, sourcePage, bottomBbox, originalWidth, halfHeight);
                    } else {
                        appendFittedHalfPage(outputDoc, sourcePage, bottomBbox, originalWidth, halfHeight);
                        appendFittedHalfPage(outputDoc, sourcePage, topBbox, originalWidth, halfHeight);
                    }
                }
            }

            outputDoc.save(outputPath);
            System.out.println("拆分完成，输出文件: " + outputPath
                    + "，共 " + outputDoc.getNumberOfPages() + " 页");
        }
    }

    /**
     * 追加一个尺寸为 (fitW × fitH) 的输出页，并把 contentBox 等比缩放并居中到该页中心。
     * 同一变换天然处理了“右半页 / 下半页内容需要平移”的情况，无需特别区分方向。
     */
    @SneakyThrows
    private static void appendFittedHalfPage(PDDocument outputDoc, PDPage sourcePage,
                                             PDRectangle contentBox, float fitW, float fitH) {
        PDPage page = outputDoc.importPage(sourcePage);
        page.setMediaBox(new PDRectangle(0, 0, fitW, fitH));
        page.setCropBox(new PDRectangle(0, 0, fitW, fitH));
        applyFitTransform(outputDoc, page, contentBox, fitW, fitH, fitW / 2f, fitH / 2f);
    }

    /**
     * 扫描像素矩形 [xStart, xEnd) × [yStart, yEnd) 内所有"黑色"像素（灰度 ≤ {@link #DARK_THRESHOLD}），
     * 计算其最小包围盒，并把像素坐标转换为 PDF 用户坐标系（注意 Y 轴翻转）。
     * <p>
     * 若该区间没有任何黑色像素，则返回该扫描区间对应的完整 PDF 矩形作为兜底，避免除零。
     *
     * @param watermarkPxRect 形如 {x1, y1, x2, y2} 的水印像素矩形（含左上、不含右下）；
     *                        命中该矩形的像素会被跳过；传入 {@code null} 表示不过滤。
     */
    private static PDRectangle detectContentBox(BufferedImage img, int xStart, int xEnd, int yStart, int yEnd,
                                                float originalWidth, float originalHeight,
                                                int[] watermarkPxRect) {
        int iw = img.getWidth();
        int ih = img.getHeight();
        int minX = xEnd;
        int minY = yEnd;
        int maxX = -1;
        int maxY = -1;
        for (int y = yStart; y < yEnd; y++) {
            for (int x = xStart; x < xEnd; x++) {
                if (watermarkPxRect != null
                        && x >= watermarkPxRect[0] && x < watermarkPxRect[2]
                        && y >= watermarkPxRect[1] && y < watermarkPxRect[3]) {
                    continue;
                }
                int rgb = img.getRGB(x, y);
                int r = (rgb >> 16) & 0xff;
                int g = (rgb >> 8) & 0xff;
                int b = rgb & 0xff;
                int gray = (r + g + b) / 3;
                if (gray <= DARK_THRESHOLD) {
                    if (x < minX) minX = x;
                    if (y < minY) minY = y;
                    if (x > maxX) maxX = x;
                    if (y > maxY) maxY = y;
                }
            }
        }

        float scaleX = originalWidth / (float) iw;
        float scaleY = originalHeight / (float) ih;

        if (maxX < 0) {
            // 兜底：未检测到任何黑色内容，按该扫描区间对应的完整 PDF 矩形返回
            float left = xStart * scaleX;
            float right = xEnd * scaleX;
            float top = (ih - yStart) * scaleY;
            float bottom = (ih - yEnd) * scaleY;
            return new PDRectangle(left, bottom, right - left, top - bottom);
        }

        float pdfLeft = minX * scaleX;
        float pdfRight = (maxX + 1) * scaleX;
        float pdfBottom = (ih - 1 - maxY) * scaleY;
        float pdfTop = (ih - minY) * scaleY;
        return new PDRectangle(pdfLeft, pdfBottom, pdfRight - pdfLeft, pdfTop - pdfBottom);
    }

    /**
     * 根据 {@link #WATERMARK_POSITION} / {@link #WATERMARK_WIDTH} / {@link #WATERMARK_HEIGHT}
     * 计算水印在像素坐标系（左上原点、Y 轴向下）下的矩形 {x1, y1, x2, y2}。
     * 若未配置或尺寸非法，返回 {@code null} 表示不需要过滤。
     */
    private static int[] computeWatermarkPixelRect(int iw, int ih,
                                                   float originalWidth, float originalHeight) {
        if (WATERMARK_POSITION == null || WATERMARK_POSITION == WatermarkPosition.NONE
                || WATERMARK_WIDTH <= 0 || WATERMARK_HEIGHT <= 0) {
            return null;
        }
        int wmW = Math.min(iw, Math.round(WATERMARK_WIDTH * iw / originalWidth));
        int wmH = Math.min(ih, Math.round(WATERMARK_HEIGHT * ih / originalHeight));
        int x1;
        int y1;
        switch (WATERMARK_POSITION) {
            case TOP_LEFT:
                x1 = 0;
                y1 = 0;
                break;
            case TOP_RIGHT:
                x1 = iw - wmW;
                y1 = 0;
                break;
            case BOTTOM_LEFT:
                x1 = 0;
                y1 = ih - wmH;
                break;
            case BOTTOM_RIGHT:
            default:
                x1 = iw - wmW;
                y1 = ih - wmH;
                break;
        }
        return new int[]{x1, y1, x1 + wmW, y1 + wmH};
    }

    /**
     * 把源页面坐标系下的 contentBox 等比缩放并居中到目标 A4 页面 (halfWidth × originalHeight)。
     * 缩放系数取宽高方向中较小者，保证 contentBox 整体可放入页面；再乘以 {@link #MARGIN_RATIO}
     * 作为安全边距。同一变换天然处理了"右半页内容需要左移"的情况，无需特别区分左右。
     */
    @SneakyThrows
    private static void applyFitTransform(PDDocument outputDoc, PDPage page, PDRectangle contentBox,
                                          float halfWidth, float originalHeight,
                                          float pageCenterX, float pageCenterY) {
        float bboxW = contentBox.getWidth();
        float bboxH = contentBox.getHeight();
        if (bboxW <= 0 || bboxH <= 0) {
            return;
        }
        float bboxCx = (contentBox.getLowerLeftX() + contentBox.getUpperRightX()) / 2f;
        float bboxCy = (contentBox.getLowerLeftY() + contentBox.getUpperRightY()) / 2f;

        float scale = Math.min(halfWidth / bboxW, originalHeight / bboxH) * MARGIN_RATIO;

        try (PDPageContentStream cs = new PDPageContentStream(outputDoc, page,
                PDPageContentStream.AppendMode.PREPEND, true)) {
            // p_new = p × scale × translate(pageCenter - scale × bboxCenter)
            // 把 contentBox 中心精确映射到 (pageCenterX, pageCenterY)
            cs.transform(Matrix.getTranslateInstance(
                    pageCenterX - scale * bboxCx,
                    pageCenterY - scale * bboxCy));
            cs.transform(Matrix.getScaleInstance(scale, scale));
        }
    }

    private static String describe(PDRectangle r) {
        return String.format("[x=%.1f,y=%.1f,w=%.1f,h=%.1f]",
                r.getLowerLeftX(), r.getLowerLeftY(), r.getWidth(), r.getHeight());
    }
}
