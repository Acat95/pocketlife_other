package com.pocketlife.user.biz;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@MapperScan("com.pocketlife.user.biz.domain.mapper")
@EnableFeignClients(basePackages = "com.pocketlife")
@ComponentScan({"com.pocketlife.user.biz", "com.pocketlife.count"}) //  多模块扫描
public class PocketlifeUserBizApplication {

    public static void main(String[] args) {
        SpringApplication.run(PocketlifeUserBizApplication.class, args);
    }

}
