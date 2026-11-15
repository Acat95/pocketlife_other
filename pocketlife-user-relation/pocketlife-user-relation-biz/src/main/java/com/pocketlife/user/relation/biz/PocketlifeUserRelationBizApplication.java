package com.pocketlife.user.relation.biz;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@MapperScan("com.pocketlife.user.relation.biz.domain.mapper")
@EnableFeignClients(basePackages = "com.pocketlife")
public class PocketlifeUserRelationBizApplication {

    public static void main(String[] args) {
        SpringApplication.run(PocketlifeUserRelationBizApplication.class, args);
    }

}
