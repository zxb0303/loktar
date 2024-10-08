package com.loktar.web.patent;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.loktar.domain.patent.PatentDetail;
import com.loktar.domain.patent.PatentDetailDocInfo;
import com.loktar.dto.patent.PatentDetailDTO;
import com.loktar.mapper.patent.PatentDetailDocInfoMapper;
import com.loktar.mapper.patent.PatentDetailMapper;
import com.loktar.util.DateTimeUtil;
import com.loktar.util.PatentUtil;
import com.loktar.util.UUIDUtil;
import lombok.SneakyThrows;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("patentdoc")
public class PatentDetailDocInfoController {
    private static String URL_DETAIL = "https://cpquery.cponline.cnipa.gov.cn/detail/index?zhuanlisqh={0}&anjianbh";
    private static String TYPE = "发明专利";
    private final PatentDetailMapper patentDetailMapper;
    private final PatentDetailDocInfoMapper patentDetailDocInfoMapper;
    private ObjectMapper objectMapper = new ObjectMapper().setPropertyNamingStrategy(PropertyNamingStrategies.LOWER_CAMEL_CASE).registerModule(new JavaTimeModule());

    public PatentDetailDocInfoController(PatentDetailMapper patentDetailMapper, PatentDetailDocInfoMapper patentDetailDocInfoMapper) {
        this.patentDetailMapper = patentDetailMapper;
        this.patentDetailDocInfoMapper = patentDetailDocInfoMapper;
    }


    @SneakyThrows
    @PostMapping("/getEncodeDetails.do")
    public String getEncodeDetails(String caseStatus,String start, String end) {
        //TODO 先更新下状态 update patent_detail_doc_info pddi left join patent_detail pd on pddi.patent_id=pd.patent_id set pd.status = 1  where pddi.`index`= 1 and pd.status = 0;
        List<PatentDetailDTO> patentDetailDTOs = new ArrayList<>();
        List<PatentDetail> patentDetails = patentDetailMapper.selectByTypeAndCaseStatus(TYPE, caseStatus, Integer.parseInt(start), Integer.parseInt(end));
        for (PatentDetail patentDetail : patentDetails) {
            PatentDetailDTO patentDetailDTO = new PatentDetailDTO();
            patentDetailDTO.setPatentId(patentDetail.getPatentId());
            patentDetailDTO.setApplyId(patentDetail.getApplyId());
            patentDetailDTO.setName(patentDetail.getName());
            patentDetailDTO.setApplyName(patentDetail.getApplyName());
            patentDetailDTO.setCaseStatus(patentDetail.getCaseStatus());
            String urlEncodedUrl = URLEncoder.encode(PatentUtil.encrypt(patentDetail.getPatentId()), StandardCharsets.UTF_8.toString());
            String doubleUrlEncodedUrl = URLEncoder.encode(urlEncodedUrl, StandardCharsets.UTF_8.toString());
            patentDetailDTO.setEncodeUrl(MessageFormat.format(URL_DETAIL, doubleUrlEncodedUrl));
            patentDetailDTOs.add(patentDetailDTO);
        }
        return objectMapper.writeValueAsString(patentDetailDTOs);
    }

    @SneakyThrows
    @PostMapping("/saveDocData.do")
    public void saveDocData(String docData, String patentId) {
        List<PatentDetailDocInfo> patentDetailDocInfos = objectMapper.readValue(docData, new TypeReference<>() {
        });
        for (int i = 0; i < patentDetailDocInfos.size(); i++) {
            patentDetailDocInfos.get(i).setDocInfoId(UUIDUtil.randomUUID());
            patentDetailDocInfos.get(i).setIndex(i + 1);
            String formattedDate = DateTimeUtil.getDatetimeStr(DateTimeUtil.parseLocalDate(patentDetailDocInfos.get(i).getDocDate(), DateTimeUtil.FORMATTER_DATE2), DateTimeUtil.FORMATTER_DATE);
            patentDetailDocInfos.get(i).setDocDate(formattedDate);
            patentDetailDocInfos.get(i).setPatentId(patentId);
        }
        patentDetailDocInfoMapper.deleteByPatentId(patentId);
        patentDetailMapper.updateStatusByPatentId(patentId,1);
        patentDetailDocInfoMapper.insertBatch(patentDetailDocInfos);
    }
}
