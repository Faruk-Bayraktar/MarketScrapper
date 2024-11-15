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
        // SokScraper sokScraper = new SokScraper(productRepository);
        // Thread sokThread = new Thread(sokScraper);
        // sokThread.start();  
        // System.out.println("Sok Scraper started");


        A101Scraper a101scraper= new A101Scraper(productRepository);
        Thread a101Thread = new Thread(a101scraper);
        a101Thread.start();  
        System.out.println("a101 Scraper started");


        // MigrosScraper migrosScraper = new MigrosScraper(productRepository);
        // Thread migrosThread = new Thread(migrosScraper);
        // migrosThread.start();
        // System.out.println("Mgiros Scraper started");
    }
}