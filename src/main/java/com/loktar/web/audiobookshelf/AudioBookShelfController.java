package com.loktar.web.audiobookshelf;


import com.loktar.conf.LokTarConfig;
import com.loktar.task.audiobookshelf.AudioBookShelfTask;
import com.loktar.util.AudioBookShelfUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("audioBookShelf")
@Slf4j
public class AudioBookShelfController {

    private final AudioBookShelfTask audioBookShelfTask;

    private final AudioBookShelfUtil audioBookShelfUtil;

    private final LokTarConfig lokTarConfig;

    public AudioBookShelfController(AudioBookShelfTask audioBookShelfTask, AudioBookShelfUtil audioBookShelfUtil, LokTarConfig lokTarConfig) {
        this.audioBookShelfTask = audioBookShelfTask;
        this.audioBookShelfUtil = audioBookShelfUtil;
        this.lokTarConfig = lokTarConfig;
    }


    @PostMapping("/testListenMonitor")
    @SneakyThrows
    public void testListenMonitor() {
        audioBookShelfTask.listenMonitor();
    }

    @PostMapping("/testResetUserActive")
    @SneakyThrows
    public void testResetUserActive() {
        audioBookShelfTask.resetUserActive();
    }

    @PostMapping("/testSwitchUserActive")
    @SneakyThrows
    public String testSwitchUserActive() {
        String monitorUsername = lokTarConfig.getAudioBookShelf().getUser();
        boolean toActive = audioBookShelfUtil.switchUserActive(monitorUsername);
        return (toActive ? "已启用" : "已禁用") + "ABS监控用户：" + monitorUsername;
    }
}
