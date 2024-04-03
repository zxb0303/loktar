package com.loktar.web.azure;

import com.loktar.util.AzureUtil;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("azure")
public class AzureController {

    private final AzureUtil azureUtil;


    public AzureController(AzureUtil azureUtil) {
        this.azureUtil = azureUtil;
    }

    @RequestMapping("/wavToText.do")
    public void wavToText() {
        System.out.println(azureUtil.wavToText("F:/voice/", "20240402134417.wav"));
    }
}
