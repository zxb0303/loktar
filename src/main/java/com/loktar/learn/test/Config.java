package com.loktar.learn.test;


import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class Config {
    @Value("${spring.profiles.active}")
    public String ip;


}
