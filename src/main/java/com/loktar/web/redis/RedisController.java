package com.loktar.web.redis;

import com.loktar.dto.cxy.RestInfo;
import com.loktar.util.RedisUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("redis")
public class RedisController {
    private final RedisUtil redisUtil;

    public RedisController(RedisUtil redisUtil) {
        this.redisUtil = redisUtil;
    }


    @GetMapping("save")
    public void save(){
        redisUtil.set("1","222");
        RestInfo restInfo = new RestInfo();
        restInfo.setEligibleDays(1);
        restInfo.setName("zhangsan");
        redisUtil.set("2",restInfo);
    }
    @GetMapping("get")
    public void get(){
        String str1 = redisUtil.get("1").toString();
        System.out.println(str1);

        RestInfo restInfo = (RestInfo) redisUtil.get("2");

        System.out.println(restInfo.toString());

        Object obj = redisUtil.get("qywx_accessToken_1000002");
        System.out.println(obj.toString());
    }


}
