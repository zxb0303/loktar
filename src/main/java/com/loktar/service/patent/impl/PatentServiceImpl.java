package com.loktar.service.patent.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.loktar.domain.patent.PatentApply;
import com.loktar.domain.patent.PatentApplyDetail;
import com.loktar.domain.patent.PatentDetail;
import com.loktar.domain.patent.PatentPdfApply;
import com.loktar.mapper.patent.PatentApplyDetailMapper;
import com.loktar.mapper.patent.PatentApplyMapper;
import com.loktar.mapper.patent.PatentDetailMapper;
import com.loktar.mapper.patent.PatentPdfApplyMapper;
import com.loktar.service.patent.PatentService;
import com.loktar.util.UUIDUtil;
import lombok.SneakyThrows;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class PatentServiceImpl implements PatentService {
    private final PatentDetailMapper patentDetailMapper;
    private final PatentPdfApplyMapper patentPdfApplyMapper;
    private final PatentApplyMapper patentApplyMapper;
    private final PatentApplyDetailMapper patentApplyDetailMapper;
    private ObjectMapper objectMapper = new ObjectMapper().setPropertyNamingStrategy(PropertyNamingStrategies.LOWER_CAMEL_CASE).registerModule(new JavaTimeModule());

    public PatentServiceImpl(PatentDetailMapper patentDetailMapper, PatentPdfApplyMapper patentPdfApplyMapper, PatentApplyMapper patentApplyMapper, PatentApplyDetailMapper patentApplyDetailMapper) {
        this.patentDetailMapper = patentDetailMapper;
        this.patentPdfApplyMapper = patentPdfApplyMapper;
        this.patentApplyMapper = patentApplyMapper;
        this.patentApplyDetailMapper = patentApplyDetailMapper;
    }

    @Override
    @SneakyThrows
    @Transactional
    public void deal(String applyId, String detail) {
        System.out.println(detail);
        System.out.println(applyId);
        List<PatentApplyDetail> patentApplyDetails = new ArrayList<>();
        detail = detail.replace("申请人：", "")
                .replace("专利类型：", "")
                .replace("申请日：", "")
                .replace("发明专利申请公布号：", "")
                .replace("授权公告号：", "")
                .replace("法律状态：", "")
                .replace("案件状态：", "")
                .replace("授权公告日：", "")
                .replace("主分类号：", "");
        List<PatentDetail> patentDetails = objectMapper.readValue(detail, new TypeReference<>() {
        });
        //处理patentDetail
        for (PatentDetail patentDetail : patentDetails) {
            patentDetail.setStatus(0);
            if (ObjectUtils.isEmpty(patentDetailMapper.selectByPrimaryKey(patentDetail.getPatentId()))) {
                patentDetailMapper.insert(patentDetail);
            } else {
                patentDetailMapper.updateByPrimaryKey(patentDetail);
            }
            PatentApplyDetail patentApplyDetail = new PatentApplyDetail();
            patentApplyDetail.setApplyDetailId(UUIDUtil.randomUUID());
            patentApplyDetail.setPatentId(patentDetail.getPatentId());
            patentApplyDetail.setApplyId(applyId);
            patentApplyDetails.add(patentApplyDetail);
        }
        //处理patentApplyDetail
        for (PatentApplyDetail patentApplyDetail : patentApplyDetails) {
            if (ObjectUtils.isEmpty(patentApplyDetailMapper.selectByPatentIdAndApplyId(patentApplyDetail.getPatentId(), patentApplyDetail.getApplyId()))) {
                patentApplyDetailMapper.insert(patentApplyDetail);
            } else {
                patentApplyDetailMapper.updateByPrimaryKey(patentApplyDetail);
            }
        }
        //处理patentApply
        PatentApply patentApply = patentApplyMapper.selectByPrimaryKey(applyId);
        if (ObjectUtils.isEmpty(patentApply)) {
            patentApply = new PatentApply();
            PatentPdfApply patentPdfApply = patentPdfApplyMapper.selectByPrimaryKey(applyId);
            patentApply.setApplyId(applyId);
            patentApply.setApplyName(patentPdfApply.getApplyName());
            patentApply.setStatus(1);
            patentApply.setPatentCount(patentDetails.size());
            patentApplyMapper.insert(patentApply);
        }else{
            patentApply.setStatus(1);
            patentApply.setPatentCount(patentDetails.size());
            patentApplyMapper.updateByPrimaryKey(patentApply);
        }
        //处理patentPdfApply
        PatentPdfApply patentPdfApply = patentPdfApplyMapper.selectByPrimaryKey(applyId);
        patentPdfApply.setStatus(1);
        patentPdfApplyMapper.updateByPrimaryKey(patentPdfApply);
    }
}
