package com.personal.bookmark_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BookmarkBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookmarkBackendApplication.class, args);
    }

}
