package com.pocketlife.note.biz;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@MapperScan("com.pocketlife.note.biz.domain.mapper")
@EnableFeignClients(basePackages = "com.pocketlife")
public class PocketlifeNoteBizApplication {

    public static void main(String[] args) {
        SpringApplication.run(PocketlifeNoteBizApplication.class, args);
    }

}
