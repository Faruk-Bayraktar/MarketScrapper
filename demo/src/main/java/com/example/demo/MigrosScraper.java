package com.example.demo;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class MigrosScraper implements Runnable {
    private String baseUrl;
    private ProductRepository productRepository;

    public MigrosScraper(String baseUrl, ProductRepository productRepository) {
        this.baseUrl = baseUrl;
        this.productRepository = productRepository;
        System.out.println("MigrosScraper initialized with base URL: " + baseUrl);
    }

    @Override
    public void run() {
        int page = 1;
        while (true) {
            try {
                String url = baseUrl + "?page=" + page;
                Document doc = Jsoup.connect(url).get();
                System.out.println("Document fetched successfully for page: " + page);

                Elements productNames = doc.select("mat-caption text-color-black product-name");
                Elements productPrices = doc.select("price subtitle-1 ng-star-inserted");

                if (productNames.isEmpty() && productPrices.isEmpty()) {
                    System.out.println("No more products found. Stopping the scraper.");
                    break;
                }

                for (int i = 0; i < productNames.size(); i++) {
                    Element name = productNames.get(i);
                    Element price = productPrices.get(i);
                    System.out.println("Migros Market - Product: " + name.text() + " - Price: " + price.text());

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