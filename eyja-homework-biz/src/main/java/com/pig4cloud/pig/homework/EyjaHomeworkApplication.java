package com.pig4cloud.pig.homework;

import com.pig4cloud.pig.common.security.annotation.EnablePigResourceServer;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 作业督学微服务启动类
 */
@EnablePigResourceServer
@EnableDiscoveryClient
@MapperScan("com.pig4cloud.pig.homework.mapper")
@SpringBootApplication
public class EyjaHomeworkApplication {
	public static void main(String[] args) {
		SpringApplication.run(EyjaHomeworkApplication.class, args);
	}
}