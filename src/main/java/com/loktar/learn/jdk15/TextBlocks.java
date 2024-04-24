package com.loktar.learn.jdk15;

public class TextBlocks {
    public static void main(String[] args) {
        String textBlock = """
    Hello,
    This is a multi-line
    text block.
               \s""";
        System.out.println(textBlock);

        String indentedBlock = """
                This is an indented block
                with leading and trailing spaces.
                """.stripIndent();
        System.out.println(indentedBlock);
    }
}
