package com.loktar.task.common;


import com.loktar.conf.LokTarConstant;
import com.loktar.conf.LokTarPrivateConstant;
import com.loktar.domain.common.Property;
import com.loktar.dto.wx.agentmsg.AgentMsgText;
import com.loktar.mapper.common.PropertyMapper;
import com.loktar.util.DateUtil;
import com.loktar.util.wx.qywx.QywxApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Configuration
@EnableScheduling
public class CertbotTask {
    @Value("${spring.profiles.active}")
    private String env;

    private final PropertyMapper propertyMapper;

    private final QywxApi qywxApi;

    public CertbotTask(PropertyMapper propertyMapper, QywxApi qywxApi) {
        this.propertyMapper = propertyMapper;
        this.qywxApi = qywxApi;
    }

    @Scheduled(cron = "0 30 10 * * MON-FRI")
    public void certbotNotice() {
        if (!env.equals("pro")) {
            return;
        }
        Property property = propertyMapper.selectByPrimaryKey("cert");
        String expireDateStr = property.getValue();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate expireDate = LocalDate.parse(expireDateStr, formatter);
        // 获取当前日期
        LocalDate today = LocalDate.now();
        // 当前日期加上25天
        LocalDate todayPlus25Days = today.plusDays(25);

        if (todayPlus25Days.isAfter(expireDate)) {

            String content = LokTarConstant.NOTICE_CERT_UPDATE + "\n\n"
                    + "请更新证书"
                    + "\n\n" + DateUtil.getMinuteSysDate();
            qywxApi.sendTextMsg(new AgentMsgText(LokTarPrivateConstant.NOTICE_ZXB, LokTarPrivateConstant.AGENT002ID, content));
        }
    }
}
