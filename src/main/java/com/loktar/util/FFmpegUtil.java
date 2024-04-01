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
        FFmpegUtil.env = env; // 静态字段赋值
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
                .addExtraArgs("-ar", "8000") // Set the audio sample rate to 8000 Hz
                .addExtraArgs("-ac", "1") // Set the audio to mono (single channel)
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

    //合并mp4 中途先转ts
    public static void mergeMP4FilesUseTs(String ffmpegPath, String ffprobePath, String inputFolderPath, String outputFilePath) {
        try {
            FFmpeg ffmpeg = new FFmpeg(ffmpegPath);
            FFprobe ffprobe = new FFprobe(ffprobePath);

            FFmpegExecutor executor = new FFmpegExecutor(ffmpeg, ffprobe);

            // 获取文件夹中的所有MP4文件
            List<Path> inputFiles = Files.list(Paths.get(inputFolderPath))  // 请将此路径替换为你的输入文件夹的路径
                    .filter(file -> file.toString().endsWith(".mp4"))
                    .collect(Collectors.toList());

            // 将所有输入文件转换为.ts格式，并添加到builder中
            for (Path inputFile : inputFiles) {
                String tsFile = inputFile.toString().replace(".mp4", ".ts");
                FFmpegBuilder builder = new FFmpegBuilder()
                        .setInput(inputFile.toString())
                        .overrideOutputFiles(true)
                        .addOutput(tsFile)
                        .setFormat("mpegts")
                        .done();
                executor.createJob(builder).run();
            }

            // 重新获取文件夹中的所有.ts文件
            List<Path> tsFiles = Files.list(Paths.get(inputFolderPath))
                    .filter(file -> file.toString().endsWith(".ts"))
                    .collect(Collectors.toList());

            // 创建一个包含所有.ts文件路径的字符串，用'|'分隔
            String concatFiles = tsFiles.stream()
                    .map(Path::toString)
                    .collect(Collectors.joining("|"));

            // 使用FFmpeg的concat协议将所有.ts文件合并为一个文件
            FFmpegBuilder builder = new FFmpegBuilder()
                    .setInput("concat:" + concatFiles)
                    .overrideOutputFiles(true)
                    .addOutput(outputFilePath + ".ts")  // 合并后的.ts文件
                    .setFormat("mpegts")
                    .done();

            executor.createJob(builder).run();

            // 将合并后的.ts文件转换回MP4格式
            FFmpegBuilder convertBuilder = new FFmpegBuilder()
                    .setInput(outputFilePath + ".ts")
                    .overrideOutputFiles(true)
                    .addOutput(outputFilePath + ".mp4")  // 输出文件，包括路径和文件名
                    .setFormat("mp4")
                    .done();

            executor.createJob(convertBuilder).run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //合并mp4 小米摄像头的视频用这个方法
    public static void mergeMP4Files(String ffmpegPath, String ffprobePath, String inputFolderPath, String outputFilePath) {
        try {
            FFmpeg ffmpeg = new FFmpeg(ffmpegPath);
            FFprobe ffprobe = new FFprobe(ffprobePath);

            FFmpegExecutor executor = new FFmpegExecutor(ffmpeg, ffprobe);

            // 获取文件夹中的所有MP4文件
            List<Path> inputFiles = Files.list(Paths.get(inputFolderPath))  // 请将此路径替换为你的输入文件夹的路径
                    .filter(file -> file.toString().endsWith(".mp4"))
                    .collect(Collectors.toList());

            // 创建一个临时文件，其中列出了所有输入文件
            Path listFile = Files.createTempFile("ffmpeg-input-list", ".txt");
            Files.write(listFile, inputFiles.stream()
                    .map(file -> "file '" + file.toString().replace("\\", "/") + "'")
                    .collect(Collectors.toList()));

            // 使用FFmpeg的concat demuxer将所有MP4文件合并为一个文件
            FFmpegBuilder builder = new FFmpegBuilder()
                    .setInput(listFile.toString())
                    .addExtraArgs("-f", "concat")  // 设置输入格式为concat
                    .setFormat("mp4")
                    .addExtraArgs("-safe", "0")
                    .overrideOutputFiles(true)
                    .addOutput(outputFilePath + ".mp4")  // 输出文件，包括路径和文件名
                    .setVideoCodec("copy")  // 直接复制视频流，不进行转码
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
