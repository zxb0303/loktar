package com.loktar.web.azure;

import com.azure.ai.documentintelligence.models.AnalyzeResult;
import com.azure.ai.documentintelligence.models.DocumentParagraph;
import com.loktar.util.AzureDocIntelligenceUtil;
import com.loktar.util.AzureVoiceUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("azure")
public class AzureController {

    private final AzureVoiceUtil azureVoiceUtil;


    public AzureController(AzureVoiceUtil azureVoiceUtil) {
        this.azureVoiceUtil = azureVoiceUtil;
    }

    @GetMapping("/wavToText.do")
    public void wavToText() {
        System.out.println(azureVoiceUtil.wavToText("F:/voice/", "20240402134417.wav"));
    }

    @GetMapping("/analyze.do")
    public void test(){
        String jpgfilepath = "F:/doc/0a8454a7f004be1668df8291aa0a0c2d.pdf";
        AnalyzeResult analyzeLayoutResult = AzureDocIntelligenceUtil.getAnalyze("prebuilt-layout",jpgfilepath,"2");
        System.out.println(analyzeLayoutResult.toString());
        List<DocumentParagraph> documentParagraphs = analyzeLayoutResult.getParagraphs();
        for (DocumentParagraph documentParagraph : documentParagraphs){
            if(documentParagraph.getContent().contains("7.基于上述结论性意见,审查员认为")){
                System.out.println(documentParagraph.getContent());
            }
        }


    }


}
