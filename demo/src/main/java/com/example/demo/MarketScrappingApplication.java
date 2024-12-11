package com.example.demo;

import java.util.concurrent.CountDownLatch;

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
    // private static ConfigurableApplicationContext context;

    public static void main(String[] args) {
        SpringApplication.run(MarketScrappingApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        CountDownLatch latch = new CountDownLatch(1); // 3 thread için latch

        try {
            SokScraper sokScraper = new SokScraper(sokDataRepository);
            Thread sokThread = new Thread(sokScraper);
            sokThread.setDaemon(true); // Sok thread'i daemon olarak ayarlandı
            sokThread.start();

            // A101Scraper a101scraper = new A101Scraper(a101DataRepository, latch);
            // Thread a101Thread = new Thread(a101scraper);
            // a101Thread.setDaemon(true); // A101 thread'i daemon olarak ayarlandı
            // a101Thread.start();
            // MigrosScraper migrosScraper = new MigrosScraper(migrosDataRepository, latch);
            // Thread migrosThread = new Thread(migrosScraper);
            // migrosThread.setDaemon(true); // Migros thread'i daemon olarak ayarlandı
            // migrosThread.start();
            // Thread'lerin tamamlanmasını bekle
            sokThread.join();
            // a101Thread.join();
            // migrosThread.join();
        } finally {
            // Uygulamayı güvenli bir şekilde kapat
            // if (context != null) {
            //     context.close();
            // }
            System.exit(0);
        }
    }
}
