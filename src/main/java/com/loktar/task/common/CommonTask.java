package com.loktar.task.common;

import com.loktar.conf.LokTarConfig;
import com.loktar.conf.LokTarConstant;
import com.loktar.domain.common.Notice;
import com.loktar.dto.wx.agentmsg.AgentMsgText;
import com.loktar.service.common.CommonService;
import com.loktar.service.common.NoticeServer;
import com.loktar.util.DateUtil;
import com.loktar.util.wx.qywx.QywxApi;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.List;
@Component
@EnableScheduling
public class CommonTask {

    private final CommonService commonService;

    private final NoticeServer noticeServer;

    private final QywxApi qywxApi;

    private final LokTarConfig lokTarConfig;


    public CommonTask(CommonService commonService, NoticeServer noticeServer, QywxApi qywxApi, LokTarConfig lokTarConfig) {
        this.commonService = commonService;
        this.noticeServer = noticeServer;
        this.qywxApi = qywxApi;
        this.lokTarConfig = lokTarConfig;
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
        if (!lokTarConfig.env.equals(LokTarConstant.ENV_PRO)) {
            return;
        }
        List<Notice> notices = noticeServer.selectAll();
        for (Notice notice : notices) {
            if (DateUtil.getMinuteSysDate().equals(notice.getNoticeTime())) {
                String content = new StringBuilder().append(notice.getNoticeTitle()).append(System.lineSeparator())
                        .append(System.lineSeparator())
                        .append(notice.getNoticeContent()).append(System.lineSeparator())
                        .append(System.lineSeparator())
                        .append(DateUtil.getMinuteSysDate()).toString();
                qywxApi.sendTextMsg(new AgentMsgText(notice.getNoticeUser(), lokTarConfig.qywxAgent002Id, content));
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
        if (!lokTarConfig.env.equals(LokTarConstant.ENV_PRO)) {
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
        if (!lokTarConfig.env.equals(LokTarConstant.ENV_PRO)) {
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
            String content = new StringBuilder().append(LokTarConstant.NOTICE_TITLE_WORK).append(System.lineSeparator())
                    .append(System.lineSeparator())
                    .append(lokTarConfig.commonCxyNoticeText).append(System.lineSeparator())
                    .append(DateUtil.getMinuteSysDate()).toString();
            qywxApi.sendTextMsg(new AgentMsgText(lokTarConfig.qywxNoticeCxy, lokTarConfig.qywxAgent002Id, content));
        }
    }

}
