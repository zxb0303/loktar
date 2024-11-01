package com.loktar.web.test;

public class MainTest {



    public static void main(String[] args) {
        String str = "7.基于上述结论性意见,审查员认为: :unselected: 申请人应当按照通知书正文部分提出的要求,对申请文件进行修改。 :selected: X 申请人应当在意见陈述书中论述其专利申请可以被授予专利权的理由,并对通知书正文部分中指出的不符 合规定之处进行修改,否则将不能授予专利权。 :unselected: 专利申请中没有可以被授予专利权的实质性内容,如果申请人没有陈述理由或者陈述理由不充分,其申请 将被驳回。 :unselected:";
        String result = str.replaceAll(":unselected:", "0")
                .replaceAll(":selected:", "1");
        StringBuilder output = new StringBuilder();
        for (char c : result.toCharArray()) {
            if (c == '0' || c == '1') {
                output.append(c);
            }
        }
        System.out.println(output.toString());
    }

}

