package com.loktar.web.cxy;

import com.loktar.dto.cxy.HardWork;
import com.loktar.dto.cxy.Leave;
import com.loktar.dto.cxy.RestInfo;
import com.loktar.util.DateTimeUtil;
import lombok.SneakyThrows;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.poi.openxml4j.util.ZipSecureFile;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.DefaultIndexedColorMap;
import org.apache.poi.xssf.usermodel.XSSFColor;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.*;
import java.util.*;

public class AttendanceMain {

    //style需要缓存，否则太多个会卡，文件也会变大
    private final static Map<String, CellStyle> STYLE_MAP = new HashMap<>();
    // TODO 需要预处理daily表中是否都是正常班和正常晚班
    // TODO 需要预处理daily表中日期格式带不带-
    private final static String MONTH = "202412";
    private final static String MAX_DAY = "31";

    // TODO  30天的请假数据是从85列开始，31天的请假数据是从86列开始
    private final static int columns = 86;

    // TODO 法定补班日 eg:"2023-06-25"
    private final static String[] SPECIALWORKDAYS = new String[]{};
    // TODO 法定节假日（除周末） eg:"2023-06-22", "2023-06-23"
    private final static String[] SPECIALWEEKDAYS = new String[]{};

    private final static String ATTENDANCE_PATH = "";

    private final static String DAILY_FILE_PATH = ATTENDANCE_PATH + MONTH + "/每日统计_" + MONTH + "01_" + MONTH + MAX_DAY + ".xlsx";
    private final static String MONTH_FILE_PATH = ATTENDANCE_PATH + MONTH + "/月度汇总_" + MONTH + "01_" + MONTH + MAX_DAY + ".xlsx";
    private final static String RESULT_FILE_PATH = ATTENDANCE_PATH + MONTH + "/result_" + MONTH + ".xlsx";
    private final static String TEMPLATE_FILE_PATH = ATTENDANCE_PATH + "template.xlsx";


    @SneakyThrows
    public static void main(String[] args) {
        deal();
    }

    @SneakyThrows
    private static void deal() {
        ZipSecureFile.setMinInflateRatio(-1.0d);
        InputStream oldInp = new FileInputStream(DAILY_FILE_PATH);
        Workbook oldWb = WorkbookFactory.create(oldInp);
        InputStream newInp = new FileInputStream(TEMPLATE_FILE_PATH);
        Workbook newWb = WorkbookFactory.create(newInp);

        List<Leave> leaves = getLeave();
        List<HardWork> hardWorks = getHardWork();
        Map<String, RestInfo> restInfoMap = new LinkedHashMap<>();


        //创建字体
        Font font = newWb.createFont();
        font.setFontHeightInPoints((short) 12);

        //创建单元格格式1 橙色
        CellStyle cellStyle1 = newWb.createCellStyle();
        XSSFColor xssfColor1 = new XSSFColor(new DefaultIndexedColorMap());
        xssfColor1.setRGB(new byte[]{(byte) 248, (byte) 203, (byte) 173});
        cellStyle1.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        cellStyle1.setFillForegroundColor(xssfColor1);
        cellStyle1.setFont(font);

        //创建单元格格式2 黄色
        CellStyle cellStyle2 = newWb.createCellStyle();
        XSSFColor xssfColor2 = new XSSFColor(new DefaultIndexedColorMap());
        xssfColor2.setRGB(new byte[]{(byte) 255, (byte) 255, (byte) 0});
        cellStyle2.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        cellStyle2.setFillForegroundColor(xssfColor2);
        cellStyle2.setFont(font);

        //创建单元格格式3 绿色
        CellStyle cellStyle3 = newWb.createCellStyle();
        XSSFColor xssfColor3 = new XSSFColor(new DefaultIndexedColorMap());
        xssfColor3.setRGB(new byte[]{(byte) 6, (byte) 255, (byte) 90});
        cellStyle3.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        cellStyle3.setFillForegroundColor(xssfColor3);
        cellStyle3.setFont(font);

        Sheet oldSheet = oldWb.getSheetAt(0);
        Sheet newSheet = newWb.getSheetAt(1);
        Sheet newSheetRest = newWb.getSheetAt(2);
        for (int rowNum = 2; rowNum <= oldSheet.getLastRowNum(); rowNum++) {
            System.out.println(rowNum);
            Row oldRow = oldSheet.getRow(rowNum);
            if (ObjectUtils.isEmpty(oldRow.getCell(0).getStringCellValue())) {
                continue;
            }
            Row newRow = newSheet.createRow(rowNum);
            //复制数据
            //第1列 姓名
            copyCell(newWb, oldRow, 0, newRow, 0);
            //第2列 部门
            copyCell(newWb, oldRow, 2, newRow, 1);
            //第3列 工号
            copyCell(newWb, oldRow, 1, newRow, 2);
            //第4列 考勤组名称 隐藏
            copyCell(newWb, oldRow, 16, newRow, 3);
            //第6列 日期
            copyCell(newWb, oldRow, 12, newRow, 5);
            //第7列 班次 隐藏
            copyCell(newWb, oldRow, 15, newRow, 6);
            //第8列 第一次 打卡时间
            copyCell(newWb, oldRow, 18, newRow, 7);
            //第9列 第一次 打卡结果
            copyCell(newWb, oldRow, 19, newRow, 8);
            //第10列 第二次 打卡时间
            copyCell(newWb, oldRow, 23, newRow, 9);
            //第11列 第二次 打卡结果
            copyCell(newWb, oldRow, 24, newRow, 10);
            //第12列 应出勤时长(小时)
            copyCell(newWb, oldRow, 29, newRow, 11);
            //第12列 实际出勤时长(小时)
            copyCell(newWb, oldRow, 32, newRow, 12);
            //第5列 出勤
            // 先统一设置为0
            // 再设置工作日为1
            // 再设置法定上班日为1
            // 再设置法定节假日为0
            Cell newCell = newRow.createCell(4);
            String date1 = newRow.getCell(5).getStringCellValue();
            LocalDate localDate1 = DateTimeUtil.parseLocalDate(date1, DateTimeUtil.FORMATTER_DATE);
            newCell.setCellValue(0);
            if (localDate1.getDayOfWeek() != DayOfWeek.SATURDAY && localDate1.getDayOfWeek() != DayOfWeek.SUNDAY) {
                newCell.setCellValue(1);
            }
            if (Arrays.asList(SPECIALWORKDAYS).contains(date1)) {
                newCell.setCellValue(1);
            }
            if (Arrays.asList(SPECIALWEEKDAYS).contains(date1)) {
                newCell.setCellValue(0);
            }

            //姓名
            Cell nameCell = newRow.getCell(0);
            //部门
            Cell deptCell = newRow.getCell(1);
            //工号
            Cell jobNoCell = newRow.getCell(2);
            //出勤
            Cell attendanceCell = newRow.getCell(4);
            //日期
            Cell dateCell = newRow.getCell(5);
            //班次
            Cell classCell = newRow.getCell(6);
            //上班打卡时间
            Cell checkInTimeCell = newRow.getCell(7);
            //上班打卡结果
            Cell checkInResultCell = newRow.getCell(8);
            //下班打卡时间
            Cell checkOutTimeCell = newRow.getCell(9);
            //下班打卡结果
            Cell checkOutResultCell = newRow.getCell(10);
            //加班总时长(小时)
            Cell applyHardWorkCell = newRow.getCell(12);
            //假期类型
            Cell leaveTypeCell = newRow.createCell(13);
            //休假天数
            Cell leaveTimeCell = newRow.createCell(14);
            //工作日加班标记
            Cell weekdayOverTimeFlagCell = newRow.createCell(15);
            //周末加班标记
            Cell weekendOverTimeFlagCell = newRow.createCell(16);
            //周末晚班标记
            Cell weekendHardTimeFlagCell = newRow.createCell(17);
            //迟到标记
            Cell LateAFlagCell = newRow.createCell(19);
            Cell LateBFlagCell = newRow.createCell(20);
            Cell LateCFlagCell = newRow.createCell(21);
            //当天日期
            String date = dateCell.getStringCellValue();
            //TODO 需要预处理daily表中日期格式带不带-
            //calendar.setTime(DateUtil.parase(date, DateUtil.DATEFORMAT2));
            LocalDate localDate = DateTimeUtil.parseLocalDate(date, DateTimeUtil.FORMATTER_DATE);

            //标记迟到
            //正常班或者正常晚班 上班打卡结果包含迟到
            // 且 出勤是1
            //上班打卡时间大于09:15，标记为迟到A(09:15-09:30)迟到B(09:30-10:00)迟到C(10:00~)
            if (checkInResultCell.getStringCellValue().contains("迟到") && (classCell.getStringCellValue().contains("正常班") || classCell.getStringCellValue().contains("正常晚班"))
                    && attendanceCell.getNumericCellValue() == 1) {
                if (checkInTimeCell.getStringCellValue().trim().compareTo("09:15") > 0 && checkInTimeCell.getStringCellValue().trim().compareTo("09:30") <= 0) {
                    LateAFlagCell.setCellValue("A");
                }
                if (checkInTimeCell.getStringCellValue().trim().compareTo("09:30") > 0 && checkInTimeCell.getStringCellValue().trim().compareTo("10:00") <= 0) {
                    LateBFlagCell.setCellValue("B");
                }
                if (checkInTimeCell.getStringCellValue().trim().compareTo("10:00") > 0) {
                    LateCFlagCell.setCellValue("C");
                }
            }
            //标记迟到
            //下午班 上班打卡结果包含迟到
            // 且 出勤是1
            //上班打卡时间大于13:30，标记为迟到A(13:31-14:00)迟到B(14:01-14:30)迟到C(14:31~)
            if (checkInResultCell.getStringCellValue().contains("迟到") && classCell.getStringCellValue().contains("下午班")
                    && attendanceCell.getNumericCellValue() == 1) {
                if (checkInTimeCell.getStringCellValue().trim().compareTo("13:30") > 0 && checkInTimeCell.getStringCellValue().trim().compareTo("14:00") <= 0) {
                    LateAFlagCell.setCellValue("A");
                }
                if (checkInTimeCell.getStringCellValue().trim().compareTo("14:00") > 0 && checkInTimeCell.getStringCellValue().trim().compareTo("14:30") <= 0) {
                    LateBFlagCell.setCellValue("B");
                }
                if (checkInTimeCell.getStringCellValue().trim().compareTo("14:30") > 0) {
                    LateCFlagCell.setCellValue("C");
                }
            }

            //标记上班时间不足
            //正常晚班
            //  迟到的打卡时间需要晚于21:15 否则标记绿色
            //  晚到的 顺延 比如09:10打卡的，需要21:10 否则标记绿色
            //正常班
            //  迟到的打卡时间需要晚于18:15 否则标记绿色
            //  晚到的 顺延 比如09:10打卡的，需要18:10 否则标记绿色
            //下午班
            //  打卡时间需要晚于21:30 否则标记绿色
            if (classCell.getStringCellValue().contains("正常晚班") && !checkInResultCell.getStringCellValue().contains("无需打卡") && !checkOutResultCell.getStringCellValue().contains("无需打卡") && !checkInResultCell.getStringCellValue().trim().contains("缺卡") && !checkOutResultCell.getStringCellValue().trim().contains("缺卡")) {
                //晚到且时间不够
                if (checkInTimeCell.getStringCellValue().trim().compareTo("09:00") > 0 && checkInTimeCell.getStringCellValue().trim().compareTo("09:15") <= 0) {
                    LocalTime start = LocalTime.parse(checkInTimeCell.getStringCellValue().trim());
                    LocalTime end = LocalTime.parse(checkOutTimeCell.getStringCellValue().trim());
                    Duration duration = Duration.between(start, end);
                    if (duration.toMinutes() < 720) {
                        classCell.setCellStyle(cellStyle3);
                    }
                }
                //迟到且21:15之前就走了
                if (checkInTimeCell.getStringCellValue().trim().compareTo("09:15") > 0 && checkOutTimeCell.getStringCellValue().trim().compareTo("21:15") < 0) {
                    classCell.setCellStyle(cellStyle3);
                }
            }
            if (classCell.getStringCellValue().contains("正常班") && !checkInResultCell.getStringCellValue().contains("无需打卡") && !checkOutResultCell.getStringCellValue().contains("无需打卡") && !checkInResultCell.getStringCellValue().trim().contains("缺卡") && !checkOutResultCell.getStringCellValue().trim().contains("缺卡")) {
                //晚到且时间不够
                if (checkInTimeCell.getStringCellValue().trim().compareTo("09:00") > 0 && checkInTimeCell.getStringCellValue().trim().compareTo("09:15") <= 0) {
                    LocalTime start = LocalTime.parse(checkInTimeCell.getStringCellValue().trim());
                    LocalTime end = LocalTime.parse(checkOutTimeCell.getStringCellValue().trim());
                    Duration duration = Duration.between(start, end);
                    if (duration.toMinutes() < 540) {
                        classCell.setCellStyle(cellStyle3);
                    }
                }
                //迟到且18:15之前就走了
                if (checkInTimeCell.getStringCellValue().trim().compareTo("09:15") > 0 && checkOutTimeCell.getStringCellValue().trim().compareTo("18:15") < 0) {
                    classCell.setCellStyle(cellStyle3);
                }
            }
            if (classCell.getStringCellValue().contains("下午班") && !checkInResultCell.getStringCellValue().contains("无需打卡") && !checkOutResultCell.getStringCellValue().contains("无需打卡") && !checkInResultCell.getStringCellValue().trim().contains("缺卡") && !checkOutResultCell.getStringCellValue().trim().contains("缺卡")) {
                if (checkOutTimeCell.getStringCellValue().trim().compareTo("21:30") < 0) {
                    classCell.setCellStyle(cellStyle3);
                }
            }

            //标记工作日加班、周末加班
            //正常晚班 日期标记橙色
            //上班打卡时间不缺卡 并且 下班打卡时间>=21:00，标记工作日加班
            //正常班且是周六(排除法定补班日) 日期标记黄色
            //上班打卡时间<=9:30 并且 下班打卡时间>=18:00，标记周末加班
            //上班打卡时间<=9:30 并且 下班打卡时间>=21:00，标记周末加班 周末晚班
            //下午班且是周六周日(排除法定补班日) 日期标记黄色
            //上班打卡时间<=13:30 并且 下班打卡时间>=21:30，标记周末加班
            if (classCell.getStringCellValue().contains("正常晚班")) {
                dateCell.setCellStyle(cellStyle1);
                if (!"-".equals(checkInTimeCell.getStringCellValue().trim()) && !"-".equals(checkInTimeCell.getStringCellValue().trim()) && checkOutTimeCell.getStringCellValue().trim().compareTo("21:00") >= 0) {
                    weekdayOverTimeFlagCell.setCellValue(1);
                }
            }

            if (classCell.getStringCellValue().contains("正常班") && localDate.getDayOfWeek() == DayOfWeek.SATURDAY && !Arrays.asList(SPECIALWORKDAYS).contains(dateCell.getStringCellValue()) && !Arrays.asList(SPECIALWEEKDAYS).contains(dateCell.getStringCellValue())) {
                dateCell.setCellStyle(cellStyle2);
                if (!"-".equals(checkInTimeCell.getStringCellValue().trim()) && checkInTimeCell.getStringCellValue().trim().compareTo("09:30") <= 0 && checkOutTimeCell.getStringCellValue().trim().compareTo("18:00") >= 0) {
                    weekendOverTimeFlagCell.setCellValue(1);
                }
                if (!"-".equals(checkInTimeCell.getStringCellValue().trim()) && checkInTimeCell.getStringCellValue().trim().compareTo("09:30") <= 0 && checkOutTimeCell.getStringCellValue().trim().compareTo("21:00") >= 0) {
                    weekendHardTimeFlagCell.setCellValue(1);
                }
            }

            if (classCell.getStringCellValue().contains("下午班") && (localDate.getDayOfWeek() == DayOfWeek.SATURDAY||localDate.getDayOfWeek()==DayOfWeek.SUNDAY) && !Arrays.asList(SPECIALWORKDAYS).contains(dateCell.getStringCellValue()) && !Arrays.asList(SPECIALWEEKDAYS).contains(dateCell.getStringCellValue())) {
                dateCell.setCellStyle(cellStyle2);
                if (!"-".equals(checkInTimeCell.getStringCellValue().trim()) && checkInTimeCell.getStringCellValue().trim().compareTo("13:30") <= 0 && checkOutTimeCell.getStringCellValue().trim().compareTo("21:30") >= 0) {
                    weekendOverTimeFlagCell.setCellValue(1);
                }
            }



            //标记申请加班
            //规则： 班次为正常班且申请了加班（month表中是正常+加班的才算，休息+加班的不算） 标记工作日加班
            HardWork hardWork = getHardWorkByNameAndDate(hardWorks, nameCell.getStringCellValue(), dateCell.getStringCellValue());

            if (classCell.getStringCellValue().contains("正常班") && !ObjectUtils.isEmpty(hardWork)) {
                weekdayOverTimeFlagCell.setCellValue(1);
            }

            //标记请假
            //规则：出勤是1的才需要标记，0的不需要标记
            // month表中带了“假”，“出”字的算请假，全天算1天，否则算0.5天，请2个0.5天会算1天，单请假内容只算前面一个
            //Leave leave = getLeaveByJobNoAndDate(leaves, jobNoCell.getStringCellValue(), dateCell.getStringCellValue());
            if (attendanceCell.getNumericCellValue() == 1) {
                Leave leave = getLeaveByNameAndDate(leaves, nameCell.getStringCellValue(), dateCell.getStringCellValue());
                if (!ObjectUtils.isEmpty(leave)) {
                    leaveTypeCell.setCellValue(leave.getLeaveType());
                    if ("全天".equals(leave.getLeaveTime())) {
                        leaveTimeCell.setCellValue(1);
                    } else {
                        leaveTimeCell.setCellValue(0.5);
                    }
                }
            }

            //调休规则 原规则
            //单个员工当月 正常晚班的日子+正常班且是周六（排除法定补班日）的考勤上班打卡早于9点30分、无早退、无忘记打卡、无请假 可调休
//            if (classCell.getStringCellValue().contains("正常晚班") || (classCell.getStringCellValue().contains("正常班") && calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) && !Arrays.asList(SPECIALWORKDAYS).contains(dateCell.getStringCellValue()) && !Arrays.asList(SPECIALWEEKDAYS).contains(dateCell.getStringCellValue())) {
//                RestInfo restInfo = restInfoMap.get(jobNoCell.getStringCellValue());
//                if (ObjectUtils.isEmpty(restInfo)) {
//                    restInfo = new RestInfo();
//                    restInfo.setName(nameCell.getStringCellValue());
//                    restInfo.setJobNo(jobNoCell.getStringCellValue());
//                    restInfo.setTotalDays(0);
//                    restInfo.setEligibleDays(0);
//                }
//                restInfo.setTotalDays(restInfo.getTotalDays() + 1);
//                if (checkOutResultCell.getStringCellValue().contains("正常") && checkInTimeCell.getStringCellValue().trim().compareTo("09:30") <= 0) {
//                    restInfo.setEligibleDays(restInfo.getEligibleDays() + 1);
//                }
//                restInfoMap.put(restInfo.getJobNo(), restInfo);
//            }
            //调休规则 2025.1月的新规则
            //单个员工当月 工作班的周六考勤上班打卡早于9点30分、无早退、无忘记打卡、无请假 可调休
            //单个员工当月 下午班的周六周日考勤上班打卡早于13点30分、无早退、无忘记打卡、无请假 可调休
            if (((classCell.getStringCellValue().contains("正常班") && localDate.getDayOfWeek() == DayOfWeek.SATURDAY) ||
                            (classCell.getStringCellValue().contains("下午班") && (localDate.getDayOfWeek() == DayOfWeek.SATURDAY|| localDate.getDayOfWeek() == DayOfWeek.SUNDAY)))
                    && !Arrays.asList(SPECIALWORKDAYS).contains(dateCell.getStringCellValue()) && !Arrays.asList(SPECIALWEEKDAYS).contains(dateCell.getStringCellValue())) {
                RestInfo restInfo = restInfoMap.get(jobNoCell.getStringCellValue());
                if (ObjectUtils.isEmpty(restInfo)) {
                    restInfo = new RestInfo();
                    restInfo.setName(nameCell.getStringCellValue());
                    restInfo.setJobNo(jobNoCell.getStringCellValue());
                    restInfo.setTotalDays(0);
                    restInfo.setEligibleDays(0);
                }
                restInfo.setTotalDays(restInfo.getTotalDays() + 1);
                if (classCell.getStringCellValue().contains("正常班") && checkInResultCell.getStringCellValue().contains("正常") && (checkOutResultCell.getStringCellValue().contains("正常")||checkOutResultCell.getStringCellValue().contains("无需打卡")) && checkInTimeCell.getStringCellValue().trim().compareTo("09:30") <= 0) {
                    restInfo.setEligibleDays(restInfo.getEligibleDays() + 1);
                }
                if (classCell.getStringCellValue().contains("下午班") && checkInResultCell.getStringCellValue().contains("正常") && (checkOutResultCell.getStringCellValue().contains("正常")||checkOutResultCell.getStringCellValue().contains("无需打卡")) && checkInTimeCell.getStringCellValue().trim().compareTo("13:30") <= 0) {
                    restInfo.setEligibleDays(restInfo.getEligibleDays() + 1);
                }
                restInfoMap.put(restInfo.getJobNo(), restInfo);
            }

        }
        int rowNumRest = 1;
        for (String key : restInfoMap.keySet()) {
            RestInfo value = restInfoMap.get(key);
            Row newRow = newSheetRest.createRow(rowNumRest);
            Cell newCellName = newRow.createCell(0);
            newCellName.setCellValue(value.getName());
            Cell newCellTotalDays = newRow.createCell(1);
            newCellTotalDays.setCellValue(value.getTotalDays());
            Cell newCellEligibleDays = newRow.createCell(2);
            newCellEligibleDays.setCellValue(value.getEligibleDays());
            rowNumRest = rowNumRest + 1;
        }

        //TODO 这2列隐藏
        //newSheet.setColumnHidden(3, true);
        //newSheet.setColumnHidden(6, true);
        OutputStream fileOut = new FileOutputStream(RESULT_FILE_PATH);
        newWb.setForceFormulaRecalculation(true);
        newWb.write(fileOut);
        newWb.close();
        fileOut.close();
    }


    @SneakyThrows
    public static List<Leave> getLeave() {
        List<Leave> leaves = new ArrayList<>();
        ZipSecureFile.setMinInflateRatio(-1.0d);
        InputStream inp = new FileInputStream(MONTH_FILE_PATH);
        Workbook wb = WorkbookFactory.create(inp);
        Sheet sheet = wb.getSheetAt(0);
        for (int rowNum = 2; rowNum <= sheet.getLastRowNum(); rowNum++) {
            Row row = sheet.getRow(rowNum);
            for (int cellNum = columns; cellNum < row.getLastCellNum(); cellNum++) {
                Cell cell = row.getCell(cellNum);
                Leave leave = new Leave();
                leave.setName(row.getCell(0).getStringCellValue());
                leave.setJobNo(row.getCell(2).getStringCellValue());
                //TODO 需要预处理daily表中日期格式带不带-
                //leave.setDate(sheet.getRow(1).getCell(cellNum).getStringCellValue().substring(0, 10).replace("-", ""));
                leave.setDate(sheet.getRow(1).getCell(cellNum).getStringCellValue().substring(0, 10));
                if (cell.getStringCellValue().split(";").length == 2) {
                    //xxx,xxx;外出(下午) -->外出不算
                    //xxx,xxx;出差(下午) -->出差 0.5天
                    //xxx,xxx;外出(全天) -->外出/出差全天
                    //xxx,xxx;年假(下午) -->年假0.5天
                    //xxx,xxx;年假(全天) -->年假全天
                    //xxx,xxx;事假(上午,下午) -->事假全天
                    //xxx,xxx;事假(下午,上午) -->事假全天
                    String str = cell.getStringCellValue().split(";")[1];
                    if(str.equals("换班")){
                        continue;
                    }
                    System.out.println(str);
                    String leaveType = str.split("\\(")[0];
                    leave.setLeaveType(leaveType);
                    String leaveTime = str.split("\\(")[1].replace(")", "");
                    if ("下午,上午".equals(leaveTime) || "上午,下午".equals(leaveTime)) {
                        leaveTime = "全天";
                    }

                    if (!(leaveType.contains("外出") && leaveTime.contains("午"))) {
                        leave.setLeaveTime(leaveTime);
                        leaves.add(leave);
                    }
                }
                if (cell.getStringCellValue().split(";").length == 3) {
                    //xxx,xxx;年假(下午);年假(上午) -->年假全天
                    //xxx,xxx;年假(下午);事假(上午) -->上午下午类型不同
                    //xxx,xxx;外出(下午);出差(上午) -->上午下午类型不同
                    //xxx,xxx;年假(上午);外出(下午) -->上午下午类型不同
                    //xxx,xxx;外出(下午);年假(上午) -->上午下午类型不同
                    String str1 = cell.getStringCellValue().split(";")[1];
                    String str2 = cell.getStringCellValue().split(";")[2];
                    String leaveType1 = str1.split("\\(")[0];
                    String leaveType2 = str2.split("\\(")[0];
                    System.out.println(leaveType1);
                    System.out.println(leaveType2);

                    if (leaveType1.equals(leaveType2)) {
                        String leaveType = str1.split("\\(")[0];
                        leave.setLeaveType(leaveType);
                        String leaveTime = "全天";
                        leave.setLeaveTime(leaveTime);
                        leaves.add(leave);
                    }
                    if (!leaveType1.equals(leaveType2)) {
                        String leaveType = "上午下午类型不同";
                        leave.setLeaveType(leaveType);
                        String leaveTime = "全天";
                        leave.setLeaveTime(leaveTime);
                        leaves.add(leave);
                    }

                }
            }
        }
        for (int i = 0; i < leaves.size(); i++) {
            Leave leave = leaves.get(i);
            if ("调休假".equals(leave.getLeaveType())) {
                leave.setLeaveType("调休");
                leaves.set(i, leave);
            }
        }
        return leaves;
    }

    @SneakyThrows
    public static List<HardWork> getHardWork() {
        List<HardWork> hardWorks = new ArrayList<>();
        ZipSecureFile.setMinInflateRatio(-1.0d);
        InputStream inp = new FileInputStream(MONTH_FILE_PATH);
        Workbook wb = WorkbookFactory.create(inp);
        Sheet sheet = wb.getSheetAt(0);

        for (int rowNum = 2; rowNum <= sheet.getLastRowNum(); rowNum++) {
            Row row = sheet.getRow(rowNum);
            for (int cellNum = columns; cellNum < row.getLastCellNum(); cellNum++) {
                Cell cell = row.getCell(cellNum);
                if (cell.getStringCellValue().contains("加班") && !cell.getStringCellValue().contains("休息")) {
                    HardWork hardWork = new HardWork();
                    hardWork.setName(row.getCell(0).getStringCellValue());
                    hardWork.setJobNo(row.getCell(2).getStringCellValue());
                    //TODO 需要预处理daily表中日期格式带不带-
                    //hardWork.setDate(sheet.getRow(1).getCell(cellNum).getStringCellValue().substring(0, 10).replace("-", ""));
                    hardWork.setDate(sheet.getRow(1).getCell(cellNum).getStringCellValue().substring(0, 10));
                    hardWork.setHardWorkType("加班");
                    hardWorks.add(hardWork);
                }
            }
        }
        return hardWorks;
    }

    public static HardWork getHardWorkByNameAndDate(List<HardWork> hardWorks, String name, String date) {
        for (HardWork hardWork : hardWorks) {
            if (hardWork.getJobNo().equals(name) && hardWork.getDate().equals(date)) {
                return hardWork;
            }
        }
        return null;
    }


    public static Leave getLeaveByJobNoAndDate(List<Leave> leaves, String jobNo, String date) {
        for (Leave leave : leaves) {
            if (leave.getJobNo().equals(jobNo) && leave.getDate().equals(date)) {
                return leave;
            }
        }
        return null;
    }

    public static Leave getLeaveByNameAndDate(List<Leave> leaves, String name, String date) {
        for (Leave leave : leaves) {
            if (leave.getName().equals(name) && leave.getDate().equals(date)) {
                return leave;
            }
        }
        return null;
    }

    public static void copyCell(Workbook newWb, Row oldRow, int oldCellNum, Row newRow, int newCellNum) {
        Cell oldCell = oldRow.getCell(oldCellNum);
        Cell newCell = newRow.createCell(newCellNum);
        newCell.setCellValue(oldCell.getStringCellValue());
        if (STYLE_MAP.get(oldCell.getCellStyle().toString()) == null) {
            CellStyle newStyle = newWb.createCellStyle();
            newStyle.cloneStyleFrom(oldCell.getCellStyle());
            STYLE_MAP.put(oldCell.getCellStyle().toString(), newStyle);
        }
        newCell.setCellStyle(STYLE_MAP.get(oldCell.getCellStyle().toString()));

    }
}
