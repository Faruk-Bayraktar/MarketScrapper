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

import com.example.demo.repository.SokDataRepository;

import io.github.bonigarcia.wdm.WebDriverManager;

public class SokScraper implements Runnable {

    private final CountDownLatch latch;
    private final String baseUrl = "https://www.sokmarket.com.tr/";

    private final SokDataRepository sokDataRepository;

    @Autowired
    public SokScraper(SokDataRepository sokDataRepository, CountDownLatch latch) {
        this.sokDataRepository = sokDataRepository;
        this.latch = latch;
    }//Bu yapıcı metod, SokScraper sınıfı için latch threadlerin tamamlanmasını beklemek için kullanılır ve sokDataRepository nesnesi oluşturulur.

    @Override
    public void run() {
        WebDriverManager.chromedriver().setup();// Gerekli olan WebDriver suruculerini otomatik olarak yukler.
        WebDriver driver = new ChromeDriver();
        try {
            driver.get(baseUrl);

            Thread.sleep(3000);
            List<WebElement> links = driver.findElements(By.cssSelector(".CategoryList_categories__wmXtl.CategoryList_isMegaMenu__iH1P5 a"));
            List<String> hrefs = new ArrayList<>();
            for (WebElement link : links) {
                hrefs.add(link.getAttribute("href"));
            }
            if (hrefs.size() > 3) {
                hrefs = hrefs.subList(3, hrefs.size());
            }
            if (!hrefs.isEmpty()) {
                hrefs.remove(hrefs.size() - 1);
            }

            String oldUrl = "https://www.sokmarket.com.tr/giyim-ve-ayakkabi-ve-aksesuar-c-20886";
            String newUrl = "https://www.sokmarket.com.tr/giyim-ayakkabi-ve-aksesuar-c-20886";

            if (hrefs.contains(oldUrl)) {
                int index = hrefs.indexOf(oldUrl);
                hrefs.set(index, newUrl);
            }
            for (String href : hrefs) {
                int page = 1;
                boolean hasNextPage = true;

                while (hasNextPage) {
                    String fullUrl = href + "?page=" + page;
                    driver.get(fullUrl);// Verilen URL'ye gitmek icin kullanilir.
                    Thread.sleep(2000);

                    List<WebElement> products = driver.findElements(By.cssSelector(".CProductCard-module_infoContainer__F8uxY"));// Urunlerin bulundugu elementlerin listesini alir.
                    if (products.isEmpty()) {
                        hasNextPage = false;
                    } else {
                        for (WebElement product : products) {
                            WebElement titleElement = product.findElement(By.cssSelector(".CProductCard-module_title__u8bMW"));
                            String productName = titleElement.getText();
                            String productId = productName.toLowerCase().replaceAll("\\s+", "-");
                            WebElement priceElement;
                            String price;
                            boolean discount = false;
                            if (!product.findElements(By.cssSelector(".CPriceBox-module_discountedPrice__15Ffw")).isEmpty()) {
                                priceElement = product.findElement(By.cssSelector(".CPriceBox-module_discountedPrice__15Ffw"));
                                price = priceElement.getText();
                                discount = true;
                            } else if (!product.findElements(By.cssSelector(".CPriceBox-module_price__bYk-c")).isEmpty()) {
                                priceElement = product.findElement(By.cssSelector(".CPriceBox-module_price__bYk-c"));
                                price = priceElement.getText();
                            } else {
                                price = "Price not found";
                            }

                            Optional<SokProduct> existingProductOpt = sokDataRepository.findById(productId);// Urunun daha once kaydedilip kaydedilmedigini kontrol eder.
                            if (existingProductOpt.isPresent()) {
                                SokProduct existingProduct = existingProductOpt.get();
                                existingProduct.setPrice(price);
                                existingProduct.setDiscount(discount);
                                sokDataRepository.save(existingProduct);
                                System.out.println("Updating product: " + existingProduct);
                            } else {
                                SokProduct productEntity = new SokProduct(productId, productName, price, discount);
                                sokDataRepository.save(productEntity);
                                System.out.println("Saving new product: " + productEntity);
                            }
                        }
                        page++;
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
