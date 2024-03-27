package com.loktar.task.ip;


import com.loktar.conf.LokTarConstant;
import com.loktar.conf.LokTarPrivateConstant;
import com.loktar.domain.common.Property;
import com.loktar.dto.wx.agentmsg.AgentMsgText;
import com.loktar.mapper.common.PropertyMapper;
import com.loktar.util.DateUtil;
import com.loktar.util.IPUtil;
import com.loktar.util.wx.qywx.QywxApi;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
public class IpTask {
    @Value("${spring.profiles.active}")
    private String env;

    private final QywxApi qywxApi;

    private final PropertyMapper propertyMapper;

    public IpTask(QywxApi qywxApi, PropertyMapper propertyMapper) {
        this.qywxApi = qywxApi;
        this.propertyMapper = propertyMapper;
    }

    @Scheduled(cron = "0 */10 * * * ?")
    private void notice() {
        if (!env.equals("pro")) {
            return;
        }
        System.out.println("IP检测定时器：" + DateUtil.getTodayToSecond());
        Property ipProperty = propertyMapper.selectByPrimaryKey("yht_ip");
        String ip = IPUtil.getip();
        if (!ObjectUtils.isEmpty(ip) && !ipProperty.getValue().equals(ip)) {
            String content = LokTarConstant.NOTICE_TITLE_IP + "\n\n"
                    + ip
                    + "\n\n" + DateUtil.getMinuteSysDate();
            ipProperty.setValue(ip);
            propertyMapper.updateByPrimaryKey(ipProperty);
            qywxApi.sendTextMsg(new AgentMsgText(LokTarPrivateConstant.NOTICE_ZXB, LokTarPrivateConstant.AGENT002ID, content));
        }
    }
}
