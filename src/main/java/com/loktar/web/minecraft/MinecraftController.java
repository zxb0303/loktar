package com.loktar.web.minecraft;


import com.loktar.task.minecraft.MinecraftTask;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("minecraft")
@Slf4j
public class MinecraftController {

    private final MinecraftTask minecraftTask;

    public MinecraftController(MinecraftTask minecraftTask) {
        this.minecraftTask = minecraftTask;
    }


    @PostMapping("/testCheckVersion")
    @SneakyThrows
    public void testCheckVersion() {
        minecraftTask.checkVersion();
    }
}
