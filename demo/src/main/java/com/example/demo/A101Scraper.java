package com.example.demo;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.demo.repository.A101DataRepository;

import io.github.bonigarcia.wdm.WebDriverManager;

public class A101Scraper implements Runnable {

    private final A101DataRepository a101DataRepository;

    @Autowired
    public A101Scraper(A101DataRepository a101DataRepository) {
        this.a101DataRepository = a101DataRepository;
    }

    private final String baseUrl = "https://www.a101.com.tr/kapida"; // Burayı kendi sitenizin URL'siyle değiştirin

    @Override
    public void run() {
        WebDriverManager.chromedriver().setup(); // Doğru sürücüyü otomatik bulur ve yükler
        WebDriver driver = new ChromeDriver();
        try {
            driver.get(baseUrl);
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
            }
            // İlk elemanı atla
            if (!hrefList.isEmpty()) {
                hrefList = hrefList.subList(4, hrefList.size());
            }

            for (String href : hrefList) {
                driver.get(href);
                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

                List<WebElement> productContainers = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.cssSelector("div.h-\\[96px\\].flex.pt-1.flex-col")));

                // Sadece belirli sınıf kombinasyonuna sahip elemanları filtrele
                List<WebElement> filteredProductContainers = new ArrayList<>();
                for (WebElement productContainer : productContainers) {
                    String classAttribute = productContainer.getAttribute("class");
                    if (classAttribute.equals("h-[96px] flex pt-1 flex-col")) {
                        filteredProductContainers.add(productContainer);
                    }
                }
                for (WebElement productContainer : filteredProductContainers) {
                    // Ürün ismini çek
                    // Ürün ismini çek
                    WebElement productNameElement = productContainer.findElement(By.cssSelector("div.mobile\\:text-xs.tablet\\:text-xs.line-clamp-3.h-12.font-medium.overflow-hidden.mb-\\[10px\\]"));
                    String productName = productNameElement.getText();
                    WebElement priceContainer = productContainer.findElement(By.cssSelector("div.h-\\[39px\\].w-full.relative"));
                    String price;
                    boolean discount = false;
                    if (!priceContainer.findElements(By.cssSelector("div.text-md.absolute.bottom-0.font-medium.tablet\\:text-base.text-\\[\\#EA242A\\]")).isEmpty()) {
                        price = priceContainer.findElement(By.cssSelector("div.text-md.absolute.bottom-0.font-medium.tablet\\:text-base.text-\\[\\#EA242A\\]")).getText();
                        discount = true;
                        System.out.println("Ürün İsmi: " + productName + " - İndirimli Fiyat: " + price);
                    } else if (!priceContainer.findElements(By.cssSelector("div.text-md.absolute.bottom-0.font-medium.tablet\\:text-base.text-\\[\\#333\\]")).isEmpty()) {
                        price = priceContainer.findElement(By.cssSelector("div.text-md.absolute.bottom-0.font-medium.tablet\\:text-base.text-\\[\\#333\\]")).getText();
                        System.out.println("Ürün İsmi: " + productName + " - Normal Fiyat: " + price);
                    } else {
                        System.out.println("Ürün İsmi: " + productName + " - Fiyat bulunamadı");
                        continue;
                    }

                    String productId = productName.toLowerCase().replaceAll("\\s+", "-");

                    Optional<A101Product> existingProductOpt = a101DataRepository.findById(productId);
                    if (existingProductOpt.isPresent()) {
                        A101Product existingProduct = existingProductOpt.get();
                        existingProduct.setPrice(price);
                        existingProduct.setDiscount(discount);
                        a101DataRepository.save(existingProduct);
                        System.out.println("Updating product: " + existingProduct);
                    } else {
                        A101Product product = new A101Product(productId, productName, price, discount);
                        a101DataRepository.save(product);
                        System.out.println("Saving new product: " + product);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            driver.quit();
        }
    }
}
