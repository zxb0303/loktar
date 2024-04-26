package com.loktar.conf;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.loktar.mapper")
public class MybatisConfig {
}
