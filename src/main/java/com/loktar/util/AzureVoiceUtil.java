package com.loktar.util;


import lombok.extern.slf4j.Slf4j;
import com.loktar.conf.LokTarConfig;
import com.loktar.conf.LokTarConstant;
import com.microsoft.cognitiveservices.speech.*;
import com.microsoft.cognitiveservices.speech.audio.AudioConfig;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AzureVoiceUtil {

    public final static String LANGUAGE = "zh-CN";
    public final static String DEFAULT_VOICE_NAME = "zh-CN-XiaoyiNeural";
    private static SpeechConfig config;

    public AzureVoiceUtil(LokTarConfig lokTarConfig) {
        config = SpeechConfig.fromSubscription(lokTarConfig.getAzure().getVoiceKey(), lokTarConfig.getAzure().getVoiceRegion());
        config.setSpeechSynthesisVoiceName(DEFAULT_VOICE_NAME);
        config.setSpeechRecognitionLanguage(LANGUAGE);
    }

    @SneakyThrows
    public void textToWav(String voicePath, String filename, String text) {
        String audioFilePath = voicePath + filename;
        AudioConfig audioConfig = AudioConfig.fromWavFileOutput(audioFilePath);
        SpeechSynthesizer synthesizer = new SpeechSynthesizer(config, audioConfig);
        SpeechSynthesisResult result = synthesizer.SpeakTextAsync(text).get();
        if (result.getReason() == ResultReason.Canceled) {
            SpeechSynthesisCancellationDetails cancellation = SpeechSynthesisCancellationDetails.fromResult(result);
            log.info("{}", "CANCELED: Reason=" + cancellation.getReason());
            log.info("{}", "CANCELED: ErrorCode=" + cancellation.getErrorCode());
            log.info("{}", "CANCELED: ErrorDetails=" + cancellation.getErrorDetails());
            log.info("{}", "CANCELED: Did you update the subscription info?");
        }
        result.close();
        synthesizer.close();

    }

    @SneakyThrows
    public String wavToText(String voicePath, String filename) {
        String audioFilePath = voicePath + filename.replace(LokTarConstant.VOICE_SUFFIX_AMR, LokTarConstant.VOICE_SUFFIX_WAV);
        AudioConfig audioConfig = AudioConfig.fromWavFileInput(audioFilePath);
        SpeechRecognizer recognizer = new SpeechRecognizer(config, audioConfig);
        SpeechRecognitionResult result = recognizer.recognizeOnceAsync().get();
        if (result.getReason() == ResultReason.RecognizedSpeech) {
            return result.getText();
        } else if (result.getReason() == ResultReason.NoMatch) {
            throw new Exception("No speech could be recognized.");
        } else if (result.getReason() == ResultReason.Canceled) {
            CancellationDetails cancellation = CancellationDetails.fromResult(result);
            throw new Exception("Speech recognition canceled: " + cancellation.getReason());
        }
        return null;
    }
}
