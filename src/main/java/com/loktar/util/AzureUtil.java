package com.loktar.util;

import com.loktar.conf.LokTarPrivateConstant;
import com.microsoft.cognitiveservices.speech.*;
import com.microsoft.cognitiveservices.speech.audio.AudioConfig;
import lombok.SneakyThrows;
import org.apache.commons.lang3.ObjectUtils;

public class AzureUtil {

    public static String VOICE_REGION = "eastasia";
    public static String SUFFIX_WAV = ".wav";
    public static String SUFFIX_AMR = ".amr";
    public static String LANGUAGE = "zh-CN";
    public static String DEFAULT_VOICE_NAME = "zh-CN-XiaochenNeural";

    @SneakyThrows
    public static void textToWav(String filename, String text, String voicePath, String voiceName) {
        // 初始化 SpeechConfig
        SpeechConfig config = SpeechConfig.fromSubscription(LokTarPrivateConstant.AZURE_VOICE_KEY, VOICE_REGION);
        if (ObjectUtils.isEmpty(voiceName)) {
            voiceName = DEFAULT_VOICE_NAME;
        }
        config.setSpeechSynthesisVoiceName(voiceName);
        // 指定输出音频的配置
        AudioConfig audioConfig = AudioConfig.fromWavFileOutput(voicePath + filename + SUFFIX_WAV);
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
    public static String wavToText(String filename, String voicePath) {
        // Build the speech configuration using the subscription key and service region.
        SpeechConfig config = SpeechConfig.fromSubscription(LokTarPrivateConstant.AZURE_VOICE_KEY, VOICE_REGION);
        config.setSpeechRecognitionLanguage(LANGUAGE);
        // Specify the audio file to be recognized.
        String audioFilePath = voicePath + filename + SUFFIX_WAV;
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
        String filename = "test";
        String voicePath = "F:/voice/";
        System.out.println(wavToText(filename,voicePath));;
    }

}
