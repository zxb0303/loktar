package com.loktar.task.cxy;

import com.loktar.conf.LokTarConfig;
import com.loktar.conf.LokTarConstant;
import com.loktar.dto.wx.agentmsg.AgentMsgText;
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

@Component
@EnableScheduling
@Profile("pro")
public class DayTask {

    private final QywxApi qywxApi;

    private final LokTarConfig lokTarConfig;


    public DayTask(QywxApi qywxApi, LokTarConfig lokTarConfig) {
        this.qywxApi = qywxApi;
        this.lokTarConfig = lokTarConfig;
    }

    /**
     * 每月第一个工作日 9点半提醒
     */
    @Scheduled(cron = "0 30 9 * * ?")
    private void zhiyiFlowRemind() {
        LocalDate today = LocalDate.now();
        LocalDate firstWorkday = getFirstWorkdayOfMonth(today);
        if (today.equals(firstWorkday)) {
            String content = LokTarConstant.NOTICE_TITLE_WORK + System.lineSeparator()
                    + System.lineSeparator()
                    + "芷懿流水 发王欢" + System.lineSeparator() +
                    DateTimeUtil.getDatetimeStr(LocalDateTime.now(), DateTimeUtil.FORMATTER_DATEMINUTE);
            qywxApi.sendTextMsg(new AgentMsgText(lokTarConfig.getQywx().getNoticeCxy(), lokTarConfig.getQywx().getAgent002Id(), content));
        }
    }

    /**
     * @description: 每个月最后一个工作日10点提醒
     * @param:
     * @retuan: void
     * @author: zxb
     * @createTime: 2021-06-02 14:38
     */
    @Scheduled(cron = "0 0 10 * * ?")
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
                    "今天是本月最后1个工作日，记得带上回单卡" + System.lineSeparator() +
                    DateTimeUtil.getDatetimeStr(LocalDateTime.now(), DateTimeUtil.FORMATTER_DATEMINUTE);
            qywxApi.sendTextMsg(new AgentMsgText(lokTarConfig.getQywx().getNoticeCxy(), lokTarConfig.getQywx().getAgent002Id(), content));
        }
    }

    /**
     * @description: 每个月最后一个工作日16点半提醒
     * @param:
     * @retuan: void
     * @author: zxb
     * @createTime: 2021-06-02 14:38
     */
    @Scheduled(cron = "0 0 10 * * ?")
    private void CXYnotice1() {
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
                    "芷懿流水 发王欢" + System.lineSeparator() +
                    DateTimeUtil.getDatetimeStr(LocalDateTime.now(), DateTimeUtil.FORMATTER_DATEMINUTE);
            qywxApi.sendTextMsg(new AgentMsgText(lokTarConfig.getQywx().getNoticeCxy(), lokTarConfig.getQywx().getAgent002Id(), content));
        }
    }

    /**
     * @description: 每月第一个工作日的前一天15点提醒
     * @param:
     * @retuan: void
     * @author: zxb
     * @createTime: 2024-06-19
     */
    @Scheduled(cron = "0 0 15 * * ?")
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
                    + "明天是当月第一个工作日，记得去拿回单，带上回单卡、包、雨伞" + System.lineSeparator()
                    + DateTimeUtil.getDatetimeStr(LocalDateTime.now(), DateTimeUtil.FORMATTER_DATEMINUTE);
            qywxApi.sendTextMsg(new AgentMsgText(lokTarConfig.getQywx().getNoticeCxy(), lokTarConfig.getQywx().getAgent002Id(), content)
            );
        }
    }


    /**
     * @description: 每月21日前的最后一个工作日15点提醒
     * @author: zxb
     * @createTime: 2024-06-20
     */
    @Scheduled(cron = "0 0 15 * * ?")
    private void gjjBalanceRemind() {
        LocalDate today = LocalDate.now();
        // 获取本月21号
        LocalDate day21 = today.withDayOfMonth(21);

        // 找到21号前的最后一个工作日
        LocalDate lastWorkdayBefore21 = null;
        // 从21号前一天开始往前找
        for (int d = 20; d >= 1; d--) {
            LocalDate date = today.withDayOfMonth(d);
            if (isWorkday(date)) {
                lastWorkdayBefore21 = date;
                break;
            }
        }

        // 今天是否就是21号前的最后一个工作日
        if (today.equals(lastWorkdayBefore21)) {
            // 发送通知
            String content = LokTarConstant.NOTICE_TITLE_WORK + System.lineSeparator()
                    + System.lineSeparator()
                    + "21号要扣缴公积金 确认账上余额是否充足" + System.lineSeparator()
                    + DateTimeUtil.getDatetimeStr(LocalDateTime.now(), DateTimeUtil.FORMATTER_DATEMINUTE);
            qywxApi.sendTextMsg(new AgentMsgText(
                    lokTarConfig.getQywx().getNoticeCxy(),
                    lokTarConfig.getQywx().getAgent002Id(),
                    content
            ));
        }
    }

    /**
     * @description: 每月21号为工作日则推送，否则提前至21号前的第一个工作日10点推送
     * @author: zxb
     * @createTime: 2024-06-20
     */
    @Scheduled(cron = "0 0 10 * * ?")
    private void gjjOpRemind() {
        LocalDate today = LocalDate.now();
        LocalDate baseDay = today.withDayOfMonth(21);

        LocalDate noticeDay;

        // 判断21号是否为周末
        if (baseDay.getDayOfWeek() != DayOfWeek.SATURDAY && baseDay.getDayOfWeek() != DayOfWeek.SUNDAY) {
            // 21号不是周末，就在21号推送
            noticeDay = baseDay;
        } else {
            // 21号是周末，找到21号前的最后一个工作日
            noticeDay = baseDay;
            do {
                noticeDay = noticeDay.minusDays(1);
            } while (noticeDay.getDayOfWeek() == DayOfWeek.SATURDAY || noticeDay.getDayOfWeek() == DayOfWeek.SUNDAY);
        }

        // 如果今天就是这个"需要通知的日子"，才发送
        if (today.equals(noticeDay)) {
            String content = LokTarConstant.NOTICE_TITLE_WORK + System.lineSeparator()
                    + System.lineSeparator()
                    + "住房公积金浙里办扣缴业务操作一下" + System.lineSeparator()
                    + DateTimeUtil.getDatetimeStr(LocalDateTime.now(), DateTimeUtil.FORMATTER_DATEMINUTE);
            qywxApi.sendTextMsg(new AgentMsgText(
                    lokTarConfig.getQywx().getNoticeCxy(),
                    lokTarConfig.getQywx().getAgent002Id(),
                    content
            ));
        }
    }

    /**
     * 判断工作日
     * 这里只做周末判断，你可以补充节假日等
     */
    private boolean isWorkday(LocalDate date) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        return dayOfWeek != DayOfWeek.SATURDAY && dayOfWeek != DayOfWeek.SUNDAY;
    }

    private LocalDate getFirstWorkdayOfMonth(LocalDate date) {
        LocalDate first = date.withDayOfMonth(1);
        while (!isWorkday(first)) {
            first = first.plusDays(1);
        }
        return first;
    }
}
