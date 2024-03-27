package com.loktar.task.cxy;


import com.loktar.conf.LokTarConstant;
import com.loktar.conf.LokTarPrivateConstant;
import com.loktar.domain.cxy.Employee;
import com.loktar.dto.wx.agentmsg.AgentMsgText;
import com.loktar.mapper.cxy.EmployeeMapper;
import com.loktar.util.DateUtil;
import com.loktar.util.wx.qywx.QywxApi;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;

@Configuration
@EnableScheduling
public class EmployeeTask {
    @Value("${spring.profiles.active}")
    private String env;
    private final QywxApi qywxApi;
    private final EmployeeMapper employeeMapper;

    public EmployeeTask(QywxApi qywxApi, EmployeeMapper employeeMapper) {
        this.qywxApi = qywxApi;
        this.employeeMapper = employeeMapper;
    }

    @Scheduled(cron = "0 0 10,11,12 * * MON-FRI")
    private void notice() {
        if (!env.equals("pro")) {
            return;
        }
        System.out.println("员工合同到期监测器：" + DateUtil.getTodayToSecond());

        List<Employee> employees = employeeMapper.getNeedNoticeEmployees();
        if (ObjectUtils.isEmpty(employees)) {
            return;
        }
        String content = LokTarConstant.NOTICE_TITLE_EMPLOYEE + "\n\n";
        for (int i = 0; i < employees.size(); i++) {
            Employee employee = employees.get(i);
            content = content + employee.getName()
                    + "\n"
                    + employee.getEndDate() + "到期"
                    + "\n\n";
            employee.setStatus(1);
            employeeMapper.updateByPrimaryKey(employee);
        }
        content = content + DateUtil.getMinuteSysDate();
        //qywxApi.sendTextMsg(new AgentMsgText(LokTarPrivateConstant.NOTICE_ZXB, LokTarPrivateConstant.AGENT002ID, content));
        qywxApi.sendTextMsg(new AgentMsgText(LokTarPrivateConstant.NOTICE_CXY, LokTarPrivateConstant.AGENT002ID, content));
    }
}
