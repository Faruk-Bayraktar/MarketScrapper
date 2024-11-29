package com.example.demo;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import io.github.bonigarcia.wdm.WebDriverManager;

public class A101Scraper implements Runnable {

    private final String baseUrl = "https://www.a101.com.tr/kapida"; // Burayı kendi sitenizin URL'siyle değiştirin

    @Override
    public void run() {
        // Selenium WebDriver'ı başlat
        WebDriverManager.chromedriver().setup(); // Doğru sürücüyü otomatik bulur ve yükler
        WebDriver driver = new ChromeDriver();
        try {
            // Sayfayı aç
            driver.get(baseUrl);

            // Bir süre bekle (JavaScript yüklemesi için)
            Thread.sleep(5000); // Gerekirse süreyi artırabilirsiniz

            // Tüm <a class="block w-full"> elemanlarını seç
            List<WebElement> linkElements = driver.findElements(By.cssSelector("a.block.w-full"));

            // Href değerlerini saklamak için bir liste oluştur
            List<String> hrefList = new ArrayList<>();

            for (WebElement linkElement : linkElements) {
                // href değerini al
                String hrefValue = linkElement.getAttribute("href");

                // Listeye ekle
                hrefList.add(hrefValue);
                System.out.println(hrefValue); // Konsola yazdır
            }

            // Href değerlerini işlem yapacağınız şekilde kullanabilirsiniz
            System.out.println("Toplam href sayısı: " + hrefList.size());

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Tarayıcıyı kapat
            driver.quit();
        }
    }

}
