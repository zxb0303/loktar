package com.loktar.task.cxy;


import com.loktar.conf.LokTarConfig;
import com.loktar.conf.LokTarConstant;
import com.loktar.domain.cxy.Employee;
import com.loktar.dto.wx.agentmsg.AgentMsgText;
import com.loktar.mapper.cxy.EmployeeMapper;
import com.loktar.util.DateTimeUtil;
import com.loktar.util.wx.qywx.QywxApi;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
@Component
@EnableScheduling
@Profile(LokTarConstant.ENV_PRO)
public class EmployeeTask {
    private final QywxApi qywxApi;

    private final EmployeeMapper employeeMapper;

    private final LokTarConfig lokTarConfig;


    public EmployeeTask(QywxApi qywxApi, EmployeeMapper employeeMapper, LokTarConfig lokTarConfig) {
        this.qywxApi = qywxApi;
        this.employeeMapper = employeeMapper;
        this.lokTarConfig = lokTarConfig;
    }

//    @Scheduled(cron = "0 0 10,11,12 * * MON-FRI")
    private void notice() {
        System.out.println("员工合同到期监测器：" + DateTimeUtil.getDatetimeStr(LocalDateTime.now(),DateTimeUtil.FORMATTER_DATESECOND));
        List<Employee> employees = employeeMapper.getNeedNoticeEmployees();
        if (ObjectUtils.isEmpty(employees)) {
            return;
        }
        StringBuilder content = new StringBuilder().append(LokTarConstant.NOTICE_TITLE_EMPLOYEE).append(System.lineSeparator());
        for (Employee employee : employees) {
            content.append(System.lineSeparator())
                    .append(employee.getName()).append(System.lineSeparator())
                    .append(System.lineSeparator()).append(employee.getEndDate()).append("到期").append(System.lineSeparator())
                    .append(System.lineSeparator());
            employee.setStatus(1);
            employeeMapper.updateByPrimaryKey(employee);
        }
        content.append(DateTimeUtil.getDatetimeStr(LocalDateTime.now(),DateTimeUtil.FORMATTER_DATEMINUTE));
        //qywxApi.sendTextMsg(new AgentMsgText(lokTarConfig.getQywx().getNoticeZxb(), lokTarConfig.getQywx().getAgent002Id(), content));
        qywxApi.sendTextMsg(new AgentMsgText(lokTarConfig.getQywx().getNoticeCxy(), lokTarConfig.getQywx().getAgent002Id(), content.toString()));
    }
}
