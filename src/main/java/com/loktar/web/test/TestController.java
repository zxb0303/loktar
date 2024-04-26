package com.loktar.web.test;

import com.loktar.conf.LokTarConfig;
import com.loktar.domain.github.GithubRepository;
import com.loktar.mapper.github.GithubRepositoryMapper;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("test")
public class TestController {

    private final LokTarConfig lokTarConfig;

    private final Environment environment;

    private final GithubRepositoryMapper githubRepositoryMapper;

    public TestController(LokTarConfig lokTarConfig, Environment environment, GithubRepositoryMapper githubRepositoryMapper) {

        this.lokTarConfig = lokTarConfig;
        this.environment = environment;
        this.githubRepositoryMapper = githubRepositoryMapper;
    }

    @GetMapping("/test.do")
    public List<GithubRepository> test() {
        return githubRepositoryMapper.selectAll();
    }

}
