package com.loktar.web.test;


import lombok.SneakyThrows;

import java.util.ArrayList;
import java.util.List;

public class MainTest {

    @SneakyThrows
    public static void main(String[] args) {
        String str = "好的，让我来给你讲一个小兔子的故事吧。从前，有一只非常可爱的小兔子，他住在森林里，森林里有很多朋友，比如聪明的小猴子、勇敢的小狮子还有慢吞吞的乌龟。但是小兔子最好的朋友是一棵大树，因为每当小兔子累了，它就会跑到大树底下休息，大树还会用树叶轻轻地给小兔子扇风。有一天，小兔子决定去探险，看看森林的另一边是什么。它告别了大树和其他朋友，踏上了旅程。小兔子一路上遇到了很多困难，比如过河时差点被水冲走，爬山时不小心滑倒了，但它都勇敢地克服了。经过一天的努力，小兔子终于到达了森林的另一边。它惊讶地发现，这里有一片美丽的花海，五颜六色的花儿开得正盛，空气中弥漫着淡淡的香味。小兔子开心极了，它在花海中跳跃玩耍，还采了一些花儿准备带回家给大树和朋友们看。当小兔子带着满满的快乐和礼物回到家时，大树和所有的朋友都为它鼓掌欢呼。小兔子把看到的美丽景色和经历的冒险故事分享给了大家，大家听得津津有味。从此以后，小兔子成了一个勇敢的小探险家，而它学到的最重要的一课是：无论遇到什么困难，都要勇敢地面对，因为你永远不知道，下一个转角会遇见怎样美丽的风景。故事就讲到这里啦，你喜欢这个故事吗";
        List<String> strs = splitTextBySentence(str);
        for (String s : strs) {
            System.out.println(s);
        }
    }

    public static List<String> splitTextBySentence(String text) {
        int maxLength = 280;
        List<String> result = new ArrayList<>();
        StringBuilder currentChunk = new StringBuilder();
        // 使用正向预查保留分隔符
        String[] sentences = text.split("(?<=。|！|？|\n)");

        for (String sentence : sentences) {
            if (currentChunk.length() + sentence.length() <= maxLength) {
                currentChunk.append(sentence);
            } else {
                if (currentChunk.length() > 0) {
                    result.add(currentChunk.toString());
                    currentChunk = new StringBuilder();
                }
                // 处理单个句子长度超过最大长度的情况
                while (sentence.length() > maxLength) {
                    String part = sentence.substring(0, maxLength);
                    result.add(part);
                    sentence = sentence.substring(maxLength);
                }
                currentChunk.append(sentence);
            }
        }

        if (currentChunk.length() > 0) {
            result.add(currentChunk.toString());
        }

        return result;
    }
}
