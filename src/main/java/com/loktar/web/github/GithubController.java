package com.loktar.web.github;

import com.loktar.service.github.GithubService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("github")
public class GithubController {

    private final GithubService githubService;

    public GithubController(GithubService githubService) {
        this.githubService = githubService;
    }

    @RequestMapping("/check.do")
    public void check(){
        githubService.checkRepositoryTag();
    }


}

