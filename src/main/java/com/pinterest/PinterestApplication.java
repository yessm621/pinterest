package com.pinterest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
public class PinterestApplication {

    public static void main(String[] args) {
        SpringApplication.run(PinterestApplication.class, args);
    }
}
