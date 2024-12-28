package com.example.demo;

import org.springframework.context.annotation.Bean; //Bean oluşturmak için gerekli olan anotasyon.
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.mongodb.client.MongoClients;

import io.github.cdimascio.dotenv.Dotenv;

@Configuration
public class MongoConfig {

    @Bean
    public MongoTemplate mongoTemplate() {
        Dotenv dotenv = Dotenv.load();
        String Uri = dotenv.get("MONGO_URI");
        
        return new MongoTemplate(
                MongoClients.create(Uri),
                "marketDB" //MongoDB de kullanılacak olan veritabanı adı.
        );
    }
}
