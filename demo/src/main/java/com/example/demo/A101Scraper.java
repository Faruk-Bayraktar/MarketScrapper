package com.example.demo;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class A101Scraper implements Runnable {

    private final String baseUrl = "https://www.a101.com.tr/kapida/meyve-sebze/meyve";

    @Override
    public void run() {

        while (true) {
            try {

                Document doc = Jsoup.connect(baseUrl).get();
                System.out.println("Sayfa başarıyla getirildi: ");

                Elements productNames = doc.select("div.mobile.text-xs.tablet.text-xs.line-clamp-3.h-12.font-medium.overflow-hidden.mb-[10px]");
                // Elements productPrices = doc.select("span.CPriceBox-module_price__bYk-c");

                if (productNames.isEmpty()) {
                    System.out.println("Daha fazla ürün bulunamadı. Kazıyıcı durduruluyor.");
                    break;
                }

                for (int i = 0; i < productNames.size(); i++) {
                    Element name = productNames.get(i);
                    System.out.println("Sok Market - Ürün: " + name.text());

                }

            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
        }
    }
}
