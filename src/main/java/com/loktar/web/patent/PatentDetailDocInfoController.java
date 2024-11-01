package com.loktar.web.patent;

import com.azure.ai.documentintelligence.models.AnalyzeResult;
import com.azure.ai.documentintelligence.models.DocumentParagraph;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import com.loktar.domain.patent.PatentDetail;
import com.loktar.domain.patent.PatentDetailDocInfo;
import com.loktar.domain.patent.PatentDetailYitong;
import com.loktar.dto.patent.PatentDetailDTO;
import com.loktar.mapper.patent.PatentDetailDocInfoMapper;
import com.loktar.mapper.patent.PatentDetailMapper;
import com.loktar.mapper.patent.PatentDetailYitongMapper;
import com.loktar.util.AzureDocIntelligenceUtil;
import com.loktar.util.DateTimeUtil;
import com.loktar.util.PatentUtil;
import com.loktar.util.UUIDUtil;
import lombok.SneakyThrows;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("patentdoc")
public class PatentDetailDocInfoController {
    private static String URL_DETAIL = "https://cpquery.cponline.cnipa.gov.cn/detail/index?zhuanlisqh={0}&anjianbh";
    private static String TYPE = "发明专利";
    private final PatentDetailMapper patentDetailMapper;
    private final PatentDetailDocInfoMapper patentDetailDocInfoMapper;
    private final PatentDetailYitongMapper patentDetailYitongMapper;
    private ObjectMapper objectMapper = new ObjectMapper().setPropertyNamingStrategy(PropertyNamingStrategies.LOWER_CAMEL_CASE).registerModule(new JavaTimeModule());

    public PatentDetailDocInfoController(PatentDetailMapper patentDetailMapper, PatentDetailDocInfoMapper patentDetailDocInfoMapper, PatentDetailYitongMapper patentDetailYitongMapper) {
        this.patentDetailMapper = patentDetailMapper;
        this.patentDetailDocInfoMapper = patentDetailDocInfoMapper;
        this.patentDetailYitongMapper = patentDetailYitongMapper;
    }


    @SneakyThrows
    @PostMapping("/getEncodeDetails.do")
    public String getEncodeDetails(String caseStatus, String start, String end) {
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
        patentDetailDocInfoMapper.insertBatch(patentDetailDocInfos);
    }

    @SneakyThrows
    @PostMapping("/updateStatus.do")
    public void updateStatus(String caseStatus, String patentId) {
        patentDetailMapper.updateCaseStatusByPatentId(patentId, caseStatus);
    }

    @GetMapping("/analyze.do")
    public void test() {
        String pdfFolderPath = "F:/OneDrive/Patent/doc/renamed/";
        List<PatentDetail> patentDetails = patentDetailMapper.getNeedAnalyzeDocPatent();
        for (PatentDetail patentDetail : patentDetails) {
            String pdfdocPath = pdfFolderPath + patentDetail.getPatentId() + "-审查文件.pdf";
            File docFile = new File(pdfdocPath);
            if (docFile.exists()) {
                AnalyzeResult analyzeLayoutResult = AzureDocIntelligenceUtil.getAnalyze("prebuilt-layout", pdfdocPath, "2");
                System.out.println(analyzeLayoutResult.toString());
                List<DocumentParagraph> documentParagraphs = analyzeLayoutResult.getParagraphs();
                for (DocumentParagraph documentParagraph : documentParagraphs) {
                    if (documentParagraph.getContent().contains("7.基于上述结论性意见,审查员认为")) {
                        String result = getAnalyzeResult(documentParagraph.getContent());
                        PatentDetailYitong patentDetailYitong = new PatentDetailYitong();
                        patentDetailYitong.setPatentId(patentDetail.getPatentId());
                        patentDetailYitong.setType(result);
                        patentDetailYitongMapper.insert(patentDetailYitong);
                    }
                }
            }else{
                System.out.println("文件不存在");
            }
        }
        System.out.println("done");
    }

    private String getAnalyzeResult(String content) {
        String result = content.replaceAll(":unselected:", "0")
                .replaceAll(":selected:", "1");
        StringBuilder output = new StringBuilder();
        for (char c : result.toCharArray()) {
            if (c == '0' || c == '1') {
                output.append(c);
            }
        }
        return output.toString();
    }

    public static void main(String[] args) {
        renamePdfDoc();
    }

    private static void renamePdfDoc() {
        String pdfFolderPath = "F:/OneDrive/Patent/doc/";
        String renamedFolderPath = pdfFolderPath + "renamed/";
        File pdfFolder = new File(pdfFolderPath);
        List<String> pdfFileNames = Arrays.stream(pdfFolder.listFiles())
                .filter(file -> file.isFile() && file.getName().toLowerCase().endsWith(".pdf"))
                .map(File::getName)
                .collect(Collectors.toList());
        for (String pdfFileName : pdfFileNames) {
            renamePDF(pdfFileName, pdfFolderPath, renamedFolderPath);
        }
    }

    @SneakyThrows
    private static void renamePDF(String pdfFileName, String pdfFolderPath, String renamedFolderPath) {
        File originalFile = new File(pdfFolderPath + pdfFileName);
        String patentId = "";
        PdfReader reader = new PdfReader(originalFile.getPath());
        String pageContent = PdfTextExtractor.getTextFromPage(reader, 1);
        String[] lines = pageContent.split("\n");
        for (String line : lines) {
            if (line.contains("申请号：")) {
                patentId = extractApplicationNumber(line);
                patentId = patentId.replace(".", "");
                break;
            }
        }
        reader.close();
        if (patentId != null && !patentId.isEmpty()) {
            File newFile = new File(renamedFolderPath + patentId + "-审查文件.pdf");
            try {
                Files.copy(originalFile.toPath(), newFile.toPath());
            } catch (IOException e) {
                System.out.println("Failed to copy: " + originalFile.getName());
                e.printStackTrace();
            }
        }
    }

    public static String extractApplicationNumber(String text) {
        String regex = "申请号：(\\d+\\.(\\d+|X))";
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(regex);
        java.util.regex.Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return matcher.group(1);
        } else {
            return null;
        }
    }

}
