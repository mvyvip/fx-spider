package com.hs.reptilian;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableAsync
@EnableScheduling
@SpringBootApplication
@MapperScan("com.hs.reptilian.mapper")
public class HSReptilianApplication {

	public static void main(String[] args) {
		SpringApplication.run(HSReptilianApplication.class, args);
	}

}
