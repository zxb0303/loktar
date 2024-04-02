package com.loktar.task.github;


import com.loktar.service.github.GithubService;
import com.loktar.util.DateUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
public class GithubTask {
    @Value("${spring.profiles.active}")
    private String env;
    private final GithubService githubService;

    public GithubTask(GithubService githubService) {
        this.githubService = githubService;
    }


    @Scheduled(cron = "0 */30 * * * ?")
    private void notice() {
        if (!env.equals("pro")) {
            return;
        }
        System.out.println("GITHUB项目检测定时器：" + DateUtil.getTodayToSecond());
        githubService.checkRepositoryTag();
    }
}
