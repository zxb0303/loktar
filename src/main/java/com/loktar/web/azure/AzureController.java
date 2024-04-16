package com.loktar.web.azure;

import com.azure.ai.documentintelligence.models.AnalyzeResult;
import com.loktar.conf.LokTarConstant;
import com.loktar.util.AzureDocIntelligenceUtil;
import com.loktar.util.AzureVoiceUtil;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("azure")
public class AzureController {

    private final AzureVoiceUtil azureVoiceUtil;


    public AzureController(AzureVoiceUtil azureVoiceUtil) {
        this.azureVoiceUtil = azureVoiceUtil;
    }

    @RequestMapping("/wavToText.do")
    public void wavToText() {
        System.out.println(azureVoiceUtil.wavToText("F:/voice/", "20240402134417.wav"));
    }

    @RequestMapping("/test.do")
    public void test(){
        String jpgfilepath = "F:\\newhouse\\conversion\\acd7ec7e03e64da1a0427a5f97de5b55\\2019000073\\66146\\0013.jpg";
        AnalyzeResult analyzeLayoutResult = AzureDocIntelligenceUtil.getAnalyze(LokTarConstant.AZURE_DOCINTELLIGENCE_MODEL_ID,jpgfilepath);
        System.out.println(1);
    }
}
