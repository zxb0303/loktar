package com.loktar.web.test;

import com.loktar.conf.LokTarConstant;

public class MainTest {


    public static void main(String[] args) {
        String fileName = "1111.amr";
        System.out.println("fileName：" + fileName);
        String coverFileName = fileName.lastIndexOf(LokTarConstant.VOICE_SUFFIX_WAV) != -1 ? fileName.replace(LokTarConstant.VOICE_SUFFIX_WAV, LokTarConstant.VOICE_SUFFIX_AMR) : fileName.replace(LokTarConstant.VOICE_SUFFIX_AMR, LokTarConstant.VOICE_SUFFIX_WAV);
        System.out.println(coverFileName);

    }

}

