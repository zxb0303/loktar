package com.loktar.conf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

@Configuration
@EnableScheduling
public class SchedulerConfig implements SchedulingConfigurer {

    @Bean(destroyMethod = "shutdown")
    public ThreadPoolTaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(8);                       // 同时可并行运行的任务数
        scheduler.setThreadNamePrefix("scheduled-");    // 线程名，方便看日志/线程栈
        scheduler.setWaitForTasksToCompleteOnShutdown(true);
        scheduler.setAwaitTerminationSeconds(30);       // 关闭时最多等 30s
        scheduler.setRemoveOnCancelPolicy(true);        // 取消的任务从队列移除，避免内存堆积
        scheduler.setErrorHandler(t -> 
            org.slf4j.LoggerFactory.getLogger("ScheduledError")
                .error("scheduled task error", t));     // 全局兜底，避免异常吞掉后续触发
        scheduler.initialize();
        return scheduler;
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar registrar) {
        registrar.setTaskScheduler(taskScheduler());
    }
}
