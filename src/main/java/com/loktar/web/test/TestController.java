package com.loktar.web.test;

import com.loktar.conf.LokTarConfig;
import com.loktar.conf.LokTarConstant;
import com.loktar.dto.wx.agentmsg.AgentMsgText;
import com.loktar.mapper.github.GithubRepositoryMapper;
import com.loktar.util.DateTimeUtil;
import com.loktar.util.IPUtil;
import com.loktar.util.RedisUtil;
import com.loktar.util.VapeOnlineUtil;
import com.loktar.util.wx.qywx.QywxApi;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("test")
public class TestController {

    private final LokTarConfig lokTarConfig;

    private final Environment environment;

    private final GithubRepositoryMapper githubRepositoryMapper;

    private final IPUtil ipUtil;

    private final RedisUtil redisUtil;

    private final QywxApi qywxApi;



    public TestController(LokTarConfig lokTarConfig, Environment environment, GithubRepositoryMapper githubRepositoryMapper, IPUtil ipUtil, RedisUtil redisUtil, QywxApi qywxApi) {

        this.lokTarConfig = lokTarConfig;
        this.environment = environment;
        this.githubRepositoryMapper = githubRepositoryMapper;
        this.ipUtil = ipUtil;
        this.redisUtil = redisUtil;
        this.qywxApi = qywxApi;
    }

    @GetMapping("/test.do")
    public void test() {
        List<VapeOnlineUtil.Product> products = VapeOnlineUtil.getInStockProducts();
        List<String> skuList = products.stream()
                .map(VapeOnlineUtil.Product::getName)
                // 去除所有英文和紧跟的空格
                .map(name -> name.replaceAll("[a-zA-Z]+ ?",""))
                .sorted()
                .collect(Collectors.toList());
        String nowInStock = String.join(System.lineSeparator(), skuList);
        String nowInStockForRedis = String.join(",", skuList);


        String last = (String) redisUtil.get(LokTarConstant.REDIS_KEY_RELX);
        if (!nowInStock.equals(last)) {
            String content = LokTarConstant.NOTICE_RELX_STOCK + System.lineSeparator() +
                    System.lineSeparator() +
                    nowInStock + System.lineSeparator() +
                    System.lineSeparator() +
                    DateTimeUtil.getDatetimeStr(LocalDateTime.now(), DateTimeUtil.FORMATTER_DATEMINUTE);
            qywxApi.sendTextMsg(new AgentMsgText(lokTarConfig.getQywx().getNoticeZxb(), lokTarConfig.getQywx().getAgent002Id(), content));
            redisUtil.set(LokTarConstant.REDIS_KEY_RELX, nowInStockForRedis);
        }
    }

}
