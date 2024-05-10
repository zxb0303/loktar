package com.loktar.service.patent.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.loktar.domain.patent.PatentApply;
import com.loktar.domain.patent.PatentDetail;
import com.loktar.domain.patent.PatentPdfApply;
import com.loktar.mapper.patent.PatentApplyMapper;
import com.loktar.mapper.patent.PatentDetailMapper;
import com.loktar.mapper.patent.PatentPdfApplyMapper;
import com.loktar.service.patent.PatentService;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PatentServiceImpl implements PatentService {
    private final PatentDetailMapper patentDetailMapper;
    private final PatentPdfApplyMapper patentPdfApplyMapper;
    private final PatentApplyMapper patentApplyMapper;
    private ObjectMapper objectMapper = new ObjectMapper().setPropertyNamingStrategy(PropertyNamingStrategies.LOWER_CAMEL_CASE).registerModule(new JavaTimeModule());

    public PatentServiceImpl(PatentDetailMapper patentDetailMapper, PatentPdfApplyMapper patentPdfApplyMapper, PatentApplyMapper patentApplyMapper) {
        this.patentDetailMapper = patentDetailMapper;
        this.patentPdfApplyMapper = patentPdfApplyMapper;
        this.patentApplyMapper = patentApplyMapper;
    }

    @Override
    @SneakyThrows
    @Transactional
    public void deal(String applyId, int patentCount, String detail) {
        System.out.println("detail:" + detail);
        System.out.println("patentCount:" + patentCount);
        System.out.println("applyId:" + applyId);
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
            patentDetailMapper.deleteByApplyId(applyId);
            for (PatentDetail patentDetail : patentDetails) {
                patentDetail.setStatus(0);
                patentDetail.setApplyId(applyId);
            }
            patentDetailMapper.insertBatch(patentDetails);
        }
        //处理patentPdfApply
        PatentPdfApply patentPdfApply = patentPdfApplyMapper.selectByPrimaryKey(applyId);
        patentPdfApply.setStatus(1);
        patentPdfApply.setUpdateTime(LocalDateTime.now());
        patentPdfApplyMapper.updateByPrimaryKey(patentPdfApply);

        //处理patentApply
        patentApplyMapper.deleteByPrimaryKey(applyId);
        PatentApply patentApply = new PatentApply();
        patentApply.setApplyId(applyId);
        patentApply.setApplyName(patentPdfApply.getApplyName());
        if(!StringUtils.isEmpty(detail)){
            patentApply.setStatus(0);
        }else{
            patentApply.setStatus(-1);
        }
        patentApply.setPatentCount(patentCount);
        patentApplyMapper.insert(patentApply);
    }
}
