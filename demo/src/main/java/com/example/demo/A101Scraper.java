package com.example.demo;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class A101Scraper implements Runnable {

    private final String baseUrl = "https://www.a101.com.tr/kapida/firindan";

    @Override
    public void run() {
        try {
            // Sayfayı indir
            Document doc = Jsoup.connect(baseUrl).get();
            System.out.println("Sayfa başarıyla getirildi!");

            // Ana div'leri seç
            Elements mainDivs = doc.select("div.bg-white.flex.items-center.rounded-full.overflow-hidden.cursor-pointer");

            if (mainDivs.isEmpty()) {
                System.out.println("Ana div bulunamadı!");
                return;
            }

            // Her ana div içindeki linkleri işle
            for (Element mainDiv : mainDivs) {
                Elements productLinks = mainDiv.select("a[class*='bg-white'][class*='flex'][class*='items-center'][class*='rounded-full'][class*='overflow-hidden'][class*='cursor-pointer']");

                for (Element productLink : productLinks) {
                    // Ürün adlarını içeren alt div'i seç
                    Element productNameDiv = productLink.selectFirst("div[class*='ml-2'][class*='flex-1'][class*='w-\\[20ch\\]'][class*='desktop:w-\\[25ch\\]'][class*='whitespace-nowrap'][class*='truncate']");

                    if (productNameDiv != null) {
                        String productName = productNameDiv.text();
                        String productLinkHref = productLink.attr("href"); // Ürün bağlantısını al
                        System.out.println("Ürün: " + productName + " | Link: " + productLinkHref);
                    } else {
                        System.out.println("Ürün adı bulunamadı!");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
