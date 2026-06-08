package com.loktar.task.investment;

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
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

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
import java.util.List;

@Component
@EnableScheduling
@Profile(LokTarConstant.ENV_PRO)
public class FundNavTask {

    private final FundNavMapper fundNavMapper;
    private final PropertyMapper propertyMapper;
    private final QywxApi qywxApi;
    private final LokTarConfig lokTarConfig;

    private static final String FUND_NAV_URL = "https://fundf10.eastmoney.com/F10DataApi.aspx?type=lsjz&code={0}&sdate={1}&edate={2}&per={3}&page={4}";
    private static final List<String> FUND_CODES = List.of("021550");

    public FundNavTask(FundNavMapper fundNavMapper, PropertyMapper propertyMapper, QywxApi qywxApi, LokTarConfig lokTarConfig) {
        this.fundNavMapper = fundNavMapper;
        this.propertyMapper = propertyMapper;
        this.qywxApi = qywxApi;
        this.lokTarConfig = lokTarConfig;
    }

    @Scheduled(cron = "0 0/10 19-23 * * *")
    @SneakyThrows
    public void syncToday() {
        LocalDate today = LocalDate.now();
        String todayStr = today.format(DateTimeUtil.FORMATTER_DATE2);

        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();

        for (String fundCode : FUND_CODES) {
            FundNav exist = fundNavMapper.selectByFundCodeAndNavDate(fundCode, today);
            if (exist != null) {
                System.out.println(fundCode + " 当日数据已存在，跳过");
                continue;
            }

            String url = MessageFormat.format(FUND_NAV_URL, fundCode, todayStr, todayStr, "1", "1");
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header(LokTarConstant.HTTP_HEADER_ACCEPT_ENCODING_NAME, "")
                    .timeout(Duration.ofSeconds(30))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            List<FundNav> list = parseFundNav(response.body(), fundCode);

            if (list.isEmpty()) {
                System.out.println(fundCode + " 未获取到数据");
                continue;
            }

            FundNav fundNav = list.get(0);
            if (!today.equals(fundNav.getNavDate())) {
                System.out.println(fundCode + " 接口返回日期非今日，跳过");
                continue;
            }

            FundNav current = fundNavMapper.selectByFundCodeAndNavDate(fundNav.getFundCode(), fundNav.getNavDate());
            if (current == null) {
                fundNav.setCreateTime(LocalDateTime.now());
                fundNav.setUpdateTime(LocalDateTime.now());
                fundNavMapper.insert(fundNav);
                System.out.println(fundCode + " 新增成功：" + fundNav.getNavDate());
                Property property = propertyMapper.selectByPrimaryKey("fund_nav_" + fundCode);
                if (property != null && property.getValue() != null) {
                    BigDecimal share = new BigDecimal(property.getValue());
                    BigDecimal total = share.multiply(fundNav.getUnitNav()).setScale(2, RoundingMode.HALF_UP);
                    StringBuilder msg = new StringBuilder();
                    msg.append(property.getType()).append(System.lineSeparator());
                    msg.append(System.lineSeparator());
                    msg.append("持有份额：").append(share).append(System.lineSeparator());
                    msg.append("涨幅：").append(fundNav.getGrowthRate() != null ? fundNav.getGrowthRate().setScale(2, RoundingMode.HALF_UP) + "%" : "").append(System.lineSeparator());
                    msg.append("当日净值：").append(fundNav.getUnitNav()).append(System.lineSeparator());
                    msg.append("资产合计：").append(total).append(System.lineSeparator());
                    if (fundNav.getBonus() != null) {
                        msg.append("每份分红：").append(fundNav.getBonus()).append(System.lineSeparator());
                    }
                    msg.append(System.lineSeparator());
                    msg.append(DateTimeUtil.getDatetimeStr(LocalDateTime.now(), DateTimeUtil.FORMATTER_DATEMINUTE));
                    qywxApi.sendTextMsg(new AgentMsgText(lokTarConfig.getQywx().getNoticeZxb(), lokTarConfig.getQywx().getAgent009Id(), msg.toString()));
                }
            }
        }
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
