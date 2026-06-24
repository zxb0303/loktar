package com.loktar.util;

import lombok.SneakyThrows;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.io.RandomAccessReadBuffer;
import org.apache.pdfbox.multipdf.Splitter;
import org.apache.pdfbox.pdmodel.PDDocument;

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.util.List;
import java.util.UUID;

public class PDFBoxUtil {

    @SneakyThrows
    public static String splitPDFFromUrl(String pdfUrl, String pdfPath) {
        String tempFold = UUID.randomUUID().toString();
        File fileFold = new File(pdfPath + tempFold);
        fileFold.mkdir();
        try (InputStream input = new URI(pdfUrl).toURL().openStream();
             RandomAccessReadBuffer randomAccessBuffer = new RandomAccessReadBuffer(input);
             PDDocument document = Loader.loadPDF(randomAccessBuffer)) {
            Splitter splitter = new Splitter();
            List<PDDocument> Pages = splitter.split(document);
            for (int i = 0; i < Pages.size(); i++) {
                try (PDDocument pd = Pages.get(i)) {
                    String filename = String.format("%04d", i + 1);
                    pd.save(fileFold.getPath() + "/" + filename + ".pdf");
                }
            }
        }
        return tempFold;
    }

}
