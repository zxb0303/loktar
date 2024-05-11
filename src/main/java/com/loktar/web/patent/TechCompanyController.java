package com.loktar.web.patent;

import com.loktar.domain.patent.TechCompany;
import com.loktar.mapper.patent.TechCompanyMapper;
import com.loktar.util.UUIDUtil;
import lombok.SneakyThrows;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("tech")
public class TechCompanyController {
    private final TechCompanyMapper techCompanyMapper;

    public TechCompanyController(TechCompanyMapper techCompanyMapper) {
        this.techCompanyMapper = techCompanyMapper;
    }

    @SneakyThrows
    @GetMapping("/gen.do")
    public void gen(String year) {
        List<TechCompany> techCompanys = new ArrayList<>();
        String filePath = "F:/loktar/tech/" + year + ".pdf";

        File pdf = new File(filePath);
        PDDocument document = Loader.loadPDF(pdf);
        PDFTextStripper pdfStripper = new PDFTextStripper();
        int numPages = document.getNumberOfPages();
        for (int i = 1; i <= numPages; i++) {
            pdfStripper.setStartPage(i);
            pdfStripper.setEndPage(i);
            String text = pdfStripper.getText(document);
            String[] lines = text.split("\r\n");
            Pattern pattern = Pattern.compile("^(\\d+)\\s+(.*)");
            for (int j = 0; j < lines.length; j++) {
                String line = lines[j];
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    String number = matcher.group(1);
                    String companyName = matcher.group(2).replace("（","(").replace("）",")");
                    System.out.println("序号: " + number + ", 公司名称: " + companyName);
                    TechCompany techCompany = new TechCompany();
                    techCompany.setCompanyId(UUIDUtil.randomUUID());
                    techCompany.setName(companyName);
                    techCompany.setYear(Integer.parseInt(year));
                    techCompany.setIndex(Integer.parseInt(number));
                    techCompanys.add(techCompany);
                }
            }
        }
        techCompanyMapper.insertBatch(techCompanys);
    }


}
