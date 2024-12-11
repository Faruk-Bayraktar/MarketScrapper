package com.example.demo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.demo.repository.SokDataRepository;

import io.github.bonigarcia.wdm.WebDriverManager;

public class SokScraper implements Runnable {

    private final String baseUrl = "https://www.sokmarket.com.tr/";
    private final SokDataRepository sokDataRepository;

    @Autowired
    public SokScraper(SokDataRepository sokDataRepository) {
        this.sokDataRepository = sokDataRepository;
    }

    @Override
    public void run() {
        WebDriverManager.chromedriver().setup(); // Doğru sürücüyü otomatik bulur ve yükler
        WebDriver driver = new ChromeDriver();
        try {
            // Sayfayı aç
            driver.get(baseUrl);

            // Bir süre bekle (JavaScript yüklemesi için)
            Thread.sleep(3000); // Gerekirse süreyi artırabilirsiniz
            // Belirtilen div altındaki tüm a etiketlerini bul
            List<WebElement> links = driver.findElements(By.cssSelector(".CategoryList_categories__wmXtl.CategoryList_isMegaMenu__iH1P5 a"));
            List<String> hrefs = new ArrayList<>();
            for (WebElement link : links) {
                // Her bir a etiketinin href özniteliğini listeye ekle
                hrefs.add(link.getAttribute("href"));
            }
            // İlk 3 elemanı ve sondan bir önceki elemanı çıkart
            if (hrefs.size() > 3) {
                hrefs = hrefs.subList(3, hrefs.size());
            }
            // Sonuncu elemanı çıkart
            if (!hrefs.isEmpty()) {
                hrefs.remove(hrefs.size() - 1);
            }
            // Belirtilen URL'yi güncelle
            String oldUrl = "https://www.sokmarket.com.tr/giyim-ve-ayakkabi-ve-aksesuar-c-20886";
            String newUrl = "https://www.sokmarket.com.tr/giyim-ayakkabi-ve-aksesuar-c-20886";

            if (hrefs.contains(oldUrl)) {
                // URL'yi güncelle
                int index = hrefs.indexOf(oldUrl);
                hrefs.set(index, newUrl);
            }
            for (String href : hrefs) {
                int page = 1;
                boolean hasNextPage = true;

                while (hasNextPage) {
                    String fullUrl = href + "?page=" + page;
                    driver.get(fullUrl);
                    // Burada her bir sayfa için yapılacak işlemleri ekleyebilirsiniz
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

                            // Ürün ID'sini oluştur (ürün adını kullanarak)
                            String productId = productName.toLowerCase().replaceAll("\\s+", "-");

                            // Fiyatı al
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

                            // Ürünü kaydet veya güncelle
                            Optional<SokProduct> existingProductOpt = sokDataRepository.findById(productId);
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
            // Tarayıcıyı kapat
            driver.quit();
            // Thread'i sonlandır
            Thread.currentThread().interrupt();
        }
    }
}
