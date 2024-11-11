package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MarketScrappingApplication implements CommandLineRunner {

    @Autowired
    private ProductRepository productRepository;

    public static void main(String[] args) {
        SpringApplication.run(MarketScrappingApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        String sokUrl = "https://www.sokmarket.com.tr/meyve-ve-sebze-c-20";
        MigrosScraper migrosScraper = new MigrosScraper(sokUrl, productRepository);
        Thread migrosThread = new Thread(migrosScraper);
        migrosThread.start();
        System.out.println("Scraper started");
    }
}