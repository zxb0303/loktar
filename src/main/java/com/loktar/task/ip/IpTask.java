package com.loktar.task.ip;


import com.loktar.conf.LokTarConfig;
import com.loktar.conf.LokTarConstant;
import com.loktar.domain.common.Property;
import com.loktar.dto.wx.agentmsg.AgentMsgText;
import com.loktar.mapper.common.PropertyMapper;
import com.loktar.util.DateTimeUtil;
import com.loktar.util.IPUtil;
import com.loktar.util.wx.qywx.QywxApi;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@EnableScheduling
@Profile(LokTarConstant.ENV_PRO)
public class IpTask {

    private final QywxApi qywxApi;

    private final PropertyMapper propertyMapper;

    private final LokTarConfig lokTarConfig;

    private final IPUtil ipUtil;

    public IpTask(QywxApi qywxApi, PropertyMapper propertyMapper, LokTarConfig lokTarConfig, IPUtil ipUtil) {
        this.qywxApi = qywxApi;
        this.propertyMapper = propertyMapper;
        this.lokTarConfig = lokTarConfig;
        this.ipUtil = ipUtil;
    }

    @Scheduled(cron = "0 */10 * * * ?")
    private void notice() {
        System.out.println("IP检测定时器：" + DateTimeUtil.getDatetimeStr(LocalDateTime.now(),DateTimeUtil.FORMATTER_DATESECOND));
        Property ipProperty = propertyMapper.selectByPrimaryKey("yht_ip");
        String ip = ipUtil.getip();
        if (!ObjectUtils.isEmpty(ip) && !ipProperty.getValue().equals(ip)) {
            String content = LokTarConstant.NOTICE_TITLE_IP + System.lineSeparator() +
                    System.lineSeparator() +
                    ip + System.lineSeparator() +
                    System.lineSeparator() +
                    DateTimeUtil.getDatetimeStr(LocalDateTime.now(), DateTimeUtil.FORMATTER_DATEMINUTE);
            ipProperty.setValue(ip);
            propertyMapper.updateByPrimaryKey(ipProperty);
            qywxApi.sendTextMsg(new AgentMsgText(lokTarConfig.getQywx().getNoticeZxb(), lokTarConfig.getQywx().getAgent002Id(), content));
        }
    }
}
