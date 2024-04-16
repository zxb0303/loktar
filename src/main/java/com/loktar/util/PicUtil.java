package com.loktar.util;

import com.loktar.conf.LokTarConstant;
import lombok.SneakyThrows;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class PicUtil {
    @SneakyThrows
    public static void converPNGtoJPG(String pngFileNamePath, String jpgFileNamePath) {
        File input = new File(pngFileNamePath);
        BufferedImage image = ImageIO.read(input);
        File output = new File(jpgFileNamePath);
        Path outputPath = Paths.get(jpgFileNamePath);
        Files.createDirectories(outputPath.getParent());
        ImageIO.write(image, LokTarConstant.PIC_TYPE_JPG, output);
    }

    @SneakyThrows
    public static void mergePNGstoJPG(String pngFilesPath,String jpgFileNamePath){
        File dir = new File(pngFilesPath);
        String[] imageFiles = dir.list(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(LokTarConstant.PIC_SUFFIX_PNG);
            }
        });
        if (imageFiles == null || imageFiles.length == 0) {
            System.out.println("No PNG files found in the directory.");
            return;
        }
        Arrays.sort(imageFiles);
        File firstImageFile = new File(dir, imageFiles[0]);
        BufferedImage firstImage = ImageIO.read(firstImageFile);
        int width = firstImage.getWidth();
        int height = firstImage.getHeight();
        BufferedImage combinedImage = new BufferedImage(width, height * imageFiles.length, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = combinedImage.createGraphics();
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, combinedImage.getWidth(), combinedImage.getHeight());
        int currentHeight = 0;
        for (String file : imageFiles) {
            BufferedImage image = ImageIO.read(new File(dir, file));
            g2d.drawImage(image, 0, currentHeight, null);
            currentHeight += height;
        }
        g2d.dispose();
        Path outputPath = Paths.get(jpgFileNamePath);
        Files.createDirectories(outputPath.getParent());
        ImageIO.write(combinedImage, LokTarConstant.PIC_TYPE_JPG, new File(jpgFileNamePath));
    }
}
