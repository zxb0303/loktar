package com.loktar.web.child;


import lombok.extern.slf4j.Slf4j;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.loktar.conf.LokTarConstant;
import com.loktar.util.DateTimeUtil;
import lombok.Data;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/*
 * 杭州市西湖区青少年宫课程查询
 */
@Slf4j
public class QSNG {

    private static final String LIST_URL = "https://bm.qsng.cn/eduplat/api/public/ic/iclass/list";
    // 区域
    private static final Map<String, String> AREA_MAP = new LinkedHashMap<>() {{
        put("0", "三墩宫");
        put("1", "西溪宫");
    }};
    // 学期
    private static final Map<String, String> TERM_MAP = new LinkedHashMap<>() {{
        put("0", "春季");
        put("1", "暑期");
        put("2", "秋季");
        put("4", "学校暑托");
        put("5", "假托管");
        put("6", "秋假托管");
    }};
    private static final String AREA = "0";
    private static final String ORG_ID = "531B60102C351F6C012C38872767000B";
    private static final String YEAR = "2026";
    private static final String TERM = "1";
    private static final String SPEL_ID = "";
    private static final String HAS_SYME = "";
    private static final String EXCEL_DIR = "F:/";
    private static final int PAGE_SIZE = 100;
    private static final String COOKIE_VALUE = "edu.session.id=060260b7-8ade-43cc-ae81-197724bbc26b; SERVERID=86974b1c5d3c68ca9a53fce778a015a6|1780281137|1780281115";
    private static final ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    public static void main(String[] args) throws Exception {
        List<QSNGBean> allBeans = new ArrayList<>();
        int pageNo = 1;
        int totalCount = 0;
        HttpClient httpClient = HttpClient.newHttpClient();
        while (true) {
            String formData = buildFormData(AREA, pageNo, PAGE_SIZE, ORG_ID, YEAR, TERM, SPEL_ID, HAS_SYME);
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(LIST_URL))
                    .header(LokTarConstant.HTTP_HEADER_ACCEPT_NAME, LokTarConstant.HTTP_HEADER_CONTENT_TYPE_VALUE_JSON)
                    .header(LokTarConstant.HTTP_HEADER_ACCEPT_LANGUAGE_NAME, LokTarConstant.HTTP_HEADER_ACCEPT_LANGUAGE_VALUE_CN)
                    .header(LokTarConstant.HTTP_HEADER_CONTENT_TYPE_NAME, LokTarConstant.HTTP_HEADER_CONTENT_TYPE_VALUE_FORM)
                    .header(LokTarConstant.HTTP_HEADER_COOKIE_NAME, COOKIE_VALUE)
                    .header(LokTarConstant.HTTP_HEADER_USER_AGENT_NAME, LokTarConstant.HTTP_HEADER_USER_AGENT_VALUE)
                    .POST(HttpRequest.BodyPublishers.ofString(formData))
                    .build();
            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            log.info("{}", "第 " + pageNo + " 页, 响应状态码: " + response.statusCode());
            QSNGResponse qsngResponse = objectMapper.readValue(response.body(), QSNGResponse.class);
            if (!Boolean.TRUE.equals(qsngResponse.getSuccess()) || qsngResponse.getData() == null) {
                log.info("{}", "请求失败: " + qsngResponse.getMsg());
                break;
            }
            totalCount = qsngResponse.getData().getTotal();
            List<QSNGRow> rows = qsngResponse.getData().getRows();
            if (rows == null || rows.isEmpty()) {
                break;
            }
            for (QSNGRow row : rows) {
                if (row.getBean() != null) {
                    allBeans.add(row.getBean());
                }
            }
            log.info("{}", "已获取: " + allBeans.size() + "/" + totalCount);
            if (allBeans.size() >= totalCount) {
                break;
            }
            pageNo++;
        }

        log.info("{}", "\n========== 查询完成 ==========");
        log.info("{}", "总数: " + allBeans.size());
//        for (int i = 0; i < allBeans.size(); i++) {
//            QSNGBean bean = allBeans.get(i);
//            QSNGCaption caption = bean.getCaption();
//            System.out.println("------------------- [" + (i + 1) + "]");
//            System.out.println("校区: " + (caption != null ? caption.getArea() : ""));
//            System.out.println("班级名称: " + bean.getName());
//            System.out.println("班级编码: " + bean.getCode());
//            System.out.println("等级: " + (caption != null ? caption.getDegree() : ""));
//            System.out.println("学员年级范围: " + (caption != null ? caption.getGradeMin() : ""));
//            System.out.println("学员年级范围: " + (caption != null ? caption.getGradeMax() : ""));
//            System.out.println("学员出生日期范围: " + DateTimeUtil.getDatetimeStr(DateTimeUtil.convertSecondsToDateTime(bean.getAgeMin() / 1000), DateTimeUtil.FORMATTER_DATE));
//            System.out.println("学员出生日期范围: " + DateTimeUtil.getDatetimeStr(DateTimeUtil.convertSecondsToDateTime(bean.getAgeMax() / 1000), DateTimeUtil.FORMATTER_DATE));
//            System.out.println("教师: " + bean.getTeacherName());
//            System.out.println("学费: " + bean.getTotalFee());
//            System.out.println("上课时间: " + getScheduleTime(bean.getSchedule()));
//            System.out.println("上课教室: " + getScheduleRoom(bean.getSchedule()));
//            System.out.println("课次: " + bean.getClassTime());
//            System.out.println("开始日期: " + (bean.getBeginDate() != null ? DateTimeUtil.getDatetimeStr(DateTimeUtil.convertSecondsToDateTime(bean.getBeginDate() / 1000), DateTimeUtil.FORMATTER_DATE) : ""));
//            System.out.println("结束日期: " + (bean.getEndDate() != null ? DateTimeUtil.getDatetimeStr(DateTimeUtil.convertSecondsToDateTime(bean.getEndDate() / 1000), DateTimeUtil.FORMATTER_DATE) : ""));
//            System.out.println("描述: " + bean.getDescription());
//        }
        saveToExcel(allBeans);
    }

    private static String buildFormData(String area, int pageNo, int pageSize, String orgId,
                                        String year, String term, String spelId, String hasSyme) {
        return "area=" + encode(area)
                + "&pageNo=" + pageNo
                + "&pageSize=" + pageSize
                + "&orgId=" + encode(orgId)
                + "&year=" + encode(year)
                + "&term=" + encode(term)
                + "&spelId=" + encode(spelId)
                + "&hasSyme=" + encode(hasSyme);
    }

    private static void saveToExcel(List<QSNGBean> allBeans) throws Exception {
        String[] headers = {"校区", "班级名称", "班级编码", "等级", "学员年级(小)", "学员年级(大)", "出生日期(小)", "出生日期(大)", "教师", "学费", "上课时间", "上课教室", "课次", "开始日期", "结束日期", "描述"};
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("班级列表");
            // 表头加粗样式
            CellStyle headerStyle = workbook.createCellStyle();
            Font boldFont = workbook.createFont();
            boldFont.setBold(true);
            headerStyle.setFont(boldFont);
            // 表头
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                org.apache.poi.ss.usermodel.Cell headerCell = headerRow.createCell(i);
                headerCell.setCellValue(headers[i]);
                headerCell.setCellStyle(headerStyle);
            }
            // 数据行
            for (int i = 0; i < allBeans.size(); i++) {
                QSNGBean bean = allBeans.get(i);
                QSNGCaption caption = bean.getCaption();
                Row row = sheet.createRow(i + 1);
                row.createCell(0).setCellValue(caption != null ? caption.getArea() : "");
                row.createCell(1).setCellValue(bean.getName());
                row.createCell(2).setCellValue(bean.getCode());
                row.createCell(3).setCellValue(caption != null ? caption.getDegree() : "");
                row.createCell(4).setCellValue(caption != null ? caption.getGradeMin() : "");
                row.createCell(5).setCellValue(caption != null ? caption.getGradeMax() : "");
                row.createCell(6).setCellValue(DateTimeUtil.getDatetimeStr(DateTimeUtil.convertSecondsToDateTime(bean.getAgeMin() / 1000), DateTimeUtil.FORMATTER_DATE));
                row.createCell(7).setCellValue(DateTimeUtil.getDatetimeStr(DateTimeUtil.convertSecondsToDateTime(bean.getAgeMax() / 1000), DateTimeUtil.FORMATTER_DATE));
                row.createCell(8).setCellValue(bean.getTeacherName());
                row.createCell(9).setCellValue(bean.getTotalFee() != null ? bean.getTotalFee() : 0);
                row.createCell(10).setCellValue(getScheduleTime(bean.getSchedule()));
                row.createCell(11).setCellValue(getScheduleRoom(bean.getSchedule()));
                row.createCell(12).setCellValue(bean.getClassTime() != null ? bean.getClassTime() : 0);
                row.createCell(13).setCellValue(bean.getBeginDate() != null ? DateTimeUtil.getDatetimeStr(DateTimeUtil.convertSecondsToDateTime(bean.getBeginDate() / 1000), DateTimeUtil.FORMATTER_DATE) : "");
                row.createCell(14).setCellValue(bean.getEndDate() != null ? DateTimeUtil.getDatetimeStr(DateTimeUtil.convertSecondsToDateTime(bean.getEndDate() / 1000), DateTimeUtil.FORMATTER_DATE) : "");
                row.createCell(15).setCellValue(bean.getDescription());
            }
            // 自动筛选
            sheet.setAutoFilter(new org.apache.poi.ss.util.CellRangeAddress(0, allBeans.size(), 0, headers.length - 1));
            try (FileOutputStream fos = new FileOutputStream(getExcelPath())) {
                workbook.write(fos);
            }
            log.info("{}", "Excel已保存: " + getExcelPath());
        }
    }

    private static String getExcelPath() {
        String areaName = AREA_MAP.getOrDefault(AREA, AREA);
        String termName = TERM_MAP.getOrDefault(TERM, TERM);
        return EXCEL_DIR + areaName + "-" + YEAR + "-" + termName + ".xlsx";
    }

    private static String getScheduleTime(String schedule) {
        if (schedule == null || schedule.isEmpty()) return "";
        int lastSpace = schedule.lastIndexOf(" ");
        return lastSpace > 0 ? schedule.substring(0, lastSpace) : schedule;
    }

    private static String getScheduleRoom(String schedule) {
        if (schedule == null || schedule.isEmpty()) return "";
        int lastSpace = schedule.lastIndexOf(" ");
        return lastSpace > 0 ? schedule.substring(lastSpace + 1) : "";
    }

    private static String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class QSNGResponse {
        private String msg;
        private String code;
        private QSNGData data;
        private Boolean success;
        private String version;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class QSNGData {
        private Integer total;
        private List<QSNGRow> rows;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class QSNGRow {
        private QSNGBean bean;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class QSNGBean {
        private String id;
        // 班级名称
        private String name;
        // 班级编码
        private String code;
        private String year;
        private String term;
        private String spelId;
        private String spelParentId;
        private String spelName;
        private String degree;
        // 开班日期
        private Long beginDate;
        // 结束日期
        private Long endDate;
        private Integer classTime;
        private Double hourRate;
        private Double classHour;
        private String teacherId;
        // 教师名称
        private String teacherName;
        private Integer recruitNum;
        private Integer reserveNum;
        private Integer applyNum;
        private Integer reserveUsedNum;
        private Integer totalUsedNum;
        private Integer applyUsedNum;
        private Integer onLineNum;
        private Integer registrableNum;
        // 描述
        private String description;
        private String gradeMin;
        private String gradeMax;
        // 年龄范围
        private Long ageMin;
        // 年龄范围
        private Long ageMax;
        private String status;
        private String enrollType;
        private String area;
        private String orgId;
        // 费用
        private Double fee;
        private Double bookFee;
        private Double totalFee;
        private Integer payNum;
        // 上课时间
        private String schedule;
        private QSNGCaption caption;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class QSNGCaption {
        private String area;
        private String enrollType;
        private String degree;
        private String gradeMax;
        private String term;
        private String gradeMin;
    }
}
