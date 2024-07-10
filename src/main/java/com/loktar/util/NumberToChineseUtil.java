package com.loktar.util;

public class NumberToChineseUtil {
    // 数字对应的中文大写
    private static final String[] CN_UPPER_NUMBER = {"零", "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖"};
    // 单位
    private static final String[] CN_UPPER_UNIT = {"", "拾", "佰", "仟"};
    // 大单位
    private static final String[] CN_UPPER_BIG_UNIT = {"", "万", "亿"};
    // 整数部分的单位
    private static final String CN_FULL = "整";
    // 元
    private static final String CN_YUAN = "元";


    public static String numberToChinese(int number) {
        if (number == 0) {
            return CN_UPPER_NUMBER[0] + CN_YUAN + CN_FULL;
        }

        StringBuilder sb = new StringBuilder();
        int unitPos = 0; // 单位的位置
        boolean zero = true; // 是否为零
        int count = 0; // 用来判断当前位是万还是亿

        while (number > 0) {
            int segment = number % 10;
            if (segment == 0) {
                if (!zero) {
                    zero = true;
                    sb.insert(0, CN_UPPER_NUMBER[0]);
                }
            } else {
                zero = false;
                String unit = CN_UPPER_UNIT[unitPos % 4];
                if (unitPos % 4 == 0 && unitPos > 0) {
                    unit = CN_UPPER_BIG_UNIT[unitPos / 4] + unit;
                }
                sb.insert(0, CN_UPPER_NUMBER[segment] + unit);
            }
            unitPos++;
            number /= 10;
            count++;
        }

        // 补上“元”字
        sb.append(CN_YUAN);
        // 补上“整”字
        sb.append(CN_FULL);

        // 去掉多余的“零”
        String result = sb.toString().replaceAll("零+", "零");
        if (result.startsWith("零")) {
            result = result.substring(1);
        }
        if (result.endsWith("零")) {
            result = result.substring(0, result.length() - 1);
        }

        return result;
    }

    public static void main(String[] args) {

        System.out.println(numberToChinese(3608));
    }
}
