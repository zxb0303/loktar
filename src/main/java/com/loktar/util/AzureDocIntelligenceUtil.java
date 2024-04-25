package com.loktar.util;

import com.azure.ai.documentintelligence.DocumentIntelligenceClient;
import com.azure.ai.documentintelligence.DocumentIntelligenceClientBuilder;
import com.azure.ai.documentintelligence.models.AnalyzeDocumentRequest;
import com.azure.ai.documentintelligence.models.AnalyzeResult;
import com.azure.ai.documentintelligence.models.AnalyzeResultOperation;
import com.azure.core.credential.AzureKeyCredential;
import com.azure.core.util.polling.SyncPoller;
import com.loktar.conf.LokTarConfig;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Files;

@Component
public class AzureDocIntelligenceUtil {
    private static DocumentIntelligenceClient client;

    public AzureDocIntelligenceUtil(LokTarConfig lokTarConfig) {
        client = new DocumentIntelligenceClientBuilder()
                .credential(new AzureKeyCredential(lokTarConfig.getAzure().getDocIntelligenceKey()))
                .endpoint(lokTarConfig.getAzure().getDocIntelligenceEndpoint())
                .buildClient();
    }

    @SneakyThrows
    public static AnalyzeResult getAnalyze(String modelId,String filepath) {
        File selectionMarkDocument = new File(filepath);
        SyncPoller<AnalyzeResultOperation, AnalyzeResultOperation> analyzeLayoutResultPoller =
                client.beginAnalyzeDocument(modelId, null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        new AnalyzeDocumentRequest().setBase64Source(Files.readAllBytes(selectionMarkDocument.toPath())));
        return analyzeLayoutResultPoller.getFinalResult().getAnalyzeResult();
    }
}
