package com.loktar.task.cxy;


import com.loktar.conf.LokTarConfig;
import com.loktar.conf.LokTarConstant;
import com.loktar.domain.cxy.Employee;
import com.loktar.dto.wx.agentmsg.AgentMsgText;
import com.loktar.mapper.cxy.EmployeeMapper;
import com.loktar.util.DateTimeUtil;
import com.loktar.util.wx.qywx.QywxApi;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
@Component
@EnableScheduling
public class EmployeeTask {
    private final QywxApi qywxApi;

    private final EmployeeMapper employeeMapper;

    private final LokTarConfig lokTarConfig;


    public EmployeeTask(QywxApi qywxApi, EmployeeMapper employeeMapper, LokTarConfig lokTarConfig) {
        this.qywxApi = qywxApi;
        this.employeeMapper = employeeMapper;
        this.lokTarConfig = lokTarConfig;
    }

    @Scheduled(cron = "0 0 10,11,12 * * MON-FRI")
    private void notice() {
        if (!lokTarConfig.env.equals(LokTarConstant.ENV_PRO)) {
            return;
        }
        System.out.println("员工合同到期监测器：" + DateTimeUtil.getDatetimeStr(LocalDateTime.now(),DateTimeUtil.FORMATTER_DATESECOND));

        List<Employee> employees = employeeMapper.getNeedNoticeEmployees();
        if (ObjectUtils.isEmpty(employees)) {
            return;
        }
        StringBuilder content = new StringBuilder().append(LokTarConstant.NOTICE_TITLE_EMPLOYEE).append(System.lineSeparator());
        for (int i = 0; i < employees.size(); i++) {
            Employee employee = employees.get(i);
            content.append(System.lineSeparator())
                    .append(employee.getName()).append(System.lineSeparator())
                    .append(System.lineSeparator())
                    .append(employee.getEndDate() + "到期").append(System.lineSeparator())
                    .append(System.lineSeparator());
            employee.setStatus(1);
            employeeMapper.updateByPrimaryKey(employee);
        }
        content = content.append(DateTimeUtil.getDatetimeStr(LocalDateTime.now(),DateTimeUtil.FORMATTER_DATEMINUTE));
        //qywxApi.sendTextMsg(new AgentMsgText(lokTarConfig.qywxNoticeZxb, lokTarConfig.qywxAgent002Id, content));
        qywxApi.sendTextMsg(new AgentMsgText(lokTarConfig.qywxNoticeCxy, lokTarConfig.qywxAgent002Id, content.toString()));
    }
}
