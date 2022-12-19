package com.gescof.springbootpostgrecompose;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class SpringBootPostgreComposeApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootPostgreComposeApplication.class, args);
    }

}
