package com.example.demo;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

//BURASI DÜZENLENECEK!!!!
public class SokScraper implements Runnable {

    private final String baseUrl = "https://www.sokmarket.com.tr/meyve-ve-sebze-c-20";
    private final ProductRepository productRepository;

    public SokScraper(ProductRepository productRepository) {
        this.productRepository = productRepository;
        System.out.println("SokScraper initialized with base URL: " + baseUrl);
    }

    @Override
    public void run() {
        int page = 1;
        while (true) {
            try {
                String url = baseUrl + "?page=" + page;
                Document doc = Jsoup.connect(url).get();
                System.out.println("Document fetched successfully for page: " + page);

                Elements productNames = doc.select("h2.CProductCard-module_title__u8bMW");
                Elements productPrices = doc.select("span.CPriceBox-module_price__bYk-c");

                if (productNames.isEmpty() && productPrices.isEmpty()) {
                    System.out.println("No more products found. Stopping the scraper.");
                    break;
                }

                for (int i = 0; i < productNames.size(); i++) {
                    Element name = productNames.get(i);
                    Element price = productPrices.get(i);
                    System.out.println("Sok Market - Product: " + name.text() + " - Price: " + price.text());

                    // MongoDB'ye kaydet
                    Product product = new Product(name.text(), price.text());
                    productRepository.save(product);
                }

                page++;
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
        }
    }
}
