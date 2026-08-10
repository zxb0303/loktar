package com.loktar.web.investment;


import com.loktar.task.investment.ChinaEquityIndexPerfTask;
import com.loktar.util.DateTimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("chinaEquityIndexPerf")
@Slf4j
public class ChinaEquityIndexPerfController {
    private final ChinaEquityIndexPerfTask chinaEquityIndexPerfTask;

    public ChinaEquityIndexPerfController(ChinaEquityIndexPerfTask chinaEquityIndexPerfTask) {
        this.chinaEquityIndexPerfTask = chinaEquityIndexPerfTask;
    }

    @PostMapping("/testSync")
    public void testSync() {
        chinaEquityIndexPerfTask.syncToday();
    }

    @PostMapping("/initHistory")
    public void initHistory(@RequestParam String startDate, @RequestParam String endDate) {
        chinaEquityIndexPerfTask.initHistory(LocalDate.parse(startDate, DateTimeUtil.FORMATTER_DATE_COMPACT), LocalDate.parse(endDate, DateTimeUtil.FORMATTER_DATE_COMPACT));
    }
}
