package com;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;



@SpringBootApplication
@EnableCaching
@EnableScheduling
@EnableAsync
//@EnableAspectJAutoProxy
public class GenFrameworkApplication {

	public static void main(String[] args) {


        SpringApplication.run(new Object[]{GenFrameworkApplication.class}, args);

		
	}
}
