package com.loktar.task.investment;


import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import com.loktar.conf.LokTarConstant;
import com.loktar.domain.investment.EquityIndexPerfDaily;
import com.loktar.mapper.investment.EquityIndexPerfDailyMapper;
import com.loktar.util.DateTimeUtil;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.MessageFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
public class ChinaEquityIndexPerfTask {

    private final EquityIndexPerfDailyMapper equityIndexPerfDailyMapper;
    private final HttpClient httpClient;
    private final JsonMapper objectMapper;
    private static final List<String> INDEX_CODES = List.of("930955","H20955");
    private static final String INDEX_PERF_URL = "https://www.csindex.com.cn/csindex-home/perf/index-perf?indexCode={0}&startDate={1}&endDate={2}";

    public ChinaEquityIndexPerfTask(EquityIndexPerfDailyMapper equityIndexPerfDailyMapper, HttpClient httpClient, JsonMapper objectMapper) {
        this.equityIndexPerfDailyMapper = equityIndexPerfDailyMapper;
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
    }

    @Scheduled(cron = "0 0/10 18-23 * * *")
    public void syncToday() {
        log.info("{}", "指数行情定时器：" + DateTimeUtil.getDatetimeStr(LocalDateTime.now(), DateTimeUtil.FORMATTER_DATESECOND));
        LocalDate today = LocalDate.now();

        boolean allExist = INDEX_CODES.stream()
                .allMatch(indexCode -> equityIndexPerfDailyMapper.existsByIndexCodeAndTradeDate(indexCode, today));
        if (allExist) {
            return;
        }
        for (String indexCode : INDEX_CODES) {
            try {
                if (equityIndexPerfDailyMapper.existsByIndexCodeAndTradeDate(indexCode, today)) {
                    continue;
                }
                fetchAndSave(indexCode, today, today);
            } catch (Exception e) {
                log.error("{} 行情同步异常: {}", indexCode, e.getMessage(), e);
            }
        }
    }

    public void initHistory(LocalDate startDate, LocalDate endDate) {
        log.info("{}", "指数历史行情初始化：" + startDate + " ~ " + endDate);
        for (String indexCode : INDEX_CODES) {
            try {
                fetchAndSave(indexCode, startDate, endDate);
            } catch (Exception e) {
                log.error("{} 历史行情初始化异常: {}", indexCode, e.getMessage(), e);
            }
        }
    }

    @SneakyThrows
    private void fetchAndSave(String indexCode, LocalDate startDate, LocalDate endDate) {
        String url = MessageFormat.format(INDEX_PERF_URL, indexCode, startDate.format(DateTimeUtil.FORMATTER_DATE_COMPACT), endDate.format(DateTimeUtil.FORMATTER_DATE_COMPACT));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header(LokTarConstant.HTTP_HEADER_USER_AGENT_NAME, LokTarConstant.HTTP_HEADER_USER_AGENT_VALUE)
                .header(LokTarConstant.HTTP_HEADER_ACCEPT_NAME, LokTarConstant.HTTP_HEADER_ACCEPT_VALUE_JSON)
                .timeout(Duration.ofSeconds(30))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        JsonNode root = objectMapper.readTree(response.body());
        if (!"200".equals(root.path("code").asString())) {
            log.warn("{} 行情接口返回错误: {}", indexCode, root.path("msg").asString());
            return;
        }
        JsonNode data = root.path("data");
        if (!data.isArray() || data.isEmpty()) {
//            log.info("{} 行情接口未返回数据", indexCode);
            return;
        }
        for (JsonNode item : data) {
            EquityIndexPerfDaily perfDaily = new EquityIndexPerfDaily();
            perfDaily.setIndexCode(item.get("indexCode").asString().trim());
            perfDaily.setIndexName(item.get("indexNameCn").asString().trim());
            perfDaily.setTradeDate(LocalDate.parse(item.get("tradeDate").asString().trim(), DateTimeUtil.FORMATTER_DATE_COMPACT));
            perfDaily.setOpen(item.get("open").decimalValue(BigDecimal.ZERO));
            perfDaily.setHigh(item.get("high").decimalValue(BigDecimal.ZERO));
            perfDaily.setLow(item.get("low").decimalValue(BigDecimal.ZERO));
            perfDaily.setClose(item.get("close").decimalValue(BigDecimal.ZERO));
            perfDaily.setChange(item.get("change").decimalValue(BigDecimal.ZERO));
            perfDaily.setChangePct(item.get("changePct").decimalValue(BigDecimal.ZERO));
            perfDaily.setTradingVol(item.get("tradingVol").asDouble(0.0));
            perfDaily.setTradingValue(item.get("tradingValue").decimalValue(BigDecimal.ZERO));
            perfDaily.setConsNumber(item.get("consNumber").asInt(0));
            perfDaily.setPeg(item.get("peg").decimalValue(BigDecimal.ZERO));

            EquityIndexPerfDaily exist = equityIndexPerfDailyMapper.selectByIndexCodeAndTradeDate(perfDaily.getIndexCode(), perfDaily.getTradeDate());
            if (exist == null) {
                equityIndexPerfDailyMapper.insert(perfDaily);
                log.info("{}", indexCode + " 指数行情新增成功：" + perfDaily.getTradeDate());
            }
        }
    }
}
