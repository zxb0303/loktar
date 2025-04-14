package com.loktar.web.azure;

import com.azure.ai.documentintelligence.models.AnalyzeResult;
import com.azure.ai.documentintelligence.models.DocumentParagraph;
import com.loktar.conf.LokTarConfig;
import com.loktar.util.AzureDocIntelligenceUtil;
import com.loktar.util.AzureVoiceUtil;
import com.microsoft.cognitiveservices.speech.*;
import com.microsoft.cognitiveservices.speech.audio.AudioConfig;
import com.microsoft.cognitiveservices.speech.audio.AudioOutputStream;
import com.microsoft.cognitiveservices.speech.audio.PullAudioOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("azure")
public class AzureController {

    private final AzureVoiceUtil azureVoiceUtil;
    private final LokTarConfig lokTarConfig;
    private static SpeechConfig config;


    public AzureController(AzureVoiceUtil azureVoiceUtil, LokTarConfig lokTarConfig) {
        this.azureVoiceUtil = azureVoiceUtil;
        this.lokTarConfig = lokTarConfig;
    }

    @GetMapping("/wavToText.do")
    public void wavToText() {
        System.out.println(azureVoiceUtil.wavToText("F:/voice/", "20240402134417.wav"));
    }

    @GetMapping("/analyze.do")
    public void test() {
        String jpgfilepath = "F:/doc/0a8454a7f004be1668df8291aa0a0c2d.pdf";
        AnalyzeResult analyzeLayoutResult = AzureDocIntelligenceUtil.getAnalyze("prebuilt-layout", jpgfilepath, "2");
        System.out.println(analyzeLayoutResult.toString());
        List<DocumentParagraph> documentParagraphs = analyzeLayoutResult.getParagraphs();
        for (DocumentParagraph documentParagraph : documentParagraphs) {
            if (documentParagraph.getContent().contains("7.基于上述结论性意见,审查员认为")) {
                System.out.println(documentParagraph.getContent());
            }
        }
    }
    @SneakyThrows
    @GetMapping("/xiaozhiTTS.do")
    public void xiaozhiTTS(String text, String voiceName, HttpServletResponse response) {
        config = SpeechConfig.fromSubscription(lokTarConfig.getAzure().getVoiceKey(), lokTarConfig.getAzure().getVoiceRegion());
        config.setSpeechRecognitionLanguage(AzureVoiceUtil.LANGUAGE);
        config.setSpeechSynthesisVoiceName(voiceName);
        try (PullAudioOutputStream pullStream = AudioOutputStream.createPullStream();
             AudioConfig audioConfig = AudioConfig.fromStreamOutput(pullStream);
             SpeechSynthesizer synthesizer = new SpeechSynthesizer(config, audioConfig)) {
            SpeechSynthesisResult result = synthesizer.SpeakTextAsync(text).get();
            if (result.getReason() == ResultReason.SynthesizingAudioCompleted) {
                byte[] audioData = result.getAudioData();
                response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
                response.setHeader("Content-Disposition", "attachment; filename=\"tts.wav\"");
                response.setContentLength(audioData.length);
                response.getOutputStream().write(audioData);
                response.getOutputStream().flush();
                System.out.println("合成完成:"+text);
            } else {
                SpeechSynthesisCancellationDetails cancellation =
                        SpeechSynthesisCancellationDetails.fromResult(result);
                response.setCharacterEncoding(StandardCharsets.UTF_8.name());
                response.setContentType("text/plain;charset=UTF-8");
                response.getWriter().write("合成失败：" + cancellation.getErrorDetails());
            }
            result.close();
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.setContentType("text/plain;charset=UTF-8");
            response.getWriter().write("服务器异常：" + e.getMessage());
        } finally {
            config.setSpeechSynthesisVoiceName(AzureVoiceUtil.DEFAULT_VOICE_NAME);
        }
    }
}
