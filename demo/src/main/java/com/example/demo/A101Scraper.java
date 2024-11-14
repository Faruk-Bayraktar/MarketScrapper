package com.example.demo;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

//BURASI DUZENLECEKEEEKKEKEKEKEKEKK !!!!!!

public class A101Scraper implements Runnable {
    private String baseUrl;
    private ProductRepository productRepository;

    public A101Scraper(String baseUrl, ProductRepository productRepository) {
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

                Elements productNames = doc.select("mobile:text-xs tablet:text-xs line-clamp-3 h-12 font-medium overflow-hidden mb-[10px]");
                Elements productPrices = doc.select("text-md absolute bottom-0 font-medium tablet:text-base text-[#333]");

                if (productNames.isEmpty() && productPrices.isEmpty()) {
                    System.out.println("No more products found. Stopping the scraper.");
                    break;
                }

                for (int i = 0; i < productNames.size(); i++) {
                    Element name = productNames.get(i);
                    Element price = productPrices.get(i);
                    System.out.println("A101 Market - Product: " + name.text() + " - Price: " + price.text());

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