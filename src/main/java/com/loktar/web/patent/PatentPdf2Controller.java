package com.loktar.web.patent;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.loktar.domain.patent.PatentApply;
import com.loktar.domain.patent.PatentDetail;
import com.loktar.dto.patent.PatentContractDTO;
import com.loktar.dto.patent.PatentDetailDTO;
import com.loktar.mapper.patent.CompanyInfoCqqMapper;
import com.loktar.mapper.patent.PatentApplyMapper;
import com.loktar.mapper.patent.PatentDetailMapper;
import com.loktar.util.DateTimeUtil;
import com.loktar.util.NumberToChineseUtil;
import com.loktar.util.PatentUtil;
import lombok.SneakyThrows;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("patentpdfv2")
public class PatentPdf2Controller {
    //报价单及合同协议第二版，包含发明
    private static String URL_DETAIL = "https://cpquery.cponline.cnipa.gov.cn/detail/index?zhuanlisqh={0}&anjianbh";
    private final PatentDetailMapper patentDetailMapper;
    private final PatentApplyMapper patentApplyMapper;
    private final CompanyInfoCqqMapper companyInfoCqqMapper;

    private ObjectMapper objectMapper = new ObjectMapper().setPropertyNamingStrategy(PropertyNamingStrategies.LOWER_CAMEL_CASE).registerModule(new JavaTimeModule());

    public PatentPdf2Controller(PatentDetailMapper patentDetailMapper, PatentApplyMapper patentApplyMapper, CompanyInfoCqqMapper companyInfoCqqMapper) {
        this.patentDetailMapper = patentDetailMapper;
        this.patentApplyMapper = patentApplyMapper;
        this.companyInfoCqqMapper = companyInfoCqqMapper;
    }


    // Uipath 生成合同协议使用
    @SneakyThrows
    @PostMapping("/getContractDTO.do")
    public String getContractDTO(String applyName, String price) {
        PatentContractDTO patentContractDTO = companyInfoCqqMapper.getPatentContractDTOByApplyName(applyName);
        List<PatentDetail> patentDetails = patentDetailMapper.selectForQuoteV2(patentContractDTO.getApplyId());
        patentContractDTO.setPatentDetails(patentDetails);
        patentContractDTO.setPrice(price);
        patentContractDTO.setPriceChinese(NumberToChineseUtil.numberToChinese(Integer.parseInt(price)));
        patentContractDTO.setDate(DateTimeUtil.getDatetimeStr(LocalDateTime.now(), DateTimeUtil.FORMATTER_DATE_CHINESE));
        return objectMapper.writeValueAsString(patentContractDTO);
    }

    //Uipath 生成报价单时使用
    @SneakyThrows
    @PostMapping("/getEncodeDetails.do")
    public String getEncodeDetails(String applyName) {
        PatentApply patentApply = patentApplyMapper.selectByApplyName(applyName);
        List<PatentDetailDTO> patentDetailDTOs = new ArrayList<>();
        List<PatentDetail> patentDetails = patentDetailMapper.selectForQuoteV2(patentApply.getApplyId());
        for (PatentDetail patentDetail : patentDetails) {
            PatentDetailDTO patentDetailDTO = new PatentDetailDTO();
            patentDetailDTO.setPatentId(patentDetail.getPatentId());
            patentDetailDTO.setApplyId(patentDetail.getApplyId());
            patentDetailDTO.setType(patentDetail.getType());
            patentDetailDTO.setName(patentDetail.getName());
            patentDetailDTO.setApplyName(patentDetail.getApplyName());
            patentDetailDTO.setCaseStatus(patentDetail.getCaseStatus());
            patentDetailDTO.setStatus(patentDetail.getStatus());
            String urlEncodedUrl = URLEncoder.encode(PatentUtil.encrypt(patentDetail.getPatentId()), StandardCharsets.UTF_8.toString());
            String doubleUrlEncodedUrl = URLEncoder.encode(urlEncodedUrl, StandardCharsets.UTF_8.toString());
            patentDetailDTO.setEncodeUrl(MessageFormat.format(URL_DETAIL, doubleUrlEncodedUrl));
            patentDetailDTOs.add(patentDetailDTO);
        }
        return objectMapper.writeValueAsString(patentDetailDTOs);
    }
}
