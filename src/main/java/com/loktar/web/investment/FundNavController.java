package com.loktar.web.investment;


import lombok.extern.slf4j.Slf4j;
import com.loktar.conf.LokTarConfig;
import com.loktar.conf.LokTarConstant;
import com.loktar.domain.common.Property;
import com.loktar.domain.investment.FundNav;
import com.loktar.dto.wx.agentmsg.AgentMsgText;
import com.loktar.mapper.common.PropertyMapper;
import com.loktar.mapper.investment.FundNavMapper;
import com.loktar.util.DateTimeUtil;
import com.loktar.util.wx.qywx.QywxApi;
import lombok.SneakyThrows;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.MessageFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("fundNav")
@Slf4j
public class FundNavController {

    private final FundNavMapper fundNavMapper;
    private final PropertyMapper propertyMapper;
    private final QywxApi qywxApi;
    private final LokTarConfig lokTarConfig;
    private final HttpClient httpClient;


    private static final String FUND_NAV_URL = "https://fundf10.eastmoney.com/F10DataApi.aspx?type=lsjz&code={0}&sdate={1}&edate={2}&per={3}&page={4}";
    private static final Pattern PAGES_PATTERN = Pattern.compile("pages:(\\d+)");

    public FundNavController(FundNavMapper fundNavMapper, PropertyMapper propertyMapper, QywxApi qywxApi, LokTarConfig lokTarConfig, HttpClient httpClient) {
        this.fundNavMapper = fundNavMapper;
        this.propertyMapper = propertyMapper;
        this.qywxApi = qywxApi;
        this.lokTarConfig = lokTarConfig;
        this.httpClient = httpClient;
    }

    @PostMapping("/testSync")
    @SneakyThrows
    public void testSync(String code) {
        LocalDate today = LocalDate.now().minusDays(1);
        String todayStr = today.format(DateTimeUtil.FORMATTER_DATE_COMPACT);

        FundNav exist = fundNavMapper.selectByFundCodeAndNavDate(code, today);
        if (exist != null) {
            log.info("{}", code + " 当日数据已存在，跳过");
            return;
        }

        String url = MessageFormat.format(FUND_NAV_URL, code, todayStr, todayStr, "1", "1");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header(LokTarConstant.HTTP_HEADER_ACCEPT_ENCODING_NAME, "")
                .timeout(Duration.ofSeconds(30))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        List<FundNav> list = parseFundNav(response.body(), code);

        if (list.isEmpty()) {
            log.info("{}", code + " 未获取到数据");
            return;
        }

        FundNav fundNav = list.get(0);
        if (!today.equals(fundNav.getNavDate())) {
            log.info("{}", code + " 接口返回日期非今日，跳过");
            return;
        }

        FundNav current = fundNavMapper.selectByFundCodeAndNavDate(fundNav.getFundCode(), fundNav.getNavDate());
        if (current == null) {
            fundNav.setCreateTime(LocalDateTime.now());
            fundNav.setUpdateTime(LocalDateTime.now());
            fundNavMapper.insert(fundNav);
            log.info("{}", code + " 新增成功：" + fundNav.getNavDate());
            Property property = propertyMapper.selectByPrimaryKey("fund_nav_" + code);
            if (property != null && property.getValue() != null) {
                BigDecimal share = new BigDecimal(property.getValue2());
                BigDecimal principal = new BigDecimal(property.getValue3());
                BigDecimal total = share.multiply(fundNav.getUnitNav()).setScale(2, RoundingMode.HALF_UP);
                BigDecimal profitRate = total.subtract(principal).divide(principal, 4, RoundingMode.HALF_UP).multiply(new BigDecimal("100")).setScale(2, RoundingMode.HALF_UP);
                StringBuilder msg = new StringBuilder();
                msg.append(property.getValue()).append(System.lineSeparator());
                msg.append(System.lineSeparator());
                msg.append("单位净值：").append(fundNav.getUnitNav()).append(System.lineSeparator());
                msg.append("日涨跌幅：").append(fundNav.getGrowthRate() != null ? fundNav.getGrowthRate().setScale(2, RoundingMode.HALF_UP) + "%" : "").append(System.lineSeparator());
                msg.append("持有份额：").append(share).append(System.lineSeparator());
                msg.append("本金总额：").append(principal).append(System.lineSeparator());
                msg.append("资产总额：").append(total).append(System.lineSeparator());
                msg.append("盈亏比例：").append(profitRate).append("%").append(System.lineSeparator());
                if (fundNav.getBonus() != null) {
                    msg.append("每份分红：").append(fundNav.getBonus()).append(System.lineSeparator());
                }
                msg.append(System.lineSeparator());
                msg.append(DateTimeUtil.getDatetimeStr(LocalDateTime.now(), DateTimeUtil.FORMATTER_DATEMINUTE));
                qywxApi.sendTextMsg(new AgentMsgText(lokTarConfig.getQywx().getNoticeZxb(), lokTarConfig.getQywx().getAgent009Id(), msg.toString()));
            }
        }
    }

    @PostMapping("/sync")
    @SneakyThrows
    public void sync(String code, String sdate, String edate, int per) {
        String url = MessageFormat.format(FUND_NAV_URL, code, sdate, edate, String.valueOf(per), "1");
        String response = doRequest(url);
        int totalPages = extractPages(response);

        List<FundNav> allList = new ArrayList<>();
        for (int page = 1; page <= totalPages; page++) {
            if (page > 1) {
                url = MessageFormat.format(FUND_NAV_URL, code, sdate, edate, String.valueOf(per), String.valueOf(page));
                response = doRequest(url);
            }
            List<FundNav> list = parseFundNav(response, code);
            log.info("{}", "第" + page + "/" + totalPages + "页，获取" + list.size() + "条数据");
            allList.addAll(list);
        }
        Collections.reverse(allList);
        log.info("{}", "总共获取" + allList.size() + "条数据，开始入库");

        for (FundNav fundNav : allList) {
            FundNav exist = fundNavMapper.selectByFundCodeAndNavDate(fundNav.getFundCode(), fundNav.getNavDate());
            if (exist == null) {
                fundNav.setCreateTime(LocalDateTime.now());
                fundNav.setUpdateTime(LocalDateTime.now());
                fundNavMapper.insert(fundNav);
            } else {
                fundNav.setId(exist.getId());
                fundNav.setCreateTime(exist.getCreateTime());
                fundNav.setUpdateTime(LocalDateTime.now());
                fundNavMapper.updateByPrimaryKey(fundNav);
            }
        }
    }

    @SneakyThrows
    private String doRequest(String url) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header(LokTarConstant.HTTP_HEADER_ACCEPT_ENCODING_NAME, "")
                .timeout(Duration.ofSeconds(30))
                .GET()
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    private int extractPages(String response) {
        Matcher matcher = PAGES_PATTERN.matcher(response);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        }
        return 1;
    }

    private List<FundNav> parseFundNav(String response, String code) {
        List<FundNav> result = new ArrayList<>();
        String contentPrefix = "content:\"";
        int contentStart = response.indexOf(contentPrefix);
        if (contentStart == -1) {
            return result;
        }
        contentStart += contentPrefix.length();
        int contentEnd = response.indexOf("\",records", contentStart);
        if (contentEnd == -1) {
            contentEnd = response.indexOf("\"", contentStart);
        }
        if (contentEnd == -1) {
            return result;
        }
        String html = response.substring(contentStart, contentEnd).replace("\\", "");
        Document document = Jsoup.parse(html);
        Elements rows = document.select("tbody tr");
        for (Element row : rows) {
            Elements tds = row.select("td");
            if (tds.size() < 7) {
                continue;
            }
            FundNav fundNav = new FundNav();
            fundNav.setFundCode(code);
            fundNav.setNavDate(LocalDate.parse(tds.get(0).text().trim(), DateTimeUtil.FORMATTER_DATE));
            fundNav.setUnitNav(new BigDecimal(tds.get(1).text().trim()));
            fundNav.setAccNav(new BigDecimal(tds.get(2).text().trim()));
            String growthRateStr = tds.get(3).text().trim().replace("%", "");
            if (!growthRateStr.isEmpty()) {
                fundNav.setGrowthRate(new BigDecimal(growthRateStr));
            }
            fundNav.setSubscribeStatus(tds.get(4).text().trim());
            fundNav.setRedeemStatus(tds.get(5).text().trim());
            String bonusText = tds.get(6).text().trim();
            if (!bonusText.isEmpty()) {
                java.util.regex.Pattern bonusPattern = java.util.regex.Pattern.compile("\\d+\\.\\d+");
                java.util.regex.Matcher bonusMatcher = bonusPattern.matcher(bonusText);
                if (bonusMatcher.find()) {
                    fundNav.setBonus(new BigDecimal(bonusMatcher.group()));
                }
            }
            result.add(fundNav);
        }
        return result;
    }
}
