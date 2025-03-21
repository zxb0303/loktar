package com.loktar.web.cxy;

import lombok.SneakyThrows;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.multipdf.Splitter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class PDFMain {

    public static void main(String[] args) {
//        图片合并成pdf
//        pdfFolderPath是主文件夹目录，会将主文件目录下的子文件夹中的图片合并为子文件夹名.pdf保存在主文件下
//        -pdf
//          -a
//            --a1.jpg
//            --a2.jpg
        String pdfFolderPath = "F:/loktar/pdf/";
        picToPdf(pdfFolderPath, 300);

//        pdf拆分成图片
//        pdfFilePath是主文件夹目录，会将主文件目录下的pdf文件拆分为jpg图片并保存在同文件名的文件夹下
//        -pdf
//          -a.pdf
//          -b.pdf
//       String pdfFilePath = "F:/loktar/pdf/";
//       pdfTojpg(pdfFilePath);

//        pdf合并
//        pdfFilePath是主文件夹目录，会将主文件目录下的子文件夹中的pdf合并为子文件夹名.pdf保存在主文件下
//        -pdf
//          -a
//            --a1.pdf
//            --a2.pdf
//        String  pdfFolderPath = "F:/loktar/pdf/";
//        mergepdfs(pdfFolderPath);

        //pdf拆分
//        String pdfFilePath = "F:/loktar/pdf/1.pdf";
//        int pagesPerSplit = 2; // 每个拆分文件的页数
//        splitPdfByPages(pdfFilePath, pagesPerSplit);
    }

    public static void mergepdfs(String pdfFolderPath) {
        File mainFolder = new File(pdfFolderPath);
        File[] folders = mainFolder.listFiles();
        for (File folder : folders) {
            if (folder.isDirectory()) {
                System.out.println(folder.getName());
                File[] pdfFiles = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".pdf"));
                //Arrays.sort(pdfFiles, new ChineseFileNameComparatorUtil());

                if (pdfFiles == null || pdfFiles.length == 0) {
                    System.out.println("No PDF files found in the folder.");
                    return;
                }
                Arrays.sort(pdfFiles, Comparator.comparing(File::getName));

                PDFMergerUtility mergerUtility = new PDFMergerUtility();
                String outputFilePath = pdfFolderPath + File.separator + folder.getName() + ".pdf";
                mergerUtility.setDestinationFileName(outputFilePath);

                for (File pdfFile : pdfFiles) {
                    System.out.println(pdfFile.getName());
                    try {
                        PDDocument document = Loader.loadPDF(pdfFile);
                        mergerUtility.addSource(pdfFile);
                        document.close(); // Close the document if it's no longer needed
                    } catch (IOException e) {
                        System.err.println("Error processing file " + pdfFile.getName() + ": " + e.getMessage());
                    }
                }

                try {
                    mergerUtility.mergeDocuments(null);
                    System.out.println("Merge completed. Output file: " + outputFilePath);
                } catch (IOException e) {
                    System.err.println("Error merging documents: " + e.getMessage());
                }
            }
        }

    }

    @SneakyThrows
    public static void pdfTojpg(String pdfFolderPath) {
        File pdfFolder = new File(pdfFolderPath);
        File[] pdfFiles = pdfFolder.listFiles((dir, name) -> name.toLowerCase().endsWith(".pdf"));
        if (pdfFiles == null) {
            System.out.println("No PDF files found in the directory.");
            return;
        }
        for (File pdfFile : pdfFiles) {
            PDDocument document = Loader.loadPDF(pdfFile);
            PDFRenderer renderer = new PDFRenderer(document);
            String baseName = pdfFile.getName().replaceFirst("[.][^.]+$", "");
            File outputFolder = new File(pdfFile.getParent(), baseName);
            if (!outputFolder.exists()) {
                outputFolder.mkdirs();
            }
            int pageCount = document.getNumberOfPages();
            for (int i = 0; i < pageCount; i++) {
                BufferedImage image = renderer.renderImageWithDPI(i, 150);
                String fileName = String.format("%s/%03d.jpg", outputFolder.getPath(), i + 1);
                ImageIO.write(image, "JPEG", new File(fileName));
            }
            document.close();
        }
    }

    @SneakyThrows
    public static void picToPdf(String pdfFolderPath, int dpi) {
        File mainFolder = new File(pdfFolderPath);
        File[] folders = mainFolder.listFiles();
        for (File folder : folders) {
            if (folder.isDirectory()) {
                String foldername = folder.getName();
                System.out.println(foldername);
                File[] imageFiles = folder.listFiles();
                PDDocument document = new PDDocument();
                if (imageFiles != null) {
                    // 对文件按自定义规则排序: 先主图（无`-1`的），后副图（有`-1`的），并保持数字顺序
                    Arrays.sort(imageFiles, (f1, f2) -> {
                        String name1 = f1.getName().replaceFirst("\\.\\w+$", ""); // 去掉扩展名
                        String name2 = f2.getName().replaceFirst("\\.\\w+$", "");

                        // 提取文件名中的数字部分
                        String base1 = name1.replaceAll("-\\d+$", ""); // 去掉 `-1`
                        String base2 = name2.replaceAll("-\\d+$", "");

                        int result = base1.compareTo(base2); // 比较主文件名（数字部分）
                        if (result == 0) {
                            // 如果主文件名相同，将不带`-`的文件优先
                            boolean isSuffix1 = name1.matches(".*-\\d+$");
                            boolean isSuffix2 = name2.matches(".*-\\d+$");
                            return Boolean.compare(isSuffix1, isSuffix2);
                        }
                        return result;
                    });

                    for (File imageFile : imageFiles) {
                        if (imageFile.isFile()) {
                            System.out.println(imageFile.getName());
                            PDRectangle pageSize = PDRectangle.A4;
                            PDPage page = new PDPage(pageSize);
                            document.addPage(page);
                            PDImageXObject image = PDImageXObject.createFromFileByExtension(imageFile, document);
                            PDPageContentStream contentStream = new PDPageContentStream(document, page);
                            float imageWidth = image.getWidth();
                            float imageHeight = image.getHeight();
                            float scale = Math.min(pageSize.getWidth() / imageWidth, pageSize.getHeight() / imageHeight);
                            float x = (pageSize.getWidth() - imageWidth * scale) / 2;
                            float y = (pageSize.getHeight() - imageHeight * scale) / 2;
                            contentStream.drawImage(image, x, y, imageWidth * scale, imageHeight * scale);
                            contentStream.close();
                        }
                    }
                }
                String filename = pdfFolderPath + File.separator + foldername + ".pdf";
                document.save(filename);
                document.close();
            }
        }
    }


    public static void splitPdfByPages(String pdfFilePath, int pagesPerSplit) {
        File pdfFile = new File(pdfFilePath);
        if (!pdfFile.exists() || !pdfFile.isFile()) {
            System.out.println("PDF file not found.");
            return;
        }

        try {
            PDDocument document = Loader.loadPDF(pdfFile);
            Splitter splitter = new Splitter();
            splitter.setSplitAtPage(pagesPerSplit);

            List<PDDocument> splitDocuments = splitter.split(document);
            String baseName = pdfFile.getName().replaceFirst("[.][^.]+$", "");

            int partNumber = 1;
            for (PDDocument splitDoc : splitDocuments) {
                String outputFileName = pdfFile.getParent() + File.separator + baseName + "_part" + partNumber + ".pdf";
                splitDoc.save(outputFileName);
                splitDoc.close();
                partNumber++;
            }

            document.close();
            System.out.println("PDF split completed.");
        } catch (IOException e) {
            System.err.println("Error splitting PDF: " + e.getMessage());
        }
    }


    public static File[] sortFiles(File[] files) {
        if (files == null) {
            return null;
        }
        Arrays.sort(files, (f1, f2) -> {
            String filename1 = f1.getName().substring(0, f1.getName().lastIndexOf('.'));
            String filename2 = f2.getName().substring(0, f2.getName().lastIndexOf('.'));
            String[] split1 = filename1.split("-(?=[0-9]+$)", 2);
            String[] split2 = filename2.split("-(?=[0-9]+$)", 2);
            int number1 = Integer.parseInt(split1[0]);
            int number2 = Integer.parseInt(split2[0]);
            if (number1 != number2) {
                return number1 - number2;
            }
            if (split1.length == 2 && split2.length == 2) {
                return Integer.parseInt(split1[1]) - Integer.parseInt(split2[1]);
            }
            return split1.length - split2.length;
        });
        return files;
    }
}
