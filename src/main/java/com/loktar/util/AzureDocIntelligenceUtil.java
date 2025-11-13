package com.loktar.util;

import com.azure.ai.documentintelligence.DocumentIntelligenceClient;
import com.azure.ai.documentintelligence.DocumentIntelligenceClientBuilder;
import com.azure.ai.documentintelligence.models.AnalyzeDocumentOptions;
import com.azure.ai.documentintelligence.models.AnalyzeOperationDetails;
import com.azure.ai.documentintelligence.models.AnalyzeResult;
import com.azure.core.credential.AzureKeyCredential;
import com.azure.core.util.polling.SyncPoller;
import com.loktar.conf.LokTarConfig;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Files;
import java.util.Collections;

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
    public static AnalyzeResult getAnalyze(String modelId, String filepath, String pages) {
        File file = new File(filepath);
        byte[] content = Files.readAllBytes(file.toPath());

        AnalyzeDocumentOptions options = new AnalyzeDocumentOptions(content);
        if (pages != null && !pages.isBlank()) {
        // 示例："1-3,5,7-9"；也可只传单页："1"
            options.setPages(Collections.singletonList(pages));
        }

        SyncPoller<AnalyzeOperationDetails, AnalyzeResult> poller =
                client.beginAnalyzeDocument(modelId, options);

        return poller.getFinalResult();
    }
}