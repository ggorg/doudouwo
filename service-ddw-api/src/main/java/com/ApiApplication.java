package com;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import springfox.documentation.swagger2.annotations.EnableSwagger2;


@SpringBootApplication
@EnableCaching
@EnableScheduling
//@EnableAspectJAutoProxy
@EnableSwagger2
@MapperScan({"com.gen.common.dao","com.*.dao"})
@ServletComponentScan

public class ApiApplication {

	public static void main(String[] args) {


        SpringApplication.run(new Object[]{ApiApplication.class}, args);

		
	}
}
