package com.loktar.task.relx;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.loktar.conf.LokTarConfig;
import com.loktar.conf.LokTarConstant;
import com.loktar.dto.wx.agentmsg.AgentMsgText;
import com.loktar.util.DateTimeUtil;
import com.loktar.util.RedisUtil;
import com.loktar.util.VapeOnlineUtil;
import com.loktar.util.wx.qywx.QywxApi;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
@EnableScheduling
@Profile(LokTarConstant.ENV_PRO)
public class RelxTask {

    private final LokTarConfig lokTarConfig;

    private final RedisUtil redisUtil;

    private final QywxApi qywxApi;

    private static ObjectMapper OBJECT_MAPPER = new ObjectMapper();


    public RelxTask(LokTarConfig lokTarConfig, RedisUtil redisUtil, QywxApi qywxApi) {
        this.lokTarConfig = lokTarConfig;
        this.redisUtil = redisUtil;
        this.qywxApi = qywxApi;
    }

    @Scheduled(cron = "0 */3 * * * *")
    @SneakyThrows
    public void relxStockAvailable() {
        System.out.println("华人蒸汽库存定时器开始：" + DateTimeUtil.getDatetimeStr(LocalDateTime.now(),DateTimeUtil.FORMATTER_DATESECOND));
        List<VapeOnlineUtil.Product> products = VapeOnlineUtil.getInStockProductsAndStockInfo();
        String nowProductsJson = OBJECT_MAPPER.writeValueAsString(products);
        String lastProductsJson = (String) redisUtil.get(LokTarConstant.REDIS_KEY_RELX);

        if (!nowProductsJson.equals(lastProductsJson)) {
            String nowInStock = products.stream()
                    .map(p -> p.getName().replaceAll("[a-zA-Z]+ ?", "").replaceAll("【","[").replaceAll("】","]") + "," + p.getStockQuantity())
                    .sorted()
                    .collect(Collectors.joining(System.lineSeparator()));
            String content = LokTarConstant.NOTICE_RELX_STOCK + System.lineSeparator() +
                    System.lineSeparator() +
                    nowInStock + System.lineSeparator() +
                    System.lineSeparator() +
                    DateTimeUtil.getDatetimeStr(LocalDateTime.now(), DateTimeUtil.FORMATTER_DATEMINUTE);
            qywxApi.sendTextMsg(new AgentMsgText(lokTarConfig.getQywx().getNoticeZxb(), lokTarConfig.getQywx().getAgent002Id(), content));
            redisUtil.set(LokTarConstant.REDIS_KEY_RELX, nowProductsJson);
        }
        System.out.println("华人蒸汽库存定时器结束：" + DateTimeUtil.getDatetimeStr(LocalDateTime.now(),DateTimeUtil.FORMATTER_DATESECOND));
    }
}
