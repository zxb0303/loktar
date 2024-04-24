package com.loktar.util;

import lombok.SneakyThrows;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.io.RandomAccessReadBuffer;
import org.apache.pdfbox.multipdf.Splitter;
import org.apache.pdfbox.pdmodel.PDDocument;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class PDFBoxUtil {

    @SneakyThrows
    public static String splitPDFFromUrl(String pdfUrl,String pdfPath){
        try {
            InputStream input = new URI(pdfUrl).toURL().openStream();
            RandomAccessReadBuffer randomAccessBuffer = new RandomAccessReadBuffer(input);
            PDDocument document = Loader.loadPDF(randomAccessBuffer);
            Splitter splitter = new Splitter();
            List<PDDocument> Pages = splitter.split(document);
            Iterator<PDDocument> iterator = Pages.listIterator();
            int i = 0;
            String tempFold = UUID.randomUUID().toString();
            File fileFold = new File(pdfPath+tempFold);
            fileFold.mkdir();
            while(iterator.hasNext()) {
                PDDocument pd = iterator.next();
                String filename = String.format("%04d", i + 1);
                pd.save(fileFold.getPath()+"/"+filename+".pdf");
                i=i+1;
            }
            document.close();
            return tempFold;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
