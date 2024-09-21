package com.loktar.web.patent;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.loktar.domain.patent.PatentApply;
import com.loktar.domain.patent.PatentDetail;
import com.loktar.domain.patent.PatentPdf;
import com.loktar.domain.patent.PatentPdfApply;
import com.loktar.domain.qywx.QywxPatentMsg;
import com.loktar.dto.patent.PatentContractDTO;
import com.loktar.dto.patent.PatentDetailDTO;
import com.loktar.mapper.patent.*;
import com.loktar.mapper.qywx.QywxPatentMsgMapper;
import com.loktar.service.patent.PatentService;
import com.loktar.util.DateTimeUtil;
import com.loktar.util.NumberToChineseUtil;
import com.loktar.util.PatentPdfUtil;
import com.loktar.util.PatentUtil;
import lombok.SneakyThrows;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("patentpdf")
public class PatentPdfController {
    private static String BASEPATH = "F:/loktar/patent/2020/";
    private static String FILENAME = "{0}.pdf";
    private static String URL_DETAIL = "https://cpquery.cponline.cnipa.gov.cn/detail/index?zhuanlisqh={0}&anjianbh";
    private static String TYPE = "实用新型";
    private final PatentPdfMapper patentPdfMapper;
    private final PatentPdfApplyMapper patentPdfApplyMapper;
    private final PatentService patentService;
    private final PatentDetailMapper patentDetailMapper;
    private final PatentApplyMapper patentApplyMapper;
    private final CompanyInfoCqqMapper companyInfoCqqMapper;
    private final QywxPatentMsgMapper qywxPatentMsgMapper;

    private ObjectMapper objectMapper = new ObjectMapper().setPropertyNamingStrategy(PropertyNamingStrategies.LOWER_CAMEL_CASE).registerModule(new JavaTimeModule());

    public PatentPdfController(PatentPdfMapper patentPdfMapper, PatentPdfApplyMapper patentPdfApplyMapper, PatentService patentService, PatentDetailMapper patentDetailMapper, PatentApplyMapper patentApplyMapper, CompanyInfoCqqMapper companyInfoCqqMapper, QywxPatentMsgMapper qywxPatentMsgMapper) {
        this.patentPdfMapper = patentPdfMapper;
        this.patentPdfApplyMapper = patentPdfApplyMapper;
        this.patentService = patentService;
        this.patentDetailMapper = patentDetailMapper;
        this.patentApplyMapper = patentApplyMapper;
        this.companyInfoCqqMapper = companyInfoCqqMapper;
        this.qywxPatentMsgMapper = qywxPatentMsgMapper;
    }

    // Uipath 生成合同协议使用
    @SneakyThrows
    @PostMapping("/getContractDTO.do")
    public String getContractDTO(String applyName, String price) {
        PatentContractDTO patentContractDTO = companyInfoCqqMapper.getPatentContractDTOByApplyName(applyName);
        List<PatentDetail> patentDetails = patentDetailMapper.selectForQuote(patentContractDTO.getApplyId(), TYPE);
        patentContractDTO.setPatentDetails(patentDetails);
        patentContractDTO.setPrice(price);
        patentContractDTO.setPriceChinese(NumberToChineseUtil.numberToChinese(Integer.parseInt(price)));
        patentContractDTO.setDate(DateTimeUtil.getDatetimeStr(LocalDateTime.now(), DateTimeUtil.FORMATTER_DATE_CHINESE));
//        System.out.println(patentContractDTO.toString());
        return objectMapper.writeValueAsString(patentContractDTO);
    }

    //处理专利公告文件使用
    @SneakyThrows
    @GetMapping("/deal.do")
    public void deal(String filename) {
        String filepath = MessageFormat.format(BASEPATH + FILENAME, filename);
        List<PatentPdf> patentPdfs = PatentPdfUtil.dealPatentPdf(filepath, TYPE);
        List<List<PatentPdf>> splitLists = splitList(patentPdfs, 10000);
        for (List<PatentPdf> list : splitLists) {
            patentPdfMapper.insertBatch(list);
        }
    }

    //处理专利公告文件使用
    @GetMapping("/dealAll.do")
    public void dealAll() {
        File pdfFolder = new File(BASEPATH);
        File[] pdfFiles = pdfFolder.listFiles((dir, name) -> name.toLowerCase().endsWith(".pdf"));
        for (File pdfFile : pdfFiles) {
            String filename = pdfFile.getName().replace(".pdf", "");
            System.out.println(filename);
            deal(filename);
        }
    }

    //Uipath 专利获取使用
    @SneakyThrows
    @GetMapping("/get.do")
    public String get(String status, String start, String end) {
        int statusInt = Integer.parseInt(status);
        List<PatentPdfApply> patentPdfApplys = patentPdfApplyMapper.selectByStatusAndLimit(statusInt, Integer.parseInt(start), Integer.parseInt(end));
        return objectMapper.writeValueAsString(patentPdfApplys);
    }

    //Uipath 专利获取使用
    @SneakyThrows
    @PostMapping("/set.do")
    public void set(String applyId, String patentCount, String detail) {
        patentService.deal(applyId, Integer.parseInt(patentCount), detail);

    }

    //Uipath 生成报价单时使用
    @SneakyThrows
    @PostMapping("/getEncodeDetails.do")
    public String getEncodeDetails(String applyName) {
        PatentApply patentApply = patentApplyMapper.selectByApplyName(applyName);
        List<PatentDetailDTO> patentDetailDTOs = new ArrayList<>();

//        List<PatentDetail> patentDetails = new ArrayList<>();
//        //专利权维持 年费待缴 无滞纳金
//        PatentDetail patentDetail1 = patentDetailMapper.selectByPrimaryKey("2022220292799");
//        //专利权维持 年费已缴
//        PatentDetail patentDetail2 = patentDetailMapper.selectByPrimaryKey("2022215620558");
//        //等年费滞纳金 只欠年费+滞纳金 第1期
//        PatentDetail patentDetail3 = patentDetailMapper.selectByPrimaryKey("2022207869427");
//        //等年费滞纳金 只欠年费+滞纳金 第5期
//        PatentDetail patentDetail4 = patentDetailMapper.selectByPrimaryKey("2021230211632");
//        //等年费滞纳金 只欠年费+滞纳金 超过第5期
//        PatentDetail patentDetail5 = patentDetailMapper.selectByPrimaryKey("2020224325275");
//        //未做费减
//        PatentDetail patentDetail6 = patentDetailMapper.selectByPrimaryKey("2020226489481");

//        //其他状态：未缴年费专利权终止，等恢复；合议组审查；届满终止失效等 不需要出表格
        //PatentDetail patentDetail4 = patentDetailMapper.selectByPrimaryKey("2022219221561");
//        patentDetails.add(patentDetail1);
//        patentDetails.add(patentDetail2);
//        patentDetails.add(patentDetail3);
//        patentDetails.add(patentDetail4);
//        patentDetails.add(patentDetail5);
//        patentDetails.add(patentDetail6);
        List<PatentDetail> patentDetails = patentDetailMapper.selectForQuote(patentApply.getApplyId(), TYPE);
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

    //Uipath 自动生成报价单及合同协议使用
    @SneakyThrows
    @PostMapping("/getQywxPatentMsg.do")
    public String getQywxPatentMsg(String status) {
        List<QywxPatentMsg> qywxPatentMsgs = qywxPatentMsgMapper.getQywxPatentMsgsByStatus(status);
        return objectMapper.writeValueAsString(qywxPatentMsgs);
    }

    //Uipath 自动生成报价单及合同协议使用
    @SneakyThrows
    @PostMapping("/updateQywxPatentStatus.do")
    public void updateQywxPatentStatus(String id, String status) {
        qywxPatentMsgMapper.updateQywxPatentStatusById(Integer.parseInt(id), status);
    }

    public static <T> List<List<T>> splitList(List<T> largeList, int chunkSize) {
        List<List<T>> lists = new ArrayList<>();
        for (int i = 0; i < largeList.size(); i += chunkSize) {
            int end = Math.min(i + chunkSize, largeList.size());
            lists.add(new ArrayList<>(largeList.subList(i, end)));
        }
        return lists;
    }

}
