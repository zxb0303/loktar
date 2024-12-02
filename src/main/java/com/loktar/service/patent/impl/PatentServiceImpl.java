package com.loktar.service.patent.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.loktar.domain.patent.PatentApply;
import com.loktar.domain.patent.PatentDetail;
import com.loktar.domain.patent.PatentPdfApply;
import com.loktar.domain.patent.PatentTrade;
import com.loktar.mapper.patent.PatentApplyMapper;
import com.loktar.mapper.patent.PatentDetailMapper;
import com.loktar.mapper.patent.PatentPdfApplyMapper;
import com.loktar.mapper.patent.PatentTradeMapper;
import com.loktar.service.patent.PatentService;
import lombok.SneakyThrows;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class PatentServiceImpl implements PatentService {
    private final PatentDetailMapper patentDetailMapper;
    private final PatentPdfApplyMapper patentPdfApplyMapper;
    private final PatentApplyMapper patentApplyMapper;
    private final PatentTradeMapper patentTradeMapper;
    private ObjectMapper objectMapper = new ObjectMapper().setPropertyNamingStrategy(PropertyNamingStrategies.LOWER_CAMEL_CASE).registerModule(new JavaTimeModule());

    public PatentServiceImpl(PatentDetailMapper patentDetailMapper, PatentPdfApplyMapper patentPdfApplyMapper, PatentApplyMapper patentApplyMapper, PatentTradeMapper patentTradeMapper) {
        this.patentDetailMapper = patentDetailMapper;
        this.patentPdfApplyMapper = patentPdfApplyMapper;
        this.patentApplyMapper = patentApplyMapper;
        this.patentTradeMapper = patentTradeMapper;
    }

    @Override
    @SneakyThrows
    @Transactional
    public void deal(String applyId, int patentCount, String detail) {
        //System.out.println("detail:" + detail);
        //System.out.println("patentCount:" + patentCount);
        System.out.println("applyId:" + applyId);
        patentDetailMapper.deleteByApplyId(applyId);
        patentApplyMapper.deleteByPrimaryKey(applyId);
        List<PatentDetail> needRemove = new ArrayList<>();
        if (!StringUtils.isEmpty(detail)) {
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
                if (patentDetail.getApplyName().contains(",") || patentDetail.getApplyName().contains("分公司")) {
                    needRemove.add(patentDetail);
                }
                String name = patentDetail.getName();
                int status;
                if (name.contains("水利")) {
                    status = 1;
                } else if (name.matches(".*(建筑|混凝土|电气|桥梁|自动化|土木|计算机).*")) {
                    status = 2;
                } else {
                    status = 0;
                }
                patentDetail.setStatus(status);
                patentDetail.setApplyId(applyId);
            }
            patentDetails.removeAll(needRemove);
            if (!CollectionUtils.isEmpty(patentDetails)) {
                try {
                    patentDetailMapper.insertBatch(patentDetails);
                } catch (Exception e) {
                    e.printStackTrace();
                    for (PatentDetail patentDetail : patentDetails) {
                        PatentDetail exist = patentDetailMapper.selectByPrimaryKey(patentDetail.getPatentId());
                        if (!ObjectUtils.isEmpty(exist)) {
                            PatentTrade patentTrade = new PatentTrade();
                            patentTrade.setPatentId(patentDetail.getPatentId());
                            patentTrade.setFromApplyName(exist.getApplyName());
                            patentTrade.setToApplyName(patentDetail.getApplyName());
                            if (!patentTrade.getFromApplyName().equals(patentTrade.getToApplyName())) {
                                patentTradeMapper.insert(patentTrade);
                            }
                            patentDetailMapper.updateByPrimaryKey(patentDetail);
                        } else {
                            patentDetailMapper.insert(patentDetail);
                        }
                    }
                }
            }
        }
        //处理patentPdfApply
        PatentPdfApply patentPdfApply = patentPdfApplyMapper.selectByPrimaryKey(applyId);
        patentPdfApply.setStatus(1);
        patentPdfApply.setUpdateTime(LocalDateTime.now());
        patentPdfApplyMapper.updateByPrimaryKey(patentPdfApply);

        //处理patentApply
        PatentApply patentApply = new PatentApply();
        patentApply.setApplyId(applyId);
        patentApply.setApplyName(patentPdfApply.getApplyName());
        if (!StringUtils.isEmpty(detail)) {
            patentApply.setStatus(0);
        } else {
            patentApply.setStatus(-1);
        }
        patentApply.setPatentCount(patentCount - needRemove.size());
        patentApplyMapper.insert(patentApply);
    }
}
