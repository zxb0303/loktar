package com.loktar.web.test;

import java.io.IOException;

public class MainTest {

    public static void main(String[] args) throws IOException {
        String str="[{\"patentId\":\"2023227968646\",\"name\":\"电梯门锁开关短接检测系统\",\"applyName\":\"申请人：杭州优迈科技有限公司\",\"type\":\"专利类型：实用新型\",\"applyDate\":\"申请日：2023-10-18\",\"pubNoticeNum\":\"发明专利申请公布号：\",\"authNoticeNum\":\"授权公告号：CN220906904U\",\"legalStatus\":\"法律状态：专利权有效\",\"caseStatus\":\"案件状态：专利权维持\",\"authNoticeDate\":\"授权公告日：2024-05-07\",\"mainCategoryNum\":\"主分类号：B66B13/14\"},{\"patentId\":\"2023227066064\",\"name\":\"电梯召唤盒\",\"applyName\":\"申请人：杭州优迈科技有限公司\",\"type\":\"专利类型：实用新型\",\"applyDate\":\"申请日：2023-10-08\",\"pubNoticeNum\":\"发明专利申请公布号：\",\"authNoticeNum\":\"授权公告号：CN220906854U\",\"legalStatus\":\"法律状态：专利权有效\",\"caseStatus\":\"案件状态：专利权维持\",\"authNoticeDate\":\"授权公告日：2024-05-07\",\"mainCategoryNum\":\"主分类号：B66B1/46\"},{\"patentId\":\"2023225547394\",\"name\":\"显示屏及地铁屏蔽门\",\"applyName\":\"申请人：杭州优迈科技有限公司\",\"type\":\"专利类型：实用新型\",\"applyDate\":\"申请日：2023-09-19\",\"pubNoticeNum\":\"发明专利申请公布号：\",\"authNoticeNum\":\"授权公告号：CN220913829U\",\"legalStatus\":\"法律状态：专利权有效\",\"caseStatus\":\"案件状态：专利权维持\",\"authNoticeDate\":\"授权公告日：2024-05-07\",\"mainCategoryNum\":\"主分类号：G09F9/30\"},{\"patentId\":\"202322476194X\",\"name\":\"电梯按钮、电梯操作箱及电梯\",\"applyName\":\"申请人：杭州优迈科技有限公司\",\"type\":\"专利类型：实用新型\",\"applyDate\":\"申请日：2023-09-12\",\"pubNoticeNum\":\"发明专利申请公布号：\",\"authNoticeNum\":\"授权公告号：CN220906853U\",\"legalStatus\":\"法律状态：专利权有效\",\"caseStatus\":\"案件状态：专利权维持\",\"authNoticeDate\":\"授权公告日：2024-05-07\",\"mainCategoryNum\":\"主分类号：B66B1/46\"}}]";
        System.out.println(str);
        str = str.replace("申请人：", "")
                .replace("专利类型：", "")
                .replace("申请日：", "")
                .replace("发明专利申请公布号：", "")
                .replace("授权公告号：", "")
                .replace("法律状态：", "")
                .replace("案件状态：", "")
                .replace("授权公告日：", "")
                .replace("主分类号：", "");
        System.out.println(str);
    }

}

