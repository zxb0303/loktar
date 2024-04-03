///*
//package com.loktar.web.test;
//
//import com.azure.ai.formrecognizer.*;
//
//import com.azure.ai.formrecognizer.documentanalysis.models.*;
//import com.azure.ai.formrecognizer.documentanalysis.DocumentAnalysisClient;
//import com.azure.ai.formrecognizer.documentanalysis.DocumentAnalysisClientBuilder;
//
//import com.azure.core.credential.AzureKeyCredential;
//import com.azure.core.util.polling.SyncPoller;
//
//import java.io.IOException;
//import java.util.List;
//import java.util.Arrays;
//import java.time.LocalDate;
//import java.util.Map;
//import java.util.stream.Collectors;
//
//public class MainTest {
//
//    // set `<your-endpoint>` and `<your-key>` variables with the values from the Azure portal
//    private static final String endpoint = "<your-endpoint>";
//    private static final String key = "<your-key>";
//
//    public static void main(String[] args) {
//
//        // create your `DocumentAnalysisClient` instance and `AzureKeyCredential` variable
//        DocumentAnalysisClient client = new DocumentAnalysisClientBuilder()
//                .credential(new AzureKeyCredential(key))
//                .endpoint(endpoint)
//                .buildClient();
//
//        // sample document
//        String documentUrl = "https://raw.githubusercontent.com/Azure-Samples/cognitive-services-REST-api-samples/master/curl/form-recognizer/sample-layout.pdf";
//        String modelId = "prebuilt-layout";
//
//        SyncPoller <OperationResult, AnalyzeResult > analyzeLayoutResultPoller =
//                client.beginAnalyzeDocumentFromUrl(modelId, documentUrl);
//
//        AnalyzeResult analyzeLayoutResult = analyzeLayoutResultPoller.getFinalResult();
//
//        // pages
//        analyzeLayoutResult.getPages().forEach(documentPage -> {
//            System.out.printf("Page has width: %.2f and height: %.2f, measured with unit: %s%n",
//                    documentPage.getWidth(),
//                    documentPage.getHeight(),
//                    documentPage.getUnit());
//
//            // lines
//            documentPage.getLines().forEach(documentLine ->
//                    System.out.printf("Line %s is within a bounding polygon %s.%n",
//                            documentLine.getContent(),
//                            documentLine.getBoundingPolygon().toString()));
//
//            // words
//            documentPage.getWords().forEach(documentWord ->
//                    System.out.printf("Word '%s' has a confidence score of %.2f%n",
//                            documentWord.getContent(),
//                            documentWord.getConfidence()));
//
//            // selection marks
//            documentPage.getSelectionMarks().forEach(documentSelectionMark ->
//                    System.out.printf("Selection mark is %s and is within a bounding polygon %s with confidence %.2f.%n",
//                            documentSelectionMark.getState().toString(),
//                            documentSelectionMark.getBoundingPolygon().toString(),
//                            documentSelectionMark.getConfidence()));
//        });
//
//        // tables
//        List < DocumentTable > tables = analyzeLayoutResult.getTables();
//        for (int i = 0; i < tables.size(); i++) {
//            DocumentTable documentTable = tables.get(i);
//            System.out.printf("Table %d has %d rows and %d columns.%n", i, documentTable.getRowCount(),
//                    documentTable.getColumnCount());
//            documentTable.getCells().forEach(documentTableCell -> {
//                System.out.printf("Cell '%s', has row index %d and column index %d.%n", documentTableCell.getContent(),
//                        documentTableCell.getRowIndex(), documentTableCell.getColumnIndex());
//            });
//            System.out.println();
//        }
//    }
//    // Utility function to get the bounding polygon coordinates
//    private static String getBoundingCoordinates(List < Point > boundingPolygon) {
//        return boundingPolygon.stream().map(point -> String.format("[%.2f, %.2f]", point.getX(),
//                point.getY())).collect(Collectors.joining(", "));
//    }
//
//
//}
//*/
