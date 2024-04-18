package com.loktar.task.github;


import com.loktar.conf.LokTarConfig;
import com.loktar.conf.LokTarConstant;
import com.loktar.service.github.GithubService;
import com.loktar.util.DateTimeUtil;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@EnableScheduling
public class GithubTask {

    private final GithubService githubService;

    private final LokTarConfig lokTarConfig;

    public GithubTask(GithubService githubService, LokTarConfig lokTarConfig) {
        this.githubService = githubService;
        this.lokTarConfig = lokTarConfig;
    }


    @Scheduled(cron = "0 */20 * * * ?")
    private void notice() {
        if (!lokTarConfig.env.equals(LokTarConstant.ENV_PRO)) {
            return;
        }
        System.out.println("GITHUB项目检测定时器：" + DateTimeUtil.getDatetimeStr(LocalDateTime.now(),DateTimeUtil.FORMATTER_DATESECOND));
        githubService.checkRepositoryTag();
    }
}
