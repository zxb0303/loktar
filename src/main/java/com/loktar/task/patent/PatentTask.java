package com.loktar.task.patent;

import com.loktar.conf.LokTarConstant;
import com.loktar.domain.patent.Patent;
import com.loktar.mapper.patent.PatentMapper;
import com.loktar.util.PatentUtil;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@EnableScheduling
@Profile(LokTarConstant.ENV_PRO)
public class PatentTask {
    private final PatentMapper patentMapper;

    public PatentTask(PatentMapper patentMapper) {
        this.patentMapper = patentMapper;
    }

    @Scheduled(cron = "0 */1 * * * ?")
    public void dealPatent() {
        int status = 1;
        List<Patent> patents = patentMapper.selectByStatus(status);
        for (Patent patent : patents) {
            patent = PatentUtil.dealPatent(patent);
            patent.setStatus(2);
            patentMapper.updateByPrimaryKey(patent);
        }
    }
}
