package com.loktar.web.patent;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.loktar.domain.patent.PatentPdf;
import com.loktar.domain.patent.PatentPdfApply;
import com.loktar.mapper.patent.PatentPdfApplyMapper;
import com.loktar.mapper.patent.PatentPdfMapper;
import com.loktar.service.patent.PatentService;
import com.loktar.util.PatentPdfUtil;
import lombok.SneakyThrows;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("patentpdf")
public class PatentPdfController {
    private static String basepath = "F:/loktar/patent/2022/{0}.pdf";
    private static String type = "实用新型";
    private final PatentPdfMapper patentPdfMapper;
    private final PatentPdfApplyMapper patentPdfApplyMapper;
    private final PatentService patentService;

    private ObjectMapper objectMapper = new ObjectMapper().setPropertyNamingStrategy(PropertyNamingStrategies.LOWER_CAMEL_CASE).registerModule(new JavaTimeModule());

    public PatentPdfController(PatentPdfMapper patentPdfMapper, PatentPdfApplyMapper patentPdfApplyMapper, PatentService patentService) {
        this.patentPdfMapper = patentPdfMapper;
        this.patentPdfApplyMapper = patentPdfApplyMapper;
        this.patentService = patentService;
        objectMapper.registerModule(new JavaTimeModule());
    }


    @SneakyThrows
    @GetMapping("/deal.do")
    public void deal(String filename) {
        String filepath = MessageFormat.format(basepath, filename);
        List<PatentPdf> patentPdfs = PatentPdfUtil.dealPatentPdf(filepath, type);
        List<List<PatentPdf>> splitLists = splitList(patentPdfs, 10000);
        for (List<PatentPdf> list : splitLists) {
            patentPdfMapper.insertBatch(list);
        }
    }

    @SneakyThrows
    @GetMapping("/get.do")
    public String get(String limit) {
        int status = 0;
        List<PatentPdfApply> patentPdfApplys = patentPdfApplyMapper.selectByStatusAndLimit(status, Integer.parseInt(limit));
        return objectMapper.writeValueAsString(patentPdfApplys);
    }

    @SneakyThrows
    @PostMapping("/set.do")
    public void set(String applyId, String patentCount, String detail) {
        System.out.println("start:" + LocalDateTime.now());
        patentService.deal(applyId, Integer.parseInt(patentCount), detail);
        System.out.println("end:" + LocalDateTime.now());

    }


    @GetMapping("/dealAll.do")
    public void dealAll() {
        String pdfFolderPath = "F:/loktar/patent/2022/";
        File pdfFolder = new File(pdfFolderPath);
        File[] pdfFiles = pdfFolder.listFiles((dir, name) -> name.toLowerCase().endsWith(".pdf"));
        for (File pdfFile : pdfFiles) {
            String filename = pdfFile.getName().replace(".pdf", "");
            System.out.println(filename);
            deal(filename);
        }
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
