package com.tmap.mit.parser;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ParserBatchApplication {
    public static void main(String[] args) {
        SpringApplication.run(ParserBatchApplication.class, args);
    }

}
