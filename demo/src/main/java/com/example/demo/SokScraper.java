package com.example.demo;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import io.github.bonigarcia.wdm.WebDriverManager;

//BURASI DÜZENLENECEK!!!!
public class SokScraper implements Runnable {

    private final String baseUrl = "https://www.sokmarket.com.tr/";

    @Override
    public void run() {
        // Selenium WebDriver'ı başlat
        WebDriverManager.chromedriver().setup(); // Doğru sürücüyü otomatik bulur ve yükler
        WebDriver driver = new ChromeDriver();
        try {
            // Sayfayı aç
            driver.get(baseUrl);

            // Bir süre bekle (JavaScript yüklemesi için)
            Thread.sleep(3000); // Gerekirse süreyi artırabilirsiniz
            // Belirtilen div altındaki tüm a etiketlerini bul
            // Belirtilen div altındaki tüm a etiketlerini bul
            List<WebElement> links = driver.findElements(By.cssSelector(".CategoryList_categories__wmXtl a"));
            List<String> hrefs = new ArrayList<>();
            for (WebElement link : links) {
                // Her bir a etiketinin href özniteliğini listeye ekle
                hrefs.add(link.getAttribute("href"));
            }
            if (!hrefs.isEmpty()) {
                // İlk 3 elemanı çıkar
                hrefs = hrefs.subList(3, hrefs.size());
                // Sonuncu elemanı çıkar
                if (!hrefs.isEmpty()) {
                    hrefs = hrefs.subList(0, hrefs.size() - 1);
                }
            }
            System.out.println(hrefs);

            for (String href : hrefs) {
                int page = 1;
                boolean hasNextPage = true;
                while (hasNextPage) {
                    String fullUrl = href + "?page=" + page;
                    driver.get(fullUrl);
                    // Burada her bir sayfa için yapılacak işlemleri ekleyebilirsiniz
                    System.out.println("Visited: " + fullUrl);
                    Thread.sleep(2000); // Her sayfa için bekleme süresi

                    // Ürün ismini ve fiyatını al
                    List<WebElement> products = driver.findElements(By.cssSelector(".CProductCard-module_infoContainer__F8uxY"));
                    if (products.isEmpty()) {
                        hasNextPage = false;
                    } else {
                        for (WebElement product : products) {
                            // Ürün ismini al
                            WebElement titleElement = product.findElement(By.cssSelector(".CProductCard-module_title__u8bMW"));
                            String productName = titleElement.getText();
                            System.out.println("Product Name: " + productName);

                            // Fiyatı al
                            WebElement priceElement;
                            String price;
                            if (product.findElements(By.cssSelector(".CPriceBox-module_discountedPrice__15Ffw")).size() > 0) {
                                priceElement = product.findElement(By.cssSelector(".CPriceBox-module_discountedPrice__15Ffw"));
                                price = "Discounted Price: " + priceElement.getText();
                            } else if (product.findElements(By.cssSelector(".CPriceBox-module_price__bYk-c")).size() > 0) {
                                priceElement = product.findElement(By.cssSelector(".CPriceBox-module_price__bYk-c"));
                                price = "Normal Price: " + priceElement.getText();
                            } else {
                                price = "Price not found";
                            }
                            System.out.println(price);
                        }
                        page++;
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
