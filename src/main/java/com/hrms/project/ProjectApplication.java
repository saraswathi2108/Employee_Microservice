package com.hrms.project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.hrms.project.client")
public class ProjectApplication {

	public static void main(String[] args) {

		SpringApplication.run(ProjectApplication.class, args);
	}

}
