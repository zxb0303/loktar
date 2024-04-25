package com.loktar.task.github;


import com.loktar.conf.LokTarConstant;
import com.loktar.service.github.GithubService;
import com.loktar.util.DateTimeUtil;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@EnableScheduling
@Profile(LokTarConstant.ENV_PRO)
public class GithubTask {

    private final GithubService githubService;

    public GithubTask(GithubService githubService) {
        this.githubService = githubService;
    }


    @Scheduled(cron = "0 */20 * * * ?")
    private void notice() {
        System.out.println("GITHUB项目检测定时器：" + DateTimeUtil.getDatetimeStr(LocalDateTime.now(),DateTimeUtil.FORMATTER_DATESECOND));
        githubService.checkRepositoryTag();
    }
}
