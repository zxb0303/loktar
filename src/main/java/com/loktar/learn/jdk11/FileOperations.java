package com.loktar.learn.jdk11;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;

public class FileOperations {
    public static void main(String[] args) throws Exception {
        String filePath = "example.txt";

        // 写入文件
        String content = "Hello, world!";
        Files.writeString(Path.of(filePath), content);

        // 读取文件
        String fileContent = Files.readString(Path.of(filePath));
        System.out.println("文件内容：\n" + fileContent);

        // 逐行写入文件
        List<String> lines = List.of("Line 1", "Line 2", "Line 3");
        //Files.writeStringList(Path.of(filePath), lines, StandardOpenOption.APPEND);

        // 逐行读取文件
        //List<String> fileLines = Files.readStringList(Path.of(filePath));
        //System.out.println("文件行数：" + fileLines.size());
        //System.out.println("文件内容：");
        //for (String line : fileLines) {
        //    System.out.println(line);
        //}
    }
}
