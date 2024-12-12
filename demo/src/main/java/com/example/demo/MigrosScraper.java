package com.example.demo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.demo.repository.MigrosDataRepository;

import io.github.bonigarcia.wdm.WebDriverManager;

public class MigrosScraper implements Runnable {

    private final MigrosDataRepository migrosDataRepository;
    private final CountDownLatch latch;

    @Autowired
    public MigrosScraper(MigrosDataRepository migrosDataRepository, CountDownLatch latch) {
        this.migrosDataRepository = migrosDataRepository;
        this.latch = latch;
    }

    private final String baseUrl = "https://www.migros.com.tr/hemen/";

    @Override
    public void run() {
        WebDriverManager.chromedriver().setup();
        WebDriver driver = new ChromeDriver();
        try {
            driver.get(baseUrl);
            Thread.sleep(5000);

            List<WebElement> categoryElements = driver.findElements(By.cssSelector("div.category-wrapper.ng-star-inserted"));

            List<String> idSuffixes = new ArrayList<>();

            for (WebElement categoryElement : categoryElements) {
                List<WebElement> linkElements = categoryElement.findElements(By.cssSelector("a[id^='home-page-category-card-']"));

                for (WebElement linkElement : linkElements) {
                    String idValue = linkElement.getAttribute("id");

                    String idSuffix = idValue.substring(idValue.lastIndexOf("card-") + 5);

                    idSuffixes.add(idSuffix);
                }
            }
            if (!idSuffixes.isEmpty()) {
                idSuffixes.remove(0);
            }
            for (String idSuffix : idSuffixes) {
                int pageNumber = 1;
                boolean hasMorePages = true;
                while (hasMorePages) {
                    String categoryUrl = baseUrl + idSuffix + "?sayfa=" + pageNumber + "&sirala=onerilenler";
                    driver.get(categoryUrl);

                    Thread.sleep(5000);
                    List<WebElement> productElements = driver.findElements(By.cssSelector("a.mat-caption.text-color-black.product-name"));
                    List<WebElement> priceElements = driver.findElements(By.cssSelector("div.price.subtitle-1.ng-star-inserted"));

                    if (productElements.isEmpty()) {
                        hasMorePages = false;
                    } else {
                        for (int i = 0; i < productElements.size(); i++) {
                            String productName = productElements.get(i).getText();
                            String productPrice = priceElements.get(i).getText();
                            boolean discount = false;

                            String productId = productName.toLowerCase().replaceAll("\\s+", "-");

                            Optional<MigrosProduct> existingProductOpt = migrosDataRepository.findById(productId);
                            if (existingProductOpt.isPresent()) {
                                MigrosProduct existingProduct = existingProductOpt.get();
                                existingProduct.setPrice(productPrice);
                                existingProduct.setDiscount(discount);
                                migrosDataRepository.save(existingProduct);
                                System.out.println("Updating product: " + existingProduct);
                            } else {
                                MigrosProduct product = new MigrosProduct(productId, productName, productPrice, discount);
                                migrosDataRepository.save(product);
                                System.out.println("Saving new product: " + product);
                            }
                        }
                        pageNumber++;
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            driver.quit();
            latch.countDown();
        }
    }

}
