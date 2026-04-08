package com.quizzplatform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.quizzplatform.auth", "com.quizzplatform.quizz", "com.quizzplatform.ia", "com.quizzplatform.translation", "com.quizzplatform.config", "com.quizzplatform.util"})
public class QuizzBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(QuizzBackendApplication.class, args);
    }
}