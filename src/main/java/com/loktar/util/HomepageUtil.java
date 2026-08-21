package com.loktar.util;

import com.loktar.conf.LokTarConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * homepage仪表盘配置文件工具，负责按Jellyfin播放状态切换services.yaml中widget的展示模式
 */
@Slf4j
@Component
public class HomepageUtil {

    private static final Pattern ENABLE_BLOCKS_PATTERN = Pattern.compile("(?m)^(\\s*enableBlocks:\\s*)(true|false)(.*)$");
    private static final Pattern ENABLE_NOW_PLAYING_PATTERN = Pattern.compile("(?m)^(\\s*enableNowPlaying:\\s*)(true|false)(.*)$");

    private final LokTarConfig lokTarConfig;

    public HomepageUtil(LokTarConfig lokTarConfig) {
        this.lokTarConfig = lokTarConfig;
    }

    /**
     * 切换services.yaml中Jellyfin widget的展示模式：
     * 播放中显示NowPlaying（enableBlocks=false、enableNowPlaying=true），
     * 未播放显示Blocks（enableBlocks=true、enableNowPlaying=false），
     * 仅正则替换两个配置项的值，保留文件中的注释和原有排版
     */
    public synchronized void switchJellyfinWidgetMode(boolean playing) {
        String yamlPath = Optional.ofNullable(lokTarConfig.getHomepage())
                .map(LokTarConfig.Homepage::getServices)
                .map(LokTarConfig.Homepage.Services::getYaml)
                .orElse(null);
        if (yamlPath == null || yamlPath.isBlank()) {
            log.error("{}", "homepage services.yaml路径未配置，跳过Jellyfin widget模式切换");
            return;
        }
        Path path = Path.of(yamlPath);
        if (!Files.exists(path)) {
            log.error("{}", "homepage services.yaml文件不存在：" + yamlPath);
            return;
        }
        try {
            String content = Files.readString(path, StandardCharsets.UTF_8);
            String newContent = ENABLE_BLOCKS_PATTERN.matcher(content).replaceAll("$1" + !playing + "$3");
            newContent = ENABLE_NOW_PLAYING_PATTERN.matcher(newContent).replaceAll("$1" + playing + "$3");
            if (newContent.equals(content)) {
                return;
            }
            Files.writeString(path, newContent, StandardCharsets.UTF_8);
            log.info("{}", "homepage Jellyfin widget已切换为" + (playing ? "NowPlaying" : "Blocks") + "模式");
        } catch (IOException e) {
            log.error("homepage services.yaml切换失败", e);
        }
    }
}
