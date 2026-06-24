package com.loktar.web.redis;


import lombok.extern.slf4j.Slf4j;
import com.loktar.dto.cxy.RestInfo;
import com.loktar.util.RedisUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("redis")
@Slf4j
public class RedisController {
    private final RedisUtil redisUtil;

    public RedisController(RedisUtil redisUtil) {
        this.redisUtil = redisUtil;
    }


    @GetMapping("save")
    public void save(){
        //redisUtil.set("1","222");
        RestInfo restInfo = new RestInfo();
        restInfo.setEligibleDays(1);
        restInfo.setName("zhangsan");
        redisUtil.set("2",restInfo,5);
    }
    @GetMapping("get")
    public void get(){
        Object obj = redisUtil.get("qywx_accessToken_1000002");
        log.info("{}", obj.toString());
    }

    @GetMapping("set1")
    public void set1(){
        redisUtil.sSetAndTime("111",10,"111");
    }

    @GetMapping("set2")
    public void set2(){
        redisUtil.sSetAndTime("111",100,"222");
    }
    @GetMapping("getSet")
    public void getSet(){
        log.info("{}", redisUtil.sGetSetSize("111"));
    }


}
