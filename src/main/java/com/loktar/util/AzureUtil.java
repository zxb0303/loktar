package com.loktar.util;

import com.loktar.conf.LokTarConfig;
import com.loktar.conf.LokTarConstant;
import com.microsoft.cognitiveservices.speech.*;
import com.microsoft.cognitiveservices.speech.audio.AudioConfig;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

@Component
public class AzureUtil {

    public static String VOICE_REGION = "eastasia";
    public static String LANGUAGE = "zh-CN";
    public static String DEFAULT_VOICE_NAME = "zh-CN-XiaoyiNeural";
    private final LokTarConfig lokTarConfig;

    public AzureUtil(LokTarConfig lokTarConfig) {
        this.lokTarConfig = lokTarConfig;
    }

    @SneakyThrows
    public  void textToWav(String voicePath, String filename, String text) {
        // 初始化 SpeechConfig
        SpeechConfig config = SpeechConfig.fromSubscription(lokTarConfig.azureVoiceKey, VOICE_REGION);
        config.setSpeechSynthesisVoiceName(DEFAULT_VOICE_NAME);
        String audioFilePath = voicePath + filename;
        //System.out.println("textToWav audioFilePath:"+audioFilePath);
        // 指定输出音频的配置
        AudioConfig audioConfig = AudioConfig.fromWavFileOutput(audioFilePath);
        // 使用指定的 AudioConfig 创建语音合成器
        SpeechSynthesizer synthesizer = new SpeechSynthesizer(config, audioConfig);
        // 开始语音合成
        SpeechSynthesisResult result = synthesizer.SpeakTextAsync(text).get();
        // 检查结果
        if (result.getReason() == ResultReason.SynthesizingAudioCompleted) {
            //System.out.println("Audio written to file: " + voicePath + filename + AzureConstant.SUFFIX_WAV);
        } else if (result.getReason() == ResultReason.Canceled) {
            SpeechSynthesisCancellationDetails cancellation = SpeechSynthesisCancellationDetails.fromResult(result);
            System.out.println("CANCELED: Reason=" + cancellation.getReason());
            System.out.println("CANCELED: ErrorCode=" + cancellation.getErrorCode());
            System.out.println("CANCELED: ErrorDetails=" + cancellation.getErrorDetails());
            System.out.println("CANCELED: Did you update the subscription info?");
        }
        //System.out.println(result.getReason());
        result.close();
        synthesizer.close();

    }

    @SneakyThrows
    public String wavToText(String voicePath,String filename) {
        // Build the speech configuration using the subscription key and service region.
        SpeechConfig config = SpeechConfig.fromSubscription(lokTarConfig.azureVoiceKey, VOICE_REGION);
        config.setSpeechRecognitionLanguage(LANGUAGE);
        // Specify the audio file to be recognized.
        String audioFilePath = voicePath + filename.replace(LokTarConstant.VOICE_SUFFIX_AMR, LokTarConstant.VOICE_SUFFIX_WAV);
        //System.out.println("wavToText audioFilePath:"+audioFilePath);
        AudioConfig audioConfig = AudioConfig.fromWavFileInput(audioFilePath);

        // Create a speech recognizer using the configuration and audio input.
        SpeechRecognizer recognizer = new SpeechRecognizer(config, audioConfig);

        // Perform the speech recognition.
        SpeechRecognitionResult result = recognizer.recognizeOnceAsync().get();

        // Check the result and return the recognized text.
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
    public static void main(String[] args) {
//        String filename = "test";
//        String text = "你好，这是一个测试文本。";
//        String voicePath = "F:/voice/";
//        String voiceName = "zh-CN-XiaochenNeural";
//        textToWav(filename, text, voicePath, voiceName);
//        String filename = "test.amr";
//        String voicePath = "F:/voice/";
//        System.out.println(wavToText(filename,voicePath));;
    }

}
