package com.loktar.task.car;


import com.loktar.conf.LokTarConstant;
import com.loktar.conf.LokTarPrivateConstant;
import com.loktar.domain.common.Property;
import com.loktar.dto.wx.agentmsg.AgentMsgText;
import com.loktar.mapper.common.PropertyMapper;
import com.loktar.util.CarUtil;
import com.loktar.util.DateUtil;
import com.loktar.util.wx.qywx.QywxApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
public class CarTask {
    @Value("${spring.profiles.active}")
    private String env;


    private final QywxApi qywxApi;


    private final PropertyMapper propertyMapper;

    public CarTask(QywxApi qywxApi, PropertyMapper propertyMapper) {
        this.qywxApi = qywxApi;
        this.propertyMapper = propertyMapper;
    }

    @Scheduled(cron = "0 */10 * * * ?")
    private void notice() {
        if (!env.equals("pro")) {
            return;
        }
        Property xc90AppVersionProperty = propertyMapper.selectByPrimaryKey("xc90_app_version");
        String lastVersion = CarUtil.getLastVersion();
        if (!lastVersion.equals(xc90AppVersionProperty.getValue())) {
            String content = LokTarConstant.NOTICE_TITLE_CAR_VERSION + "\n\n"
                    + lastVersion
                    + "\n\n" + DateUtil.getMinuteSysDate();
            xc90AppVersionProperty.setValue(lastVersion);
            propertyMapper.updateByPrimaryKey(xc90AppVersionProperty);
            qywxApi.sendTextMsg(new AgentMsgText(LokTarPrivateConstant.NOTICE_ZXB, LokTarPrivateConstant.AGENT002ID, content));
        }
    }


}
