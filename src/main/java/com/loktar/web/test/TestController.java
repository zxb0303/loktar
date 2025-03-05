package com.loktar.web.test;

import com.loktar.conf.LokTarConfig;
import com.loktar.mapper.github.GithubRepositoryMapper;
import com.loktar.util.IPUtil;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("test")
public class TestController {

    private final LokTarConfig lokTarConfig;

    private final Environment environment;

    private final GithubRepositoryMapper githubRepositoryMapper;

    private final IPUtil ipUtil;

    public TestController(LokTarConfig lokTarConfig, Environment environment, GithubRepositoryMapper githubRepositoryMapper, IPUtil ipUtil) {

        this.lokTarConfig = lokTarConfig;
        this.environment = environment;
        this.githubRepositoryMapper = githubRepositoryMapper;
        this.ipUtil = ipUtil;
    }

    @GetMapping("/test.do")
    public void test() {
        System.out.println(ipUtil.getip());
    }

}
