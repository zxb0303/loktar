package com.loktar.web.test;

import java.io.File;
import java.io.IOException;

public class MainTest {

    public static void main(String[] args) throws IOException {
        String pdfFolderPath = "F:/loktar/patent/2022/";
        File pdfFolder = new File(pdfFolderPath);
        File[] pdfFiles = pdfFolder.listFiles((dir, name) -> name.toLowerCase().endsWith(".pdf"));
        for (File pdfFile : pdfFiles) {
            String filename = pdfFile.getName().replace(".pdf","");
            System.out.println(filename);
        }
    }

}

