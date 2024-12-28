package com.example.demo;

import java.util.concurrent.CountDownLatch;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories; //MongoDB repositorylerini kullanabilmek için gerekli olan anotasyonlar.

import com.example.demo.repository.A101DataRepository;
import com.example.demo.repository.MigrosDataRepository;
import com.example.demo.repository.SokDataRepository;

@SpringBootApplication
@EnableMongoRepositories
public class MarketScrappingApplication implements CommandLineRunner {

    @Autowired //Spring tarafından otomatik olarak oluşturulan bir bean'i enjekte etmek için kullanılır.
    private SokDataRepository sokDataRepository;

    @Autowired
    private A101DataRepository a101DataRepository;

    @Autowired
    private MigrosDataRepository migrosDataRepository;

    private static ConfigurableApplicationContext context;

    public static void main(String[] args) {
        context = SpringApplication.run(MarketScrappingApplication.class, args);//Spring boot, Spring uygulamalarini hizlandirmak ve basitlestirmek icin kullanilir.
    }

    @Override
    public void run(String... args) throws Exception {
        CountDownLatch latch = new CountDownLatch(3);

        try {

            // SokScraper sokScraper = new SokScraper(sokDataRepository, latch); //sok scraper sokDataRepository ve latch parametreleri ile oluşturuldu.latch sayesinde 3 threadin işlemi bitene kadar beklenir.
            // Thread sokThread = new Thread(sokScraper); //sokScraper thread oluşturuldu.sokScraper arguman olarak verildi cunku sokScraper Runnable interface implemente ediyor.
            // sokThread.start();
            // A101Scraper a101scraper = new A101Scraper(a101DataRepository, latch);
            // Thread a101Thread = new Thread(a101scraper);
            // a101Thread.start();
            MigrosScraper migrosScraper = new MigrosScraper(migrosDataRepository, latch);
            Thread migrosThread = new Thread(migrosScraper);
            migrosThread.start();

            latch.await();
        } finally {

            if (context != null) {
                context.close();
            }

        }
    }
}
