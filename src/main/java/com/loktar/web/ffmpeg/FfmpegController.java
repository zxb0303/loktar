package com.loktar.web.ffmpeg;

import com.loktar.util.FFmpegUtil;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



@RestController
@RequestMapping("ffmpeg")
public class FfmpegController {

    @RequestMapping("/execute.do")
    public void execute() {
        String voicePath ="/loktar/voice/";
        String wavFilename = "20240329103527_1.wav";
        FFmpegUtil.convertWavToAmrPro(voicePath,wavFilename);
        System.out.println("111");
    }
}

