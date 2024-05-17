package com.loktar.task.common;


import com.loktar.conf.LokTarConfig;
import com.loktar.conf.LokTarConstant;
import com.loktar.domain.common.Property;
import com.loktar.dto.wx.agentmsg.AgentMsgText;
import com.loktar.mapper.common.PropertyMapper;
import com.loktar.util.DateTimeUtil;
import com.loktar.util.wx.qywx.QywxApi;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
@EnableScheduling
@Profile(LokTarConstant.ENV_PRO)
public class CertbotTask {

    private final PropertyMapper propertyMapper;

    private final QywxApi qywxApi;

    private final LokTarConfig lokTarConfig;

    public CertbotTask(PropertyMapper propertyMapper, QywxApi qywxApi, LokTarConfig lokTarConfig) {
        this.propertyMapper = propertyMapper;
        this.qywxApi = qywxApi;
        this.lokTarConfig = lokTarConfig;
    }

//    @Scheduled(cron = "0 30 10 * * MON-FRI")
    public void certbotNotice() {
        Property property = propertyMapper.selectByPrimaryKey("cert");
        String expireDateStr = property.getValue();
        LocalDate expireDate = DateTimeUtil.parseLocalDate(expireDateStr, DateTimeUtil.FORMATTER_DATE);
        LocalDate todayAdd25Days = LocalDate.now().plusDays(25);
        if (todayAdd25Days.isAfter(expireDate)) {
            String content = LokTarConstant.NOTICE_CERT_UPDATE + System.lineSeparator() +
                    System.lineSeparator() +
                    "请更新证书" + System.lineSeparator() +
                    System.lineSeparator() +
                    DateTimeUtil.getDatetimeStr(LocalDateTime.now(), DateTimeUtil.FORMATTER_DATEMINUTE);
            qywxApi.sendTextMsg(new AgentMsgText(lokTarConfig.getQywx().getNoticeZxb(), lokTarConfig.getQywx().getAgent002Id(), content));
        }
    }
}
