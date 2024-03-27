package com.loktar;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.loktar.mapper")
@SpringBootApplication
public class LoktarApplication {

	public static void main(String[] args) {
		SpringApplication.run(LoktarApplication.class, args);
	}

}
