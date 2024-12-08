package com.example.demo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.mongodb.client.MongoClients;

@Configuration
public class MongoConfig {

    @Bean
    public MongoTemplate mongoTemplate() {
        return new MongoTemplate(
                MongoClients.create("mongodb+srv://omerbyrktrdev:FsNssD6BpuFrwEhx@cluster0.nz7ot.mongodb.net/?retryWrites=true&w=majority"),
                "marketDB"
        );
    }
}
