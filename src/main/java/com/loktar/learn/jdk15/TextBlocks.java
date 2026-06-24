package com.loktar.learn.jdk15;


import lombok.extern.slf4j.Slf4j;
@Slf4j
public class TextBlocks {
    public static void main(String[] args) {
        String textBlock = """
    Hello,
    This is a multi-line
    text block.
               \s""";
        log.info("{}", textBlock);

        String indentedBlock = """
                This is an indented block
                with leading and trailing spaces.
                """.stripIndent();
        log.info("{}", indentedBlock);
    }
}
