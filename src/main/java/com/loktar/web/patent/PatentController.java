package com.loktar.web.patent;

import com.loktar.domain.patent.*;
import com.loktar.mapper.patent.*;
import com.loktar.util.PatentUtil;
import com.loktar.util.UUIDUtil;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("patent")
public class PatentController {
    private final PatentDetailMapper patentDetailMapper;
    private final PatentApplyMapper patentApplyMapper;
    private final PatentApplyDetailMapper patentApplyDetailMapper;
    private final PatentContentMapper patentContentMapper;

    public PatentController(PatentDetailMapper patentDetailMapper, PatentApplyMapper patentApplyMapper, PatentApplyDetailMapper patentApplyDetailMapper, PatentContentMapper patentContentMapper) {
        this.patentDetailMapper = patentDetailMapper;
        this.patentApplyMapper = patentApplyMapper;
        this.patentApplyDetailMapper = patentApplyDetailMapper;
        this.patentContentMapper = patentContentMapper;
    }



    @GetMapping("/gen.do")
    public void gen(int year, int type, int start, int count) {
        //year 2022 type 2 start 1 count 100
        List<String> patentNumbers = PatentUtil.generatePatentNumbers(year, type, start, count);
        List<PatentContent> patentContents = new ArrayList<>();
        for (String patentNumber : patentNumbers) {
            PatentContent patentContent = new PatentContent();
            patentContent.setPatentId(patentNumber);
            patentContent.setStatus(0);
            patentContents.add(patentContent);
        }
        patentContentMapper.insertBatch(patentContents);
    }

    @GetMapping("/dealContent.do")
    public void dealContent() {
        List<PatentContent> patentContents = patentContentMapper.selectByStatus(1);
        for (PatentContent patentContent : patentContents) {
            PatentDetail patentDetail = PatentUtil.dealPatentContent(patentContent.getContent());
            patentDetail.setPatentId(patentContent.getPatentId());
            patentContent.setStatus(2);
            patentContentMapper.updateByPrimaryKey(patentContent);
            patentDetailMapper.insert(patentDetail);
        }
    }

    @GetMapping("/dealDetail.do")
    public void dealDetail() {
        List<PatentDetail> patentDetails = patentDetailMapper.selectByStatus(0);
        for (PatentDetail patentDetail : patentDetails) {
            String applyNameStr = patentDetail.getApplyName();
            if ("--".equals(applyNameStr)||"信息不完整或未找到".equals(applyNameStr)) {
                patentDetail.setStatus(1);
                patentDetailMapper.updateByPrimaryKey(patentDetail);
                continue;
            }
            String[] applyNames = new String[]{applyNameStr};
            if (applyNameStr.contains(",")) {
                applyNames = applyNameStr.split(",");
            }
            for (String applyName : applyNames) {
                PatentApply patentApply = patentApplyMapper.selectByApplyName(applyName);
                if (ObjectUtils.isEmpty(patentApply)) {
                    patentApply = new PatentApply();
                    patentApply.setApplyId(UUIDUtil.randomUUID());
                    patentApply.setApplyName(applyName);
                    patentApply.setStatus(0);
                    patentApplyMapper.insert(patentApply);
                }
                PatentApplyDetail patentApplyDetail = new PatentApplyDetail();
                patentApplyDetail.setApplyDetailId(UUIDUtil.randomUUID());
                patentApplyDetail.setPatentId(patentDetail.getPatentId());
                patentApplyDetail.setApplyId(patentApply.getApplyId());
                patentApplyDetailMapper.insert(patentApplyDetail);
            }
            patentDetail.setStatus(1);
            patentDetailMapper.updateByPrimaryKey(patentDetail);
        }
        System.out.println("dealhistory");
    }


}
