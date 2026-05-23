package org.example.hr_system;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication

public class HrSystemApplication {
    public static void main(String[] args) {
        SpringApplication.run(HrSystemApplication.class, args);
    }
}