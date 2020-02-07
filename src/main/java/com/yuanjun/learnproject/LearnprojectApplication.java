package com.yuanjun.learnproject;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.yuanjun.learnproject.dao")
public class LearnprojectApplication {

    public static void main(String[] args) {
        SpringApplication.run(LearnprojectApplication.class, args);
    }

}
