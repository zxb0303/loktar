package com.loktar.task.car;


import com.loktar.conf.LokTarConfig;
import com.loktar.conf.LokTarConstant;
import com.loktar.domain.common.Property;
import com.loktar.dto.wx.agentmsg.AgentMsgText;
import com.loktar.mapper.common.PropertyMapper;
import com.loktar.util.CarUtil;
import com.loktar.util.DateUtil;
import com.loktar.util.wx.qywx.QywxApi;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
public class CarTask {

    private final QywxApi qywxApi;

    private final PropertyMapper propertyMapper;

    private final LokTarConfig lokTarConfig;


    public CarTask(QywxApi qywxApi, PropertyMapper propertyMapper, LokTarConfig lokTarConfig) {
        this.qywxApi = qywxApi;
        this.propertyMapper = propertyMapper;
        this.lokTarConfig = lokTarConfig;
    }

    @Scheduled(cron = "0 */10 * * * ?")
    private void notice() {
        if (!lokTarConfig.env.equals(LokTarConstant.ENV_PRO)) {
            return;
        }
        Property xc90AppVersionProperty = propertyMapper.selectByPrimaryKey("xc90_app_version");
        String lastVersion = CarUtil.getLastVersion();
        if (!lastVersion.equals(xc90AppVersionProperty.getValue())) {
            String content = new StringBuilder().append(LokTarConstant.NOTICE_TITLE_CAR_VERSION).append(System.lineSeparator())
                    .append(System.lineSeparator())
                    .append(lastVersion).append(System.lineSeparator())
                    .append(System.lineSeparator())
                    .append(DateUtil.getMinuteSysDate()).toString();
            xc90AppVersionProperty.setValue(lastVersion);
            propertyMapper.updateByPrimaryKey(xc90AppVersionProperty);
            qywxApi.sendTextMsg(new AgentMsgText(lokTarConfig.qywxNoticeZxb, lokTarConfig.qywxAgent002Id, content));
        }
    }


}
