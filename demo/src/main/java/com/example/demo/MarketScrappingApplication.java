package com.example.demo;  //Bu proje için oluşturulan paket adı

import org.springframework.beans.factory.annotation.Autowired; //Spring Framework'ün otomatik olarak bağlamaları yapabilmesi için kullanılan anotasyon
import org.springframework.boot.CommandLineRunner; //Spring Boot uygulamalarının başlatılmasını sağlayan arayüz
import org.springframework.boot.SpringApplication; //Spring Boot uygulamalarının başlatılmasını sağlayan sınıf
import org.springframework.boot.autoconfigure.SpringBootApplication; //Spring Boot uygulamalarının otomatik yapılandırılmasını sağlayan anotasyon
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories; //MongoDB veritabanı işlemleri için kullanılan anotasyon

import com.example.demo.repository.A101DataRepository; //A101 verilerinin veritabanına kaydedilmesi için kullanılan repository sınıfı
import com.example.demo.repository.MigrosDataRepository; //Migros verilerinin veritabanına kaydedilmesi için kullanılan repository sınıfı
import com.example.demo.repository.SokDataRepository; //Şok verilerinin veritabanına kaydedilmesi için kullanılan repository sınıfı

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
        SokScraper sokScraper = new SokScraper(sokDataRepository);
        Thread sokThread = new Thread(sokScraper);
        sokThread.start();

        A101Scraper a101scraper = new A101Scraper(a101DataRepository);
        Thread a101Thread = new Thread(a101scraper);
        a101Thread.start();

        MigrosScraper migrosScraper = new MigrosScraper(migrosDataRepository);
        Thread migrosThread = new Thread(migrosScraper);
        migrosThread.start();

        sokThread.join();
        a101Thread.join();
        migrosThread.join();
    }
}
