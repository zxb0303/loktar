package com.loktar.web.investment;


import com.loktar.task.investment.ChinaEquityIndexTask;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("chinaEquityIndex")
@Slf4j
public class ChinaEquityIndexController {
    private final ChinaEquityIndexTask chinaEquityIndexTask;

    public ChinaEquityIndexController(ChinaEquityIndexTask chinaEquityIndexTask) {
        this.chinaEquityIndexTask = chinaEquityIndexTask;
    }

    @PostMapping("/testGatData")
    @SneakyThrows
    public void getData() {
        chinaEquityIndexTask.getData();
    }

}
