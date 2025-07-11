package com.loktar.task.common;

import com.loktar.conf.LokTarConfig;
import com.loktar.conf.LokTarConstant;
import com.loktar.domain.common.Notice;
import com.loktar.dto.wx.agentmsg.AgentMsgText;
import com.loktar.service.common.CommonService;
import com.loktar.service.common.NoticeServer;
import com.loktar.util.DateTimeUtil;
import com.loktar.util.wx.qywx.QywxApi;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Component
@EnableScheduling
@Profile("pro")
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
        List<Notice> notices = noticeServer.getUnsendNotices();
        for (Notice notice : notices) {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime noticeTime = LocalDateTime.parse(notice.getNoticeTime(), DateTimeUtil.FORMATTER_DATEMINUTE);
            if (now.isAfter(noticeTime)) {
                String content = notice.getNoticeTitle() + System.lineSeparator() +
                        System.lineSeparator() +
                        notice.getNoticeContent() + System.lineSeparator() +
                        System.lineSeparator() +
                        DateTimeUtil.getDatetimeStr(LocalDateTime.now(), DateTimeUtil.FORMATTER_DATEMINUTE);
                qywxApi.sendTextMsg(new AgentMsgText(notice.getNoticeUser(), lokTarConfig.getQywx().getAgent002Id(), content));
                notice.setStatus(1);
                noticeServer.updateByPrimaryKey(notice);
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
//        commonService.sendLandNotice();
//        commonService.sendSecondHandHouseNotice();
        commonService.sendLotteryNotice();
//        commonService.sendNewHouseNotice();
    }

    /**
     * @description: 每个月最后一个工作日下午5点推送消息给小陈
     * @param:
     * @retuan: void
     * @author: zxb
     * @createTime: 2021-06-02 14:38
     */
    @Scheduled(cron = "0 30 17 * * ?")
    private void CXYnotice() {
        LocalDate today = LocalDate.now();
        LocalDate lastDayOfMonth = today.with(TemporalAdjusters.lastDayOfMonth());

        // 如果最后一天是周日，则向前推两天到周五
        if (lastDayOfMonth.getDayOfWeek() == DayOfWeek.SUNDAY) {
            lastDayOfMonth = lastDayOfMonth.minusDays(2);
        }
        // 如果最后一天是周六，则向前推一天到周五
        else if (lastDayOfMonth.getDayOfWeek() == DayOfWeek.SATURDAY) {
            lastDayOfMonth = lastDayOfMonth.minusDays(1);
        }
        if (today.equals(lastDayOfMonth)) {
            String content = LokTarConstant.NOTICE_TITLE_WORK + System.lineSeparator() +
                    System.lineSeparator() +
                    "今天是本月最后1个工作日" + System.lineSeparator() +
                    DateTimeUtil.getDatetimeStr(LocalDateTime.now(), DateTimeUtil.FORMATTER_DATEMINUTE);
            qywxApi.sendTextMsg(new AgentMsgText(lokTarConfig.getQywx().getNoticeCxy(), lokTarConfig.getQywx().getAgent002Id(), content));
        }
    }


    /**
     * @description: 每月第一个工作日**前一天**下午5点半提醒小陈
     * @param:
     * @retuan: void
     * @author: zxb
     * @createTime: 2024-06-19
     */
    @Scheduled(cron = "0 30 17 * * ?")
    private void CXYnotice2() {
        LocalDate today = LocalDate.now();

        // 计算下一个月的第一个工作日
        LocalDate nextMonth = today.withDayOfMonth(1).plusMonths(1);
        LocalDate firstWorkdayOfNextMonth = nextMonth;
        while (firstWorkdayOfNextMonth.getDayOfWeek() == DayOfWeek.SATURDAY
                || firstWorkdayOfNextMonth.getDayOfWeek() == DayOfWeek.SUNDAY) {
            firstWorkdayOfNextMonth = firstWorkdayOfNextMonth.plusDays(1);
        }

        // 需要提醒的日期（即下个月第一个工作日的前一天）
        LocalDate remindDay = firstWorkdayOfNextMonth.minusDays(1);

        if (today.equals(remindDay)) {
            String content = LokTarConstant.NOTICE_TITLE_WORK + System.lineSeparator()
                    + System.lineSeparator()
                    + "明天是当月第一个工作日，记得去拿回单" + System.lineSeparator()
                    + DateTimeUtil.getDatetimeStr(LocalDateTime.now(), DateTimeUtil.FORMATTER_DATEMINUTE);
            qywxApi.sendTextMsg(new AgentMsgText(lokTarConfig.getQywx().getNoticeCxy(), lokTarConfig.getQywx().getAgent002Id(), content)
            );
        }
    }

}
