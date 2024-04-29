package com.loktar.web.patent;

import com.loktar.domain.patent.Patent;
import com.loktar.mapper.patent.PatentMapper;
import com.loktar.util.PatentUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("patent")
public class PatentController {
    private final PatentMapper patentMapper;

    public PatentController(PatentMapper patentMapper) {
        this.patentMapper = patentMapper;
    }

    @GetMapping("/gen.do")
    public void gen(int year,int type,int start,int count) {
        //year 2022 type 2 start 1 count 100
        List<String> patentNumbers = PatentUtil.generatePatentNumbers(year, type, start, count);
        List<Patent> patents = new ArrayList<>();
        for (String patentNumber : patentNumbers) {
            Patent patent = new Patent();
            patent.setPatentId(patentNumber);
            patent.setStatus(0);
            patents.add(patent);
        }
        patentMapper.insertBatch(patents);
    }

    @GetMapping("/deal.do")
    public void deal(){
        int status = 1;
        List<Patent> patents = patentMapper.selectByStatus(status);
        for (Patent patent : patents) {
            patent = PatentUtil.dealPatent(patent);
            patent.setStatus(2);
            patentMapper.updateByPrimaryKey(patent);
        }

    }
}
