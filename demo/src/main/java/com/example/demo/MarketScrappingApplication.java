package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.example.demo.repository.A101DataRepository;
import com.example.demo.repository.MigrosDataRepository;
import com.example.demo.repository.SokDataRepository;

@SpringBootApplication
@EnableMongoRepositories
public class MarketScrappingApplication implements CommandLineRunner {

    @Autowired
    private SokDataRepository sokDataRepository;

    @Autowired
    private A101DataRepository a101DataRepository;

    @Autowired
    private MigrosDataRepository migrosDataRepository;

    public static void main(String[] args) {
        SpringApplication.run(MarketScrappingApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        // SokScraper sokScraper = new SokScraper(sokDataRepository);
        // Thread sokThread = new Thread(sokScraper);
        // sokThread.start();

        A101Scraper a101scraper = new A101Scraper(a101DataRepository);
        Thread a101Thread = new Thread(a101scraper);
        a101Thread.start();

        // MigrosScraper migrosScraper = new MigrosScraper(migrosDataRepository);
        // Thread migrosThread = new Thread(migrosScraper);
        // migrosThread.start();
    }
}
