package com.loktar.web.cxy;

import com.loktar.domain.cxy.CompanyProperty;
import com.loktar.mapper.cxy.CompanyPropertyMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("companyProperty")
public class CompanyPropertyController {

    private final CompanyPropertyMapper companyPropertyMapper;

    public CompanyPropertyController( CompanyPropertyMapper companyPropertyMapper) {
        this.companyPropertyMapper = companyPropertyMapper;
    }

    @GetMapping("/test.do")
    public void test() {
        List<CompanyProperty> tests = companyPropertyMapper.selectAll();
        for (CompanyProperty companyProperty : tests) {
            String[] strs = companyProperty.getZichanbianhao().split("-");
            if (strs.length < 4) {
                System.out.println(companyProperty.getZhuti() + ";" + companyProperty.getZichanbianhao() + ";" + companyProperty.getShebeimingcheng() + ";" + companyProperty.getPinpai() + ";" + companyProperty.getXinghao() + ";" + "1" + ";" + companyProperty.getDanjia() + ";" + companyProperty.getDanjia());
            } else {
                //22060014
                int start = Integer.parseInt(strs[2]);
                //22060023
                int end = Integer.parseInt(replaceEnd(strs[2], strs[3]));
                for (int i = start; i <= end; i++) {
                    String newBianhao = strs[0] + "-" + strs[1] + "-" + i;
                    System.out.println(companyProperty.getZhuti() + ";" + newBianhao + ";" + companyProperty.getShebeimingcheng() + ";" + companyProperty.getPinpai() + ";" + companyProperty.getXinghao() + ";" + "1" + ";" + companyProperty.getDanjia() + ";" + companyProperty.getDanjia());
                }
            }
        }
    }

    public static String replaceEnd(String a, String b) {
        if (a.length() < b.length()) {
            throw new IllegalArgumentException("字符串a必须比b长");
        }

        // 截取a的前面部分，长度为a的长度减去b的长度
        String startPart = a.substring(0, a.length() - b.length());

        // 将截取的部分与b拼接
        return startPart + b;
    }

}
