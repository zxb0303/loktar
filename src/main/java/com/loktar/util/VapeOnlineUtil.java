package com.loktar.util;


import com.github.houbb.opencc4j.util.ZhConverterUtil;
import lombok.extern.slf4j.Slf4j;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.SneakyThrows;
import org.apache.commons.lang3.ObjectUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
public class VapeOnlineUtil {

    private static final String URL = "https://vapeonlines.shop/collections/all_9415de3f/products/relxddp";
    private static final String ADD_URL = "https://vapeonlines.shop/homeapi/cart/add";

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static void main(String[] args) {
//        List<Product> product1s = getProductsFromPage();
//        for (Product product : product1s) {
//            System.out.println(product);
//        }

//        List<Product> product2s = getInStockProducts();
//        for (Product product : product2s) {
//            System.out.println(product);
//        }

//        List<Product> product3s = getInStockAndNeedProducts();
//        for (Product product : product3s) {
//            System.out.println(product);
//        }



        List<Product> products = getStockQuantityGreaterThan(2);

        String nowInStock = products.stream()
                .map(p ->
                        ZhConverterUtil.toSimple(p.getName())
                                .trim()
                                .replace("Ultra Pod","奥创")
                                .replace(" ", "")
                                .replace("-", "")
                                .replace("Infinity2", "")
                                .replaceAll("[a-zA-Z]+ ?", "")
                                .replace("【", "[")
                                .replace("】", "]")
                                .replace("(三颗装)", "")
                                .replace("（三颗装）", "")
                                .replace("[新]", "")
                                .replace("[]", "")
                                .replace("颗装","")
                                .replace("奥创","Ultra Pod")
                                + "," + p.getStockQuantityText()
                )
                .sorted()
                .collect(Collectors.joining(System.lineSeparator()));
        System.out.println(nowInStock);
    }


    @SneakyThrows
    public static List<Product> getInStockAndNeedProductsAndStockInfo() {
        List<Product> products = getInStockProducts();
        HttpClient httpClient = HttpClient.newBuilder().build();

        for (Product product : products) {
//            System.out.println(product.getName() + " - " + product.getProductId() + " - " + product.getSkuCode());
            // 库存查询上限20，超过20按20+展示：先探测上限，不足时二分确定确切数量
            int stockLimit = 20;
            int maxQuantity = findMaxPurchasable(httpClient, product, stockLimit);
            product.setStockQuantity(maxQuantity);
            product.setStockQuantityText(maxQuantity >= stockLimit ? stockLimit + "+" : String.valueOf(maxQuantity));
        }
        return products;
    }

    // 以最少查询次数确定最大可购买数量：先探测上限，一次成功即库存充足直接返回上限；否则在 [1, maxLimit-1] 内二分查找确切数量
    @SneakyThrows
    private static int findMaxPurchasable(HttpClient httpClient, Product product, int maxLimit) {
        if (tryPurchase(httpClient, product, maxLimit) == 0) {
            return maxLimit;
        }
        int maxQuantity = 0;
        int lo = 1;
        int hi = maxLimit - 1;
        while (lo <= hi) {
            try {
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
            int mid = (lo + hi) >>> 1;
            if (tryPurchase(httpClient, product, mid) == 0) {
                maxQuantity = mid;
                lo = mid + 1;
            } else {
                hi = mid - 1;
            }
        }
        return maxQuantity;
    }

    @SneakyThrows
    private static int tryPurchase(HttpClient httpClient, Product product, int quantity) {
        String body = String.format(
                "{\"product_id\":%s,\"sku_code\":\"%s\",\"quantity\":%d," +
                        "\"data_from\":\"index_index\",\"property\":[]}",
                product.getProductId(), product.getSkuCode(), quantity
        );
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(ADD_URL))
                .timeout(Duration.ofSeconds(30))
                .header("Content-Type", "application/json")
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        JsonNode root = MAPPER.readTree(response.body());
        return root.get("code").asInt();
    }


    public static List<Product> getInStockProducts() {
        List<Product> products = getProductsFromPage();
        List<Product> newProducts = new ArrayList<>();
        for (Product product : products) {
            if (!ObjectUtils.isEmpty(product.getAvailability()) && product.getAvailability().contains("InStock")) {
                newProducts.add(product);
            }
        }
        return newProducts;
    }

    // 筛选库存数量大于指定值的产品
    public static List<Product> getStockQuantityGreaterThan(int stockQuantity) {
        List<Product> products = getInStockAndNeedProductsAndStockInfo();
        List<Product> newProducts = new ArrayList<>();
        for (Product product : products) {
            if (product.getStockQuantity() > stockQuantity) {
                newProducts.add(product);
            }
        }
        return newProducts;
    }

    @SneakyThrows
    private static List<Product> getProductsFromPage() {
        List<Product> result = new ArrayList<>();
        HttpClient httpClient = HttpClient.newBuilder().build();
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(URL))
                .timeout(Duration.ofSeconds(60))
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
                .GET()
                .build();
        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        String respBody = response.body();
        Document document = Jsoup.parse(respBody);
        Elements scripts = document.select("script[type=application/ld+json]");
        for (Element script : scripts) {
            String json = script.data().trim();
            if (json.isEmpty()) {
                json = script.html().trim();
            }
            json = Parser.unescapeEntities(json, true);
            json = escapeStringLiteralsNewline(json);
            JsonNode node = MAPPER.readTree(json);
            if (node.has("@type") && "Product".equals(node.get("@type").asText()) && node.has("offers")) {
                JsonNode offersNode = node.get("offers");
                List<Product> offerList = MAPPER.readValue(offersNode.traverse(), new TypeReference<List<Product>>() {
                });
                for (Product p : offerList) {
                    if (p.getUrl() != null) {
                        String skuCode = extractSkuCode(p.getUrl());
                        p.setSkuCode(skuCode);
                        String productId = (skuCode != null && skuCode.contains("-")) ? skuCode.split("-")[0] : skuCode;
                        p.setProductId(productId);
                    }
                }
                result.addAll(offerList);
            }
        }
        return result;
    }

    private static final Pattern JSON_STRING = Pattern.compile(
            "\"([^\"\\\\]*(?:\\\\.[^\"\\\\]*)*)\"",
            Pattern.MULTILINE);

    public static String escapeStringLiteralsNewline(String jsonText) {
        Matcher m = JSON_STRING.matcher(jsonText);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            String orig = m.group(1);
            String fixed = orig
                    .replace("\r\n", "\\n")
                    .replace("\n", "\\n")
                    .replace("\r", "\\n");
            m.appendReplacement(sb, Matcher.quoteReplacement("\"" + fixed + "\""));
        }
        m.appendTail(sb);
        return sb.toString();
    }

    private static String extractSkuCode(String url) {
        try {
            int idx = url.indexOf("sku_code=");
            if (idx == -1) return null;
            String sub = url.substring(idx + "sku_code=".length());
            int andIdx = sub.indexOf('&');
            String skuCode = (andIdx != -1 ? sub.substring(0, andIdx) : sub);
            // 防止有URL编码
            return java.net.URLDecoder.decode(skuCode, java.nio.charset.StandardCharsets.UTF_8);
        } catch (Exception e) {
            return null;
        }
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Product {
        private String priceCurrency;
        private String price;
        private String name;
        private String itemCondition;
        private String mpn;
        private String sku;
        private String gtin;
        private String url;
        private String availability;
        private String productId;
        private String skuCode;
        private int stockQuantity;
        private String stockQuantityText;
    }
}