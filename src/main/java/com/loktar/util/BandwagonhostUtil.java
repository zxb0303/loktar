package com.loktar.util;


import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.loktar.conf.LokTarConstant;
import com.loktar.conf.LokTarPrivateConstant;
import com.loktar.dto.bandwagonhost.VPSInfo;
import lombok.SneakyThrows;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.MessageFormat;
import java.time.Duration;
import java.util.Calendar;

public class BandwagonhostUtil {

    private final static String URL = "https://api.64clouds.com/v1/getServiceInfo?veid={0}&&api_key={1}";

    @SneakyThrows
    public static VPSInfo getVPSData(String veid) {
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(MessageFormat.format(URL, veid, LokTarPrivateConstant.BWG_API_KEY)))
                .timeout(Duration.ofSeconds(10))
                .header(LokTarConstant.HTTP_HEADER_USER_AGENT_NAME, LokTarConstant.HTTP_HEADER_USER_AGENT_VALUE)
                .header(LokTarConstant.HTTP_HEADER_ACCEPT_NAME, LokTarConstant.HTTP_HEADER_ACCEPT_VALUE_JSON)
                .GET()
                .build();
        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        VPSInfo vpsInfo = objectMapper.readValue(response.body(), VPSInfo.class);
        return vpsInfo;
    }

    public static void main(String[] args) {
        String veids[] = new String[]{"1830460", "1830460"};
        String replymsg = "当前搬瓦工VPS信息如下：";
        for (String veid : veids) {
            VPSInfo vpsInfoDTO = getVPSData(veid);
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(vpsInfoDTO.getDataNextReset() * 1000);
            System.out.println(calendar.getTime());
            replymsg = replymsg + "\n\n" + vpsInfoDTO.getHostname() + "\n"
                    + "IP：" + vpsInfoDTO.getIpAddresses()[0] + "\n"
                    + "Bandwidth：" + vpsInfoDTO.getDataCounter() / 1024 / 1024 / 1024 + "GB" + "/" + vpsInfoDTO.getPlanMonthlyData() / 1024 / 1024 / 1024 + "GB" + "\n"
                    + "Reset：" + DateUtil.format(calendar.getTime(), DateUtil.DATEFORMATDAY);
        }
        System.out.println(replymsg);
    }
}
