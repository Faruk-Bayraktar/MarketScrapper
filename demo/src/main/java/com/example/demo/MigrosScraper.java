package com.example.demo;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import io.github.bonigarcia.wdm.WebDriverManager;

public class MigrosScraper implements Runnable {

    private final String baseUrl = "https://www.migros.com.tr/hemen/";

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

            // Kategorileri içeren elemanları seç
            List<WebElement> categoryElements = driver.findElements(By.cssSelector("div.category-wrapper.ng-star-inserted"));

            // ID'lerin son kısımlarını saklamak için bir liste oluştur
            List<String> idSuffixes = new ArrayList<>();

            for (WebElement categoryElement : categoryElements) {
                // Altındaki a etiketlerini seç
                List<WebElement> linkElements = categoryElement.findElements(By.cssSelector("a[id^='home-page-category-card-']"));

                for (WebElement linkElement : linkElements) {
                    // ID değerini al
                    String idValue = linkElement.getAttribute("id");

                    // ID değerinin son kısmını ve bir önceki kelimeyi ayıkla
                    String idSuffix = idValue.substring(idValue.lastIndexOf("card-") + 5);

                    // Listeye ekle
                    idSuffixes.add(idSuffix);
                }
            }

            // İlk elemanı atla
            if (!idSuffixes.isEmpty()) {
                idSuffixes.remove(0);
            }

            // Her bir idSuffix için işlemleri gerçekleştir
            for (String idSuffix : idSuffixes) {
                int pageNumber = 1;
                boolean hasMorePages = true;

                while (hasMorePages) {
                    String categoryUrl = baseUrl + idSuffix + "?sayfa=" + pageNumber + "&sirala=onerilenler";
                    driver.get(categoryUrl);

                    // Bir süre bekle (JavaScript yüklemesi için)
                    Thread.sleep(5000); // Gerekirse süreyi artırabilirsiniz

                    // Ürün isimlerini içeren elemanları seç
                    List<WebElement> productElements = driver.findElements(By.cssSelector("a.mat-caption.text-color-black.product-name"));

                    if (productElements.isEmpty()) {
                        hasMorePages = false;
                    } else {
                        // Ürün isimlerini yazdır
                        for (WebElement productElement : productElements) {
                            System.out.println(idSuffix + " : " + productElement.getText());
                        }
                        pageNumber++;
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Tarayıcıyı kapat
            driver.quit();
        }
    }

}
