package com.loktar.task.relx;

import com.loktar.conf.LokTarConfig;
import com.loktar.conf.LokTarConstant;
import com.loktar.dto.wx.agentmsg.AgentMsgText;
import com.loktar.util.DateTimeUtil;
import com.loktar.util.RedisUtil;
import com.loktar.util.VapeOnlineUtil;
import com.loktar.util.wx.qywx.QywxApi;
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

    public RelxTask(LokTarConfig lokTarConfig, RedisUtil redisUtil, QywxApi qywxApi) {
        this.lokTarConfig = lokTarConfig;
        this.redisUtil = redisUtil;
        this.qywxApi = qywxApi;
    }

    @Scheduled(cron = "0 */1 * * * *")
    public void relxStockAvailable() {
        List<VapeOnlineUtil.Product> products = VapeOnlineUtil.getInStockProducts();
        List<String> skuList = products.stream()
                .map(VapeOnlineUtil.Product::getName)
                // 去除所有英文和紧跟的空格
                .map(name -> name.replaceAll("[a-zA-Z]+ ?",""))
                .sorted()
                .collect(Collectors.toList());
        String nowInStock = String.join(",", skuList);

        String last = (String) redisUtil.get(LokTarConstant.REDIS_KEY_RELX);
        if (!nowInStock.equals(last)) {
            String content = LokTarConstant.NOTICE_RELX_STOCK + System.lineSeparator() +
                    System.lineSeparator() +
                    nowInStock + System.lineSeparator() +
                    System.lineSeparator() +
                    DateTimeUtil.getDatetimeStr(LocalDateTime.now(), DateTimeUtil.FORMATTER_DATEMINUTE);
            qywxApi.sendTextMsg(new AgentMsgText(lokTarConfig.getQywx().getNoticeZxb(), lokTarConfig.getQywx().getAgent002Id(), content));
            redisUtil.set(LokTarConstant.REDIS_KEY_RELX, nowInStock);
        }
    }
}
