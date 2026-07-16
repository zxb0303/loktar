package com.loktar.web.investment;


import com.loktar.task.investment.FundNavTask;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("fundNav")
@Slf4j
public class FundNavController {

    private final FundNavTask fundNavTask;

    public FundNavController(FundNavTask fundNavTask) {
        this.fundNavTask = fundNavTask;
    }


    @PostMapping("/testSync")
    @SneakyThrows
    public void testSync() {
        fundNavTask.syncToday();
    }
}
