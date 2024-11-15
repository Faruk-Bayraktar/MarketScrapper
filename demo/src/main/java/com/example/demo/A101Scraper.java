package com.example.demo;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class A101Scraper implements Runnable {
    private final String baseUrl = "https://www.a101.com.tr/kapida/meyve-sebze/";
    private final ProductRepository productRepository;

    public A101Scraper(ProductRepository productRepository) {
        this.productRepository = productRepository;
        System.out.println("A101Scraper initialized with base URL: " + baseUrl);
    }

    @Override
    public void run() {
        try {
            // Ana sayfadan kategorileri çek
            Document doc = Jsoup.connect(baseUrl).get();
            Elements categoryElements = doc.select("a.bg-white.flex.items-center.rounded-full.overflow-hidden.cursor-pointer");
            List<String> categories = new ArrayList<>();

            // Her kategori için alt kategorileri çek
            for (Element categoryElement : categoryElements) {
                String categoryUrl = categoryElement.attr("href");
                System.out.println("Category URL: " + categoryUrl); // Debug çıktısı

                Document categoryDoc = Jsoup.connect("https://www.a101.com.tr" + categoryUrl).get();
                Elements subCategoryElements = categoryDoc.select("div.block.capitalize.w-[20ch].desktop:w-[25ch].whitespace-nowrap.truncate");

                for (Element subCategoryElement : subCategoryElements) {
                    String subCategory = subCategoryElement.text().toLowerCase().replace(" ", "-");
                    categories.add(subCategory);
                    System.out.println("Subcategory added: " + subCategory); // Debug çıktısı
                }
            }

            System.out.println("Categories: " + categories); // Debug çıktısı

            // Her alt kategori için verileri çek
            for (String category : categories) {
                int page = 1;
                while (true) {
                    try {
                        String url = baseUrl + category + "?page=" + page;
                        Document categoryPageDoc = Jsoup.connect(url).get();
                        System.out.println("Document fetched successfully for category: " + category + ", page: " + page);

                        Elements productNames = categoryPageDoc.select("mobile:text-xs tablet:text-xs line-clamp-3 h-12 font-medium overflow-hidden mb-[10px]");
                        Elements productPrices = categoryPageDoc.select("text-md absolute bottom-0 font-medium tablet:text-base text-[#333]");

                        if (productNames.isEmpty() && productPrices.isEmpty()) {
                            System.out.println("No more products found for category: " + category + ". Moving to next category.");
                            break;
                        }

                        for (int i = 0; i < productNames.size(); i++) {
                            Element name = productNames.get(i);
                            Element price = productPrices.get(i);
                            System.out.println("A101 Market - Category: " + category + " - Product: " + name.text() + " - Price: " + price.text());

                            // MongoDB'ye kaydetme kodu buraya gelecek
                        }

                        page++;
                    } catch (Exception e) {
                        e.printStackTrace();
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}