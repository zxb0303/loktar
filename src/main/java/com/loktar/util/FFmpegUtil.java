package com.loktar.util;

import com.loktar.conf.LokTarConstant;
import lombok.SneakyThrows;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
@Component
public class FFmpegUtil {

    private static String env;

    @Value("${spring.profiles.active}")
    public void setEnv(String env) {
        FFmpegUtil.env = env;
    }

    public static void convertWavToAmr(String voicePath, String filename) {
        if (!env.equals(LokTarConstant.ENV_PRO)) {
            convertWavToAmrDev(voicePath, filename);
        }
        convertWavToAmrPro(voicePath, filename);
    }

    public static void convertAmrToWav(String voicePath, String filename) {
        if (!env.equals(LokTarConstant.ENV_PRO)) {
            convertAmrToWavDev(voicePath, filename);
        }
        convertAmrToWavPro(voicePath, filename);
    }


    public static void convertWavToAmrPro(String voicePath, String wavFilename) {
        //ffmpeg -y -loglevel error -i /fun/voice/111.wav -c:a libopencore_amrnb -ar 8000 -ac 1 /fun/voice/111.amr
        String command = "ffmpeg -y -loglevel error -i " + voicePath + wavFilename + " -c:a libopencore_amrnb -ar 8000 -ac 1 " + voicePath + wavFilename.replace(LokTarConstant.VOICE_SUFFIX_WAV, LokTarConstant.VOICE_SUFFIX_AMR);
        DockerEngineApiUtil.containerExec(DockerEngineApiUtil.FFMPEG_COMTAINER_NAME, command);
    }

    public static void convertAmrToWavPro(String voicePath, String amrFilename) {
        //String command = "ffmpeg -y -loglevel error -i /fun/voice/222.amr -ac 1 -ar 16000 -b:a 256k /fun/voice/222.wav";
        String command = "ffmpeg -y -loglevel error -i " + voicePath + amrFilename + " -ac 1 -ar 16000 -b:a 256k " + voicePath + amrFilename.replace(LokTarConstant.VOICE_SUFFIX_AMR, LokTarConstant.VOICE_SUFFIX_WAV);
        DockerEngineApiUtil.containerExec(DockerEngineApiUtil.FFMPEG_COMTAINER_NAME, command);
    }

    @SneakyThrows
    public static void convertWavToAmrDev(String voicePath, String wavFilename) {
        FFmpeg ffmpeg = new FFmpeg("D:/ffmpeg/bin/ffmpeg.exe");
        FFmpegBuilder builder = new FFmpegBuilder()
                .setInput(voicePath + wavFilename)
                .overrideOutputFiles(true)
                .addOutput(voicePath + wavFilename.replace(LokTarConstant.VOICE_SUFFIX_WAV, LokTarConstant.VOICE_SUFFIX_AMR))
                .setAudioCodec("libopencore_amrnb")
                // Set the audio sample rate to 8000 Hz
                .addExtraArgs("-ar", "8000")
                // Set the audio to mono (single channel)
                .addExtraArgs("-ac", "1")
                .done();
        new FFmpegExecutor(ffmpeg).createJob(builder).run();
    }

    @SneakyThrows
    public static void convertAmrToWavDev(String voicePath, String amrFilename) {
        FFmpeg ffmpeg = new FFmpeg("D:/ffmpeg/bin/ffmpeg.exe");

        FFmpegBuilder builder = new FFmpegBuilder()
                .setInput(voicePath + amrFilename)
                .overrideOutputFiles(true)
                .addOutput(voicePath + amrFilename.replace(LokTarConstant.VOICE_SUFFIX_AMR, LokTarConstant.VOICE_SUFFIX_WAV))
                .setAudioChannels(1)
                .setAudioSampleRate(16_000)
                .setAudioBitRate(256_000)
                .done();
        new FFmpegExecutor(ffmpeg).createJob(builder).run();
    }


    //合并mp4 小米摄像头的视频用这个方法
    public static void mergeMP4Files(String ffmpegPath, String ffprobePath, String inputFolderPath, String outputFilePath) {
        try {
            FFmpeg ffmpeg = new FFmpeg(ffmpegPath);
            FFprobe ffprobe = new FFprobe(ffprobePath);

            FFmpegExecutor executor = new FFmpegExecutor(ffmpeg, ffprobe);

            // 获取文件夹中的所有MP4文件
            List<Path> inputFiles = Files.list(Paths.get(inputFolderPath))
                    .filter(file -> file.toString().endsWith(".mp4"))
                    .toList();

            // 创建一个临时文件，其中列出了所有输入文件
            Path listFile = Files.createTempFile("ffmpeg-input-list", ".txt");
            Files.write(listFile, inputFiles.stream()
                    .map(file -> "file '" + file.toString().replace("\\", "/") + "'")
                    .collect(Collectors.toList()));

            // 使用FFmpeg的concat demuxer将所有MP4文件合并为一个文件
            FFmpegBuilder builder = new FFmpegBuilder()
                    .setInput(listFile.toString())
                    .addExtraArgs("-f", "concat")
                    .setFormat("mp4")
                    .addExtraArgs("-safe", "0")
                    .overrideOutputFiles(true)
                    .addOutput(outputFilePath + ".mp4")
                    .setVideoCodec("copy")
                    .setAudioCodec("aac")
                    .done();

            executor.createJob(builder).run();

            // 删除临时文件
            Files.delete(listFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        String ffmpegPath = "D:/ffmpeg/bin/ffmpeg.exe";
        String ffprobePath = "D:/ffmpeg/bin/ffprobe.exe";

        //String inputFilePath = "F:/voice/111.amr";
        //String outputFilePath = "F:/voice/111.wav";
        //convertAmrToWav(ffmpegPath,inputFilePath,outputFilePath);

        String inputFilePath = "F:/mp4fold/2021070412";
        String outputFilePath = "F:/mp4fold/2021070412/result";
        mergeMP4Files(ffmpegPath, ffprobePath, inputFilePath, outputFilePath);
    }
}
