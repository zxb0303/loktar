package com.loktar.task.minecraft;


import lombok.extern.slf4j.Slf4j;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.loktar.conf.LokTarConfig;
import com.loktar.conf.LokTarConstant;
import com.loktar.dto.minecraft.BedrockVersionsDTO;
import com.loktar.dto.wx.agentmsg.AgentMsgText;
import com.loktar.util.DateTimeUtil;
import com.loktar.util.PortainerUtil;
import com.loktar.util.wx.qywx.QywxApi;
import lombok.SneakyThrows;
import me.dilley.MineStat;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

@Component
@Slf4j
public class MinecraftTask {

    private final static String VERSIONS_URL = "https://raw.githubusercontent.com/EndstoneMC/bedrock-server-data/refs/heads/v2/versions.json";

    private final LokTarConfig lokTarConfig;

    private final HttpClient httpClient;

    private final PortainerUtil portainerUtil;

    private final QywxApi qywxApi;

    private final static ObjectMapper objectMapper = new ObjectMapper();

    public MinecraftTask(LokTarConfig lokTarConfig, HttpClient httpClient, PortainerUtil portainerUtil, QywxApi qywxApi) {
        this.lokTarConfig = lokTarConfig;
        this.httpClient = httpClient;
        this.portainerUtil = portainerUtil;
        this.qywxApi = qywxApi;
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @SneakyThrows
    @Scheduled(cron = "0 */30 * * * ?")
    public void checkVersion() {
        log.info("{}", "Minecraft版本检测定时器：" + DateTimeUtil.getDatetimeStr(LocalDateTime.now(), DateTimeUtil.FORMATTER_DATESECOND));
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(VERSIONS_URL))
                .timeout(Duration.ofSeconds(30))
                .header(LokTarConstant.HTTP_HEADER_USER_AGENT_NAME, LokTarConstant.HTTP_HEADER_USER_AGENT_VALUE)
                .header(LokTarConstant.HTTP_HEADER_ACCEPT_NAME, LokTarConstant.HTTP_HEADER_ACCEPT_VALUE_JSON)
                .GET()
                .build();
        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        BedrockVersionsDTO bedrockVersionsDTO = objectMapper.readValue(response.body(), BedrockVersionsDTO.class);
        String latestVersion = bedrockVersionsDTO.getRelease().getLatest();

        MineStat mineStat = new MineStat(lokTarConfig.getMinecraft().getHost(), lokTarConfig.getMinecraft().getPort(), 5, MineStat.Request.BEDROCK, true);
        if (!mineStat.isServerUp()) {
            log.warn("{}", "Minecraft服务器离线，跳过本次检测");
            return;
        }
        String rawVersion = mineStat.getVersion();
        if (ObjectUtils.isEmpty(rawVersion)) {
            log.warn("{}", "未获取到Minecraft服务器版本号，跳过本次检测");
            return;
        }
        //MineStat返回的版本串可能附带世界名等信息，如"1.26.40 Synology-world (MCPE)"，仅取首个空格前的版本号
        String localVersion = rawVersion.split(" ")[0];
        String containerName = lokTarConfig.getMinecraft().getContainerName();
        if (localVersion.equals(latestVersion)) {
//            log.info("{}", "Minecraft版本已是最新：" + localVersion);
            return;
        }
        if (bedrockVersionsDTO.getRelease().getVersions().contains(localVersion)) {
            log.info("{}", "检测到Minecraft新版本，重启容器" + containerName + "，当前版本：" + localVersion + "，最新版本：" + latestVersion);
            boolean restarted = portainerUtil.restartContainerByName(containerName);
            if (!restarted) {
                log.error("{}", "容器" + containerName + "重启失败，本次不发送通知，下个周期将重试");
                return;
            }
            String content = LokTarConstant.NOTICE_TITLE_MINECRAFT + System.lineSeparator() +
                    System.lineSeparator() +
                    "当前：" + localVersion + " → 最新：" + latestVersion + System.lineSeparator() +
                    System.lineSeparator() +
                    DateTimeUtil.getDatetimeStr(LocalDateTime.now(), DateTimeUtil.FORMATTER_DATEMINUTE);
            qywxApi.sendTextMsg(new AgentMsgText(lokTarConfig.getQywx().getNoticeZxb(), lokTarConfig.getQywx().getAgent002Id(), content));
        } else {
            log.warn("{}", "本地版本不在release版本列表中，可能为preview构建，跳过重启：" + localVersion);
        }
    }
}
