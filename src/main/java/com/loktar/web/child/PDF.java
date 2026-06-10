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

    /**
     * 自适应放大后保留的边距比例：1.0 表示让黑色内容完全填满 A4 页面，
     * 小于 1 则四周留出对应比例的空白。可按打印机/装订需求自行调整。
     */
    private static final float MARGIN_RATIO = 0.98f;

    /**
     * 用于"找黑边"的内容检测渲染分辨率（DPI）。
     * 值越大检测越精确但越耗时；试卷类一般 100 DPI 足够。
     */
    private static final int DETECT_DPI = 100;

    /**
     * 灰度阈值：像素灰度 ≤ 此值视为"黑色内容"，> 此值视为空白。
     * 试卷扫描件偏灰时可适当调高（如 230），干净电子稿可调低（如 200）。
     */
    private static final int DARK_THRESHOLD = 220;

    /**
     * 水印所在角落。若源 PDF 在某个角落带有 LOGO / 二维码 / 文本水印，
     * 设置后该区域内的像素在"黑色范围"检测时会被忽略，避免拉大包围盒导致缩放系数偏小。
     * 设为 {@link WatermarkPosition#NONE} 关闭水印过滤。
     */
    private static final WatermarkPosition WATERMARK_POSITION = WatermarkPosition.BOTTOM_RIGHT;

    /**
     * 水印宽度（PDF 点，1 pt ≈ 0.353 mm；A4 宽约 595pt，A3 横向宽约 1190pt）。
     * 取略大于真实水印的值更稳妥。
     */
    private static final float WATERMARK_WIDTH = 140f;

    /**
     * 水印高度（PDF 点）。
     */
    private static final float WATERMARK_HEIGHT = 70f;

    /**
     * 水印所在角落。
     */
    public enum WatermarkPosition {
        NONE, TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT
    }

    @SneakyThrows
    public static void main(String[] args) {
        String sourcePath = "F:/周末作业0522(1).pdf";
        String outputPath = sourcePath.replace(".pdf", "_split.pdf");
        splitPdfHorizontal(sourcePath, outputPath);
    }

    /**
     * 将PDF每页横向拆分为两页（适用于A3试卷拆分为A4打印）。
     * <p>
     * 对每一半内容会先识别黑色像素的最小包围盒（即试卷上文字/线条的真实范围），
     * 然后按比例放大并居中到 A4 页面，多余的白边自动去掉，
     * 内容溢出 A4 范围的部分也会被自动裁切。
     *
     * @param sourcePath 源PDF文件路径
     * @param outputPath 输出PDF文件路径
     */
    @SneakyThrows
    public static void splitPdfHorizontal(String sourcePath, String outputPath) {
        File sourceFile = new File(sourcePath);

        try (PDDocument sourceDoc = Loader.loadPDF(sourceFile);
             PDDocument outputDoc = new PDDocument()) {

            PDFRenderer renderer = new PDFRenderer(sourceDoc);
            int totalPages = sourceDoc.getNumberOfPages();
            System.out.println("源PDF共 " + totalPages + " 页，边距比例: " + MARGIN_RATIO
                    + "，检测DPI: " + DETECT_DPI + "，黑色阈值: " + DARK_THRESHOLD);

            for (int i = 0; i < totalPages; i++) {
                PDPage sourcePage = sourceDoc.getPage(i);
                PDRectangle mediaBox = sourcePage.getMediaBox();
                float originalWidth = mediaBox.getWidth();
                float originalHeight = mediaBox.getHeight();
                float halfWidth = originalWidth / 2f;
                float pageCenterX = halfWidth / 2f;
                float pageCenterY = originalHeight / 2f;

                // 渲染整页用于扫描黑色像素范围
                BufferedImage img = renderer.renderImageWithDPI(i, DETECT_DPI);
                int iw = img.getWidth();
                int ih = img.getHeight();
                int halfIw = iw / 2;

                // 计算水印在像素坐标系下的矩形（左上 (x1,y1)，右下 (x2,y2)）；null 表示无需过滤
                int[] watermarkPxRect = computeWatermarkPixelRect(iw, ih, originalWidth, originalHeight);

                PDRectangle leftBbox = detectContentBox(img, 0, halfIw, originalWidth, originalHeight, watermarkPxRect);
                PDRectangle rightBbox = detectContentBox(img, halfIw, iw, originalWidth, originalHeight, watermarkPxRect);

                System.out.println("第 " + (i + 1) + " 页 左半黑色范围: " + describe(leftBbox)
                        + "，右半黑色范围: " + describe(rightBbox));

                // 左半页
                PDPage leftPage = outputDoc.importPage(sourcePage);
                leftPage.setMediaBox(new PDRectangle(0, 0, halfWidth, originalHeight));
                leftPage.setCropBox(new PDRectangle(0, 0, halfWidth, originalHeight));
                applyFitTransform(outputDoc, leftPage, leftBbox, halfWidth, originalHeight,
                        pageCenterX, pageCenterY);

                // 右半页
                PDPage rightPage = outputDoc.importPage(sourcePage);
                rightPage.setMediaBox(new PDRectangle(0, 0, halfWidth, originalHeight));
                rightPage.setCropBox(new PDRectangle(0, 0, halfWidth, originalHeight));
                applyFitTransform(outputDoc, rightPage, rightBbox, halfWidth, originalHeight,
                        pageCenterX, pageCenterY);
            }

            outputDoc.save(outputPath);
            System.out.println("拆分完成，输出文件: " + outputPath
                    + "，共 " + outputDoc.getNumberOfPages() + " 页");
        }
    }

    /**
     * 扫描像素区间 [xStart, xEnd) 内所有"黑色"像素（灰度 ≤ {@link #DARK_THRESHOLD}），
     * 计算其最小包围盒，并把像素坐标转换为 PDF 用户坐标系（注意 Y 轴翻转）。
     * <p>
     * 若该区间没有任何黑色像素，则返回该像素区间对应的完整 PDF 矩形作为兜底，避免除零。
     *
     * @param watermarkPxRect 形如 {x1, y1, x2, y2} 的水印像素矩形（含左上、不含右下）；
     *                        命中该矩形的像素会被跳过；传入 {@code null} 表示不过滤。
     */
    private static PDRectangle detectContentBox(BufferedImage img, int xStart, int xEnd,
                                                float originalWidth, float originalHeight,
                                                int[] watermarkPxRect) {
        int iw = img.getWidth();
        int ih = img.getHeight();
        int minX = xEnd;
        int minY = ih;
        int maxX = -1;
        int maxY = -1;
        for (int y = 0; y < ih; y++) {
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
            // 兜底：未检测到任何黑色内容，按该半页的完整范围返回
            float left = xStart * scaleX;
            float right = xEnd * scaleX;
            return new PDRectangle(left, 0, right - left, originalHeight);
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
