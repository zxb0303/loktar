package com.loktar.task.common;

import com.loktar.conf.LokTarConstant;
import com.loktar.conf.LokTarPrivateConstant;
import com.loktar.domain.common.Notice;
import com.loktar.dto.wx.agentmsg.AgentMsgText;
import com.loktar.service.common.CommonService;
import com.loktar.service.common.NoticeServer;
import com.loktar.util.DateUtil;
import com.loktar.util.wx.qywx.QywxApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Calendar;
import java.util.List;

@Configuration
@EnableScheduling
public class CommonTask {
    @Value("${spring.profiles.active}")
    private String env;

    private final CommonService commonService;

    private final NoticeServer noticeServer;

    private final QywxApi qywxApi;

    public CommonTask(CommonService commonService, NoticeServer noticeServer, QywxApi qywxApi) {
        this.commonService = commonService;
        this.noticeServer = noticeServer;
        this.qywxApi = qywxApi;
    }

    /**
     * @description: 根据notice表中配置的信息进行提醒
     * @param:
     * @retuan: void
     * @author: zxb
     * @createTime: 2021-06-02 14:38
     */
    @Scheduled(cron = "0 */1 * * * ?")
    public void commonNotice() {
        if (!env.equals("pro")) {
            return;
        }
        List<Notice> notices = noticeServer.selectAll();
        for (Notice notice : notices) {
            if (DateUtil.getMinuteSysDate().equals(notice.getNoticeTime())) {
                String content = notice.getNoticeTitle() + "\n\n"
                        + notice.getNoticeContent()
                        + "\n\n" + DateUtil.getMinuteSysDate();
                qywxApi.sendTextMsg(new AgentMsgText(notice.getNoticeUser(), LokTarPrivateConstant.AGENT002ID, content));
            }
        }

    }


    /**
     * @description: 每天早上7点半推送 摇号提醒
     * @param:
     * @retuan: void
     * @author: zxb
     * @createTime: 2021-06-02 14:38
     */
    @Scheduled(cron = "0 30 7 * * ?")
    private void lotteryNotice() {
        if (!env.equals("pro")) {
            return;
        }
//        commonService.sendLandNotice();
//        commonService.sendSecondHandHouseNotice();
        commonService.sendLotteryNotice();
//        commonService.sendNewHouseNotice();
    }

    /**
     * @description: 每个月最后一个工作日下午5点推送消息 小陈税盘提醒
     * @param:
     * @retuan: void
     * @author: zxb
     * @createTime: 2021-06-02 14:38
     */
    @Scheduled(cron = "0 30 17 * * ?")
    private void CXYnotice() {
        if (!env.equals("pro")) {
            return;
        }
        Calendar today = Calendar.getInstance();
        Calendar sendDay = Calendar.getInstance();
        sendDay.set(Calendar.DATE, 1);
        sendDay.roll(Calendar.DATE, -1);
        if (sendDay.get(Calendar.DAY_OF_WEEK) == 1) {
            sendDay.add(Calendar.DATE, -2);
        }
        if (sendDay.get(Calendar.DAY_OF_WEEK) == 7) {
            sendDay.add(Calendar.DATE, -1);
        }
        if (today.get(Calendar.DAY_OF_YEAR) == sendDay.get(Calendar.DAY_OF_YEAR)) {
            String content = LokTarConstant.NOTICE_TITLE_WORK + "\n\n"
                    + LokTarPrivateConstant.CXY_NOTICE_MSG
                    + "\n\n" + DateUtil.getMinuteSysDate();
            qywxApi.sendTextMsg(new AgentMsgText(LokTarPrivateConstant.NOTICE_CXY, LokTarPrivateConstant.AGENT002ID, content));
        }
    }

}
