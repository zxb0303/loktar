package com.loktar.task.github;



import lombok.extern.slf4j.Slf4j;
import com.loktar.service.github.GithubService;
import com.loktar.util.DateTimeUtil;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Slf4j
public class GithubTask {

    private final GithubService githubService;

    public GithubTask(GithubService githubService) {
        this.githubService = githubService;
    }


    @Scheduled(cron = "0 */20 * * * ?")
    public void notice() {
        log.info("{}", "Github定时器：" + DateTimeUtil.getDatetimeStr(LocalDateTime.now(),DateTimeUtil.FORMATTER_DATESECOND));
        githubService.checkRepositoryTag();
    }
}
