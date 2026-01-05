package com.loktar.web.cxy.tiktok;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.loktar.domain.cxy.TiktokAccount;
import com.loktar.domain.cxy.TiktokCreatorData;
import com.loktar.domain.cxy.TiktokData;
import com.loktar.web.cxy.tiktok.util.Encoder;
import lombok.SneakyThrows;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TikTokTestJdkHttp {
    // 保持 Cookie 顺序
    private static final Map<String, String> COOKIE_MAP = new LinkedHashMap<>();
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper().configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true).configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    ;
    private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(60)).followRedirects(HttpClient.Redirect.NORMAL).build();
    private static final String LOGIN_URL = "https://live-backstage.tiktok.com/passport/web/email/login/?aid=6849&account_sdk_source=web&sdk_version=2.1.1-tiktok&language=zh";
    private static final String TODAY_CORE_METRICS_URL = "https://live-backstage.tiktok.com/creators/live/union_platform_api/agency/workbench/today_core_metrics/";
    private static final String CORE_METRICS_URL = "https://live-backstage.tiktok.com/creators/live/union_platform_api/agency/workbench/core_metrics/";
    private static final String NEW_ANCHOR_PERFORMANCE_URL = "https://live-backstage.tiktok.com/creators/live/union_platform_api/union/anchor/get_new_anchor_performance/";

    public static void main(String[] args) {
        TiktokAccount tiktokAccount = new TiktokAccount();

        String dateStr = "2025-12-28";
//        getTikTokData(tiktokAccount, dateStr);
        if (!login(tiktokAccount)) {
            return;
        }
        getAnchorData(tiktokAccount, dateStr);
    }

    public static List<TiktokCreatorData> getTikTokCreatorData(TiktokAccount tiktokAccount, String dateStr) {
        List<TiktokCreatorData> tiktokCreatorDatas = new ArrayList<>();
        AnchorDataResp anchorDataResp = getAnchorData(tiktokAccount, dateStr);
        for (int i = 0; i < 10; i++) {
            AnchorDataResp.AnchorInfo anchorInfo = anchorDataResp.getData().getAnchorAnalysisDataInfos().get(i);
            TiktokCreatorData tiktokCreatorData = new TiktokCreatorData();
            tiktokCreatorData.setAccountId(tiktokAccount.getAccountId());
            tiktokCreatorData.setDate(dateStr);
            tiktokCreatorData.setRank(i + 1);
            tiktokCreatorData.setDisplayId(anchorInfo.getAnchorBaseInfo().getUserBaseInfo().getDisplayId());
            tiktokCreatorData.setIncomeCurrent(anchorInfo.getIncome().getCurrent());
            tiktokCreatorDatas.add(tiktokCreatorData);
        }
        return tiktokCreatorDatas;
    }


    public static TiktokData getTikTokData(TiktokAccount tiktokAccount, String dateStr) {
        TiktokData tiktokData = new TiktokData();
        tiktokData.setAccountId(tiktokAccount.getAccountId());
        tiktokData.setDate(dateStr);
        // 获取今日核心数据
        TodayCoreMetricsResp todayCoreMetricsResp = getWorkbenchMetrics(TODAY_CORE_METRICS_URL, TodayCoreMetricsResp.class, tiktokAccount);
        if (todayCoreMetricsResp != null && todayCoreMetricsResp.getTodayMetricsData() != null) {
            for (TodayCoreMetricsResp.TodayMetricsData d : todayCoreMetricsResp.getTodayMetricsData()) {
                if (d.getIndicatorName().contains("agency_income_diamond_cnt")) {
//                    System.out.println("当日总钻石数: " + d.getSamePeriodValue());
                    tiktokData.setTodayDiamondCnt(Integer.valueOf(d.getSamePeriodValue()));
                }
                if (d.getIndicatorName().contains("agency_live_ucnt")) {
//                    System.out.println("当日开播人数: " + d.getSamePeriodValue());
                    tiktokData.setTodayLiveCnt(Integer.valueOf(d.getSamePeriodValue()));
                }
                if (d.getIndicatorName().contains("agency_new_increase_anchor_ucnt")) {
//                    System.out.println("当日入会人数: " + d.getSamePeriodValue());
                    tiktokData.setTodayNewMemberCnt(Integer.valueOf(d.getSamePeriodValue()));
                }
            }
        }

        // 获取本月核心数据
        OverviewResp overviewResp = getWorkbenchMetrics(CORE_METRICS_URL, OverviewResp.class, tiktokAccount);
        if (overviewResp != null && overviewResp.getThisMonthMetricsData() != null) {
            for (OverviewResp.TodayMetricsData d : overviewResp.getThisMonthMetricsData()) {
                if (d.getIndicatorName().contains("agency_income_diamond_cnt")) {
//                    System.out.println("当月总钻石数: " + d.getValue());
                    tiktokData.setMonthDiamondCnt(Integer.valueOf(d.getValue()));
                    BigDecimal pct = new BigDecimal(d.getSamePeriodPercentageIncrease())
                            .setScale(2, RoundingMode.HALF_UP);
//                    System.out.println("月环比: " + pct.toPlainString() + "%");
                    tiktokData.setMonthDiamondPct(pct.toPlainString() + "%");
                }
                if (d.getIndicatorName().contains("agency_new_managed_creators")) {
//                    System.out.println("当月新主播数: " + d.getValue());
                    tiktokData.setMonthNewCreatorCnt(Integer.valueOf(d.getValue()));
                }
            }
        }
        NewCreatorOverviewResp newCreatorOverviewResp = getNewAnchorPerformance(tiktokAccount, dateStr);
        if (newCreatorOverviewResp != null && newCreatorOverviewResp.getData() != null) {
//            System.out.println("当月新主播钻石数: " + Integer.valueOf(newCreatorOverviewResp.getData().getNewCreatorDiamonds().getCurrent()));
            tiktokData.setMonthNewCreatorDiamondCnt(Integer.valueOf(newCreatorOverviewResp.getData().getNewCreatorDiamonds().getCurrent()));
        }
        return tiktokData;
    }

    @SneakyThrows
    public static boolean login(TiktokAccount tiktokAccount) {
        Map<String, Object> enc = Encoder.encodeForEmailLogin(tiktokAccount.getEmail(), tiktokAccount.getPassword(), null);
        String formData = buildFormData(Map.of(
                "mix_mode", String.valueOf(enc.get("mix_mode")),
                "email", String.valueOf(enc.get("email")),
                "password", String.valueOf(enc.get("password")),
                "fixed_mix_mode", String.valueOf(enc.get("fixed_mix_mode"))
        ));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(LOGIN_URL))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("sec-ch-ua-platform", "\"Windows\"")
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36")
                .header("Accept", "application/json, text/javascript")
                .header("sec-ch-ua", "\"Google Chrome\";v=\"143\", \"Chromium\";v=\"143\", \"Not A(Brand\";v=\"24\"")
                .header("sec-ch-ua-mobile", "?0")
                .header("x-tt-passport-csrf-token", "")
                .header("Origin", "https://live-backstage.tiktok.com")
                .header("Referer", "https://live-backstage.tiktok.com/login/")
                .header("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8")
                // 标准库不支持 "same-origin" 这种非标准值的自动校验，但可以手动设置
                .header("Sec-Fetch-Site", "same-origin")
                .header("Sec-Fetch-Mode", "cors")
                .header("Sec-Fetch-Dest", "empty")
                .POST(HttpRequest.BodyPublishers.ofString(formData))
                .build();

        HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        mergeCookies(response);
        String bodyStr = response.body();
        boolean flag = false;
        if (bodyStr != null && !bodyStr.isBlank()) {
            flag = "success".equalsIgnoreCase(
                    OBJECT_MAPPER.readTree(bodyStr).path("message").asText("")
            );
        }
        System.out.println("账号: " + tiktokAccount.getEmail() + " 登录 " + (flag ? "成功" : "失败"));
        return flag;
    }

    @SneakyThrows
    private static <T> T getWorkbenchMetrics(String endpointUrl, Class<T> respClass, TiktokAccount tiktokAccount) {
        String url = endpointUrl
                + "?QueryType=3"
                + "&IsWorkspace=0";
//                + "&msToken=" + tiktokAccount.getMsTokenOverview()
//                + "&X-Bogus=" + tiktokAccount.getxBogusOverview()
//                + "&X-Gnarly=" + tiktokAccount.getxGnarlyOverview();
        String cookieHeader = getCookieHeader();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .header("x-appid", "1180")
                .header("x-csrf-token", "undefined")
                .header("x-language", "zh-Hans")
                .header("faction-id", tiktokAccount.getFactionId())
                .header("X-Dp-Business-Level1", "2")
                .header("X-Dp-Business-Level2", "1")
                .header("X-Dp-Business-Level3", "82")
                .header("X-Dp-Business-Level4", "97")
                .header("Accept", "application/json, text/plain, _/_")
                .header("Content-Type", "application/json")
                .header("Referer", "https://live-backstage.tiktok.com/portal/overview")
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36")
                .header("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8")
                .header("sec-ch-ua-platform", "\"Windows\"")
                .header("sec-ch-ua", "\"Google Chrome\";v=\"143\", \"Chromium\";v=\"143\", \"Not A(Brand\";v=\"24\"")
                .header("sec-ch-ua-mobile", "?0")
                .header("Sec-Fetch-Site", "same-origin")
                .header("Sec-Fetch-Mode", "cors")
                .header("Sec-Fetch-Dest", "empty")
                .header("Cookie", cookieHeader)
                .build();

        HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        mergeCookies(response);
        String bodyStr = response.body();
        if (bodyStr == null || bodyStr.isBlank()) {
            return null;
        }
        return OBJECT_MAPPER.readValue(bodyStr, respClass);
    }

    @SneakyThrows
    private static NewCreatorOverviewResp getNewAnchorPerformance(TiktokAccount tiktokAccount, String dateStr) {
        LocalDate date = LocalDate.parse(dateStr);
        long start1 = date.withDayOfMonth(1).atStartOfDay(ZoneOffset.UTC).toEpochSecond();
        long end1 = date.atTime(23, 59, 59).toInstant(ZoneOffset.UTC).getEpochSecond();
        String specificUrl = NEW_ANCHOR_PERFORMANCE_URL +
                "?AgencyID=" + tiktokAccount.getFactionId() +
                "&StartTime=" + start1 +
                "&EndTime=" + end1 +
                "&TimeType=3" +
                "&CreatorSources=%5B0%5D" ;
//                "&msToken=" + tiktokAccount.getMsTokenNewAnchorPerformance() +
//                "&X-Bogus=" + tiktokAccount.getxBogusNewAnchorPerformance() +
//                "&X-Gnarly=" + tiktokAccount.getxGnarlyNewAnchorPerformance();
        String cookieHeader = getCookieHeader();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(specificUrl))
                .GET()
                .header("sec-ch-ua-platform", "\"Windows\"")
                .header("x-csrf-token", "undefined")
                .header("sec-ch-ua", "\"Google Chrome\";v=\"143\", \"Chromium\";v=\"143\", \"Not A(Brand\";v=\"24\"")
                .header("sec-ch-ua-mobile", "?0")
                .header("x-language", "zh-Hans")
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36")
                .header("Accept", "application/json, text/plain, _/_")
                .header("Content-Type", "application/json")
                .header("x-appid", "1180")
                .header("faction-id", tiktokAccount.getFactionId())
                .header("Sec-Fetch-Site", "same-origin")
                .header("Sec-Fetch-Mode", "cors")
                .header("Sec-Fetch-Dest", "empty")
                .header("Referer", "https://live-backstage.tiktok.com/portal/data/new-anchor-analysis?AgentID=&EndTime=1766966399&GroupID=&StartTime=1764547200&TimeType=3")
                .header("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8")
                .header("Cookie", cookieHeader)
                .build();
        HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        // 更新 Cookie
        mergeCookies(response);
        String bodyStr = response.body();
        if (bodyStr == null || bodyStr.isBlank()) {
            return null;
        }
        return OBJECT_MAPPER.readValue(bodyStr, NewCreatorOverviewResp.class);
    }

    /**
     * 获取主播数据 (get_anchor_data)
     */
    /**
     * 获取主播数据 (get_anchor_data)
     * 根据提供的 JSON 结构进行解析
     */
    @SneakyThrows
    private static AnchorDataResp getAnchorData(TiktokAccount tiktokAccount, String dateStr) {
        LocalDate date = LocalDate.parse(dateStr);
        long end1 = date.atTime(23, 59, 59).toInstant(ZoneOffset.UTC).getEpochSecond();
        long start2 = date.atStartOfDay(ZoneOffset.UTC).toEpochSecond();
        String specificUrl = "https://live-backstage.tiktok.com/creators/live/union_platform_api/union/anchor/get_anchor_data/" +
                "?startTime=" + start2 +
                "&endTime=" + end1 +
                "&sortTagEnum=1" +
                "&descOrAsc=desc" +
                "&offset=0" +
                "&limit=20" +
                "&FactionID=" + tiktokAccount.getFactionId() +
                "&compareType=1" +
                "&timeType=4" ;
//                "&msToken=" + tiktokAccount.getMsTokenNewAnchorData() +
//                "&X-Bogus=" + tiktokAccount.getxBogusNewAnchorData() +
//                "&X-Gnarly=" + tiktokAccount.getxGnarlyNewAnchorData();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(specificUrl))
                .GET()
                .header("sec-ch-ua-platform", "\"Windows\"")
                .header("x-csrf-token", "undefined")
                .header("sec-ch-ua", "\"Google Chrome\";v=\"143\", \"Chromium\";v=\"143\", \"Not A(Brand\";v=\"24\"")
                .header("sec-ch-ua-mobile", "?0")
                .header("x-language", "zh-Hans")
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36")
                .header("Accept", "application/json, text/plain, _/_")
                .header("Content-Type", "application/json")
                .header("x-appid", "1180")
                .header("faction-id", tiktokAccount.getFactionId())
                .header("Sec-Fetch-Site", "same-origin")
                .header("Sec-Fetch-Mode", "cors")
                .header("Sec-Fetch-Dest", "empty")
//                .header("Referer", "https://live-backstage.tiktok.com/portal/data/data?anchorID=&descOrAsc=&endTime=1766966399&sortTagEnum=1&startTime=1766880000")
                .header("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8")
                .header("Cookie", getCookieHeader()) // 动态获取 Cookie
                .build();
        HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        mergeCookies(response);
        String bodyStr = response.body();
        if (bodyStr == null || bodyStr.isBlank()) {
            return null;
        }
//        System.out.println(bodyStr);
        return OBJECT_MAPPER.readValue(bodyStr, AnchorDataResp.class);
    }

    /**
     * 将 HttpResponse 中的 Set-Cookie 头合并到静态 COOKIE_MAP 中
     */
    private static void mergeCookies(HttpResponse<?> response) {
        List<String> cookies = response.headers().allValues("Set-Cookie");
        if (cookies != null) {
            for (String cookie : cookies) {
                // 简单解析：取分号前的第一部分作为 key=value
                String[] parts = cookie.split(";", 2);
                String cookiePair = parts[0];
                int eqIndex = cookiePair.indexOf('=');
                if (eqIndex > 0) {
                    String name = cookiePair.substring(0, eqIndex).trim();
                    String value = cookiePair.substring(eqIndex + 1).trim();
                    // 这里可以添加逻辑判断是否过期，或者是否是 "deleted"
                    if (!value.isEmpty() && !"\"\"".equals(value)) {
                        COOKIE_MAP.put(name, value);
                    }
                }
            }
        }
    }

    private static String getCookieHeader() {
        return COOKIE_MAP.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining("; "));
    }

    @SneakyThrows
    private static String buildFormData(Map<String, String> data) {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, String> entry : data.entrySet()) {
            if (builder.length() > 0) {
                builder.append("&");
            }
            builder.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8));
            builder.append("=");
            builder.append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8));
        }
        return builder.toString();
    }
}