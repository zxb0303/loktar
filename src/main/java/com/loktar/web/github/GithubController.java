package com.loktar.web.github;

import com.loktar.conf.LokTarConfig;
import com.loktar.dto.wx.agentmsg.AgentMsgText;
import com.loktar.service.github.GithubService;
import com.loktar.util.wx.qywx.QywxApi;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("github")
public class GithubController {

    private final GithubService githubService;

    private final QywxApi QywxApi;

    private final LokTarConfig lokTarConfig;

    public GithubController(GithubService githubService, com.loktar.util.wx.qywx.QywxApi qywxApi, LokTarConfig lokTarConfig) {
        this.githubService = githubService;
        QywxApi = qywxApi;
        this.lokTarConfig = lokTarConfig;
    }

    @RequestMapping("/check.do")
    public void check() {
        githubService.checkRepositoryTag();
    }


    @RequestMapping("/notifyMsg.do")
    public void notifyMsg(String version) {
        QywxApi.sendTextMsg(new AgentMsgText(lokTarConfig.qywxNoticeZxb, lokTarConfig.qywxAgent002Id, version+"已经推送到镜像仓库"));
    }


}

