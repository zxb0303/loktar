package com.loktar.util;

import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;


public class FFmpegUtil {
    //TODO 尝试用docker镜像 不直接打进去

    public static void convertWavToAmr(String ffmpegPath, String inputFilePath, String outputFilePath) {
        FFmpeg ffmpeg = null;
        try {
            ffmpeg = new FFmpeg(ffmpegPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        FFmpegBuilder builder = new FFmpegBuilder()
                .setInput(inputFilePath)
                .overrideOutputFiles(true)
                .addOutput(outputFilePath)
                .setAudioCodec("libopencore_amrnb")
                .addExtraArgs("-ar", "8000") // Set the audio sample rate to 8000 Hz
                .addExtraArgs("-ac", "1") // Set the audio to mono (single channel)
                .done();
        FFmpegExecutor executor = null;
        //ffmpeg -i /debian-sshd-macvlan/1a9554381457405b86041adf99f3dbd6.wav -c:a libopencore_amrnb /debian-sshd-macvlan/1.amr
        try {
            executor = new FFmpegExecutor(ffmpeg);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        executor.createJob(builder).run();
    }

    public static void convertAmrToWav(String ffmpegPath, String inputFilePath, String outputFilePath) {
        FFmpeg ffmpeg = null;
        try {
            ffmpeg = new FFmpeg(ffmpegPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        FFmpegBuilder builder = new FFmpegBuilder()
                .setInput(inputFilePath)
                .overrideOutputFiles(true)
                .addOutput(outputFilePath)
                .setAudioChannels(1)
                .setAudioSampleRate(16_000)
                .setAudioBitRate(256_000)
                .done();
        FFmpegExecutor executor = null;
        //ffmpeg -i F:/voice/1.amr -ac 1 -ar 16000 -b:a 256k F:/voice/output.wav
        try {
            executor = new FFmpegExecutor(ffmpeg);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        executor.createJob(builder).run();
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
