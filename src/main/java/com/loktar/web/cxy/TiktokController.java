package com.loktar.web.cxy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.loktar.domain.cxy.TiktokAccount;
import com.loktar.mapper.cxy.TiktokAccountMapper;
import lombok.SneakyThrows;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("tiktok")
public class TiktokController {

    private final TiktokAccountMapper tiktokAccountMapper;

    private ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    public TiktokController(TiktokAccountMapper tiktokAccountMapper) {
        this.tiktokAccountMapper = tiktokAccountMapper;
    }


    @GetMapping("/getAccount.do")
    @SneakyThrows
    public String getAccount() {
        List<TiktokAccount> tiktokAccounts = tiktokAccountMapper.selectByStatus(1);
        return objectMapper.writeValueAsString(tiktokAccounts);
    }

    @GetMapping("/getTimeParams.do")
    @SneakyThrows
    public String getTimeParams(String dateStr){
        LocalDate date = LocalDate.parse(dateStr); // yyyy-MM-dd
        String monthStartSecond = String.valueOf(date.withDayOfMonth(1).atStartOfDay().toEpochSecond(ZoneOffset.UTC));
        String dayStartSecond = String.valueOf(date.atStartOfDay().toEpochSecond(ZoneOffset.UTC));
        String dayEndSecond = String.valueOf(date.plusDays(1).atStartOfDay().toEpochSecond(ZoneOffset.UTC) - 1);

        // yyyy-M-d（不补零）
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-M-d");
        String monthStartDate = date.withDayOfMonth(1).format(fmt);
        String dayStartDate = date.format(fmt);
        String dayEndDate = date.format(fmt);

        // ===== 新增：上月开始到上月当天（yyyy-M-d） =====
        LocalDate lastMonth = date.minusMonths(1);
        String lastMonthStartDate = lastMonth.withDayOfMonth(1).format(fmt);

        int targetDay = date.getDayOfMonth();
        int lastMonthMaxDay = lastMonth.lengthOfMonth();
        LocalDate lastMonthSameDay = lastMonth.withDayOfMonth(Math.min(targetDay, lastMonthMaxDay));
        String lastMonthSameDayDate = lastMonthSameDay.format(fmt);
        // ============================================

        UtcTimeParams dto = new UtcTimeParams(
                monthStartSecond, dayStartSecond, dayEndSecond,
                monthStartDate, dayStartDate, dayEndDate,
                lastMonthStartDate, lastMonthSameDayDate
        );
        return objectMapper.writeValueAsString(dto);
    }

    public static class UtcTimeParams {
        public String monthStartSecond; // 本月第一天 00:00:00 UTC
        public String dayStartSecond;   // 当天 00:00:00 UTC
        public String dayEndSecond;     // 当天 23:59:59 UTC（inclusive）
        public String monthStartDate;   // yyyy-M-d
        public String dayStartDate;     // yyyy-M-d
        public String dayEndDate;       // yyyy-M-d

        // 新增
        public String lastMonthStartDate;     // 上月第一天 yyyy-M-d
        public String lastMonthSameDayDate;   // 上月“同一天” yyyy-M-d（不存在则取上月最后一天）

        public UtcTimeParams(String monthStartSecond,
                             String dayStartSecond,
                             String dayEndSecond,
                             String monthStartDate,
                             String dayStartDate,
                             String dayEndDate,
                             String lastMonthStartDate,
                             String lastMonthSameDayDate) {
            this.monthStartSecond = monthStartSecond;
            this.dayStartSecond = dayStartSecond;
            this.dayEndSecond = dayEndSecond;
            this.monthStartDate = monthStartDate;
            this.dayStartDate = dayStartDate;
            this.dayEndDate = dayEndDate;
            this.lastMonthStartDate = lastMonthStartDate;
            this.lastMonthSameDayDate = lastMonthSameDayDate;
        }
    }
}
