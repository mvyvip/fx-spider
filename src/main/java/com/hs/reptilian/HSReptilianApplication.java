package com.hs.reptilian;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@EnableAsync
@EnableScheduling
@SpringBootApplication
@MapperScan("com.hs.reptilian.mapper")
@Controller
public class HSReptilianApplication {

	public static void main(String[] args) {
		SpringApplication.run(HSReptilianApplication.class, args);
	}

	@RequestMapping({"", "/"})
	public String toOrder() {
		return "redirect:/order.html";
	}

	@RequestMapping("/test")
	public String test() {
		return "redirect:/test.html";
	}


}
