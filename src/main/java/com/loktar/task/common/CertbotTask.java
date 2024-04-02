package com.loktar.task.common;


import com.loktar.conf.LokTarConfig;
import com.loktar.conf.LokTarConstant;
import com.loktar.domain.common.Property;
import com.loktar.dto.wx.agentmsg.AgentMsgText;
import com.loktar.mapper.common.PropertyMapper;
import com.loktar.util.DateUtil;
import com.loktar.util.wx.qywx.QywxApi;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
@Component
@EnableScheduling
public class CertbotTask {

    private final PropertyMapper propertyMapper;

    private final QywxApi qywxApi;

    private final LokTarConfig lokTarConfig;

    public CertbotTask(PropertyMapper propertyMapper, QywxApi qywxApi, LokTarConfig lokTarConfig) {
        this.propertyMapper = propertyMapper;
        this.qywxApi = qywxApi;
        this.lokTarConfig = lokTarConfig;
    }

    @Scheduled(cron = "0 30 10 * * MON-FRI")
    public void certbotNotice() {
        if (!lokTarConfig.env.equals(LokTarConstant.ENV_PRO)) {
            return;
        }
        Property property = propertyMapper.selectByPrimaryKey("cert");
        String expireDateStr = property.getValue();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DateUtil.DATEFORMATDAY);
        LocalDate expireDate = LocalDate.parse(expireDateStr, formatter);
        // 获取当前日期
        LocalDate today = LocalDate.now();
        // 当前日期加上25天
        LocalDate todayPlus25Days = today.plusDays(25);

        if (todayPlus25Days.isAfter(expireDate)) {
            String content = new StringBuilder().append(LokTarConstant.NOTICE_CERT_UPDATE).append(System.lineSeparator())
                    .append(System.lineSeparator())
                    .append("请更新证书").append(System.lineSeparator())
                    .append(System.lineSeparator())
                    .append(DateUtil.getMinuteSysDate()).toString();
            qywxApi.sendTextMsg(new AgentMsgText(lokTarConfig.qywxNoticeZxb, lokTarConfig.qywxAgent002Id, content));
        }
    }
}
