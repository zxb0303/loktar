package com.loktar.web.redis;


import lombok.extern.slf4j.Slf4j;
import com.loktar.dto.cxy.RestInfo;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.concurrent.TimeUnit;


@RestController
@RequestMapping("redis")
@Slf4j
public class RedisController {
    private final RedisTemplate<String, Object> redisTemplate;

    public RedisController(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }


    @GetMapping("save")
    public void save(){
        RestInfo restInfo = new RestInfo();
        restInfo.setEligibleDays(1);
        restInfo.setName("zhangsan");
        redisTemplate.opsForValue().set("2", restInfo, Duration.ofSeconds(5));
    }
    @GetMapping("get")
    public void get(){
        Object obj = redisTemplate.opsForValue().get("qywx_accessToken_1000002");
        log.info("{}", obj.toString());
    }

    @GetMapping("set1")
    public void set1(){
        redisTemplate.opsForSet().add("111", "111");
        redisTemplate.expire("111", 10, TimeUnit.SECONDS);
    }

    @GetMapping("set2")
    public void set2(){
        redisTemplate.opsForSet().add("111", "222");
        redisTemplate.expire("111", 100, TimeUnit.SECONDS);
    }
    @GetMapping("getSet")
    public void getSet(){
        log.info("{}", redisTemplate.opsForSet().size("111"));
    }


}
