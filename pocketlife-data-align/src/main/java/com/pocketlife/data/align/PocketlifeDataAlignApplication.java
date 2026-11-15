package com.pocketlife.data.align;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@MapperScan("com.pocketlife.data.align.domain.mapper")
@EnableFeignClients(basePackages = "com.pocketlife")
public class PocketlifeDataAlignApplication {

    public static void main(String[] args) {
        SpringApplication.run(PocketlifeDataAlignApplication.class, args);
    }

}
