package com.loktar.util;

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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VapeOnlineUtil {

    private static final String URL = "https://www.vapeonlines.net/collections/all_9415de3f/products/relxddp?data_from=index_index&prefetch_cache=1";
    private static final String ADD_URL = "https://www.vapeonlines.net/homeapi/cart/add";

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static void main(String[] args) {
//        List<Product> product1s = getProductsFromPage();
//        for (Product product : product1s) {
//            System.out.println(product);
//        }
//
//        List<Product> product2s = getInStockProducts();
//        for (Product product : product2s) {
//            System.out.println(product);
//        }

        List<Product> prodcut3s = getInStockProductsAndStockInfo();
        for (Product product : prodcut3s) {
            System.out.println(product);
        }
    }

    @SneakyThrows
    public static List<Product> getInStockProductsAndStockInfo() {
        List<Product> product2s = getInStockProducts();

        HttpClient httpClient = HttpClient.newBuilder().build();
        for (Product product : product2s) {
            int maxQuantity = 0;
            int batch = 5;
            // 只测试5的整数倍
            for (int quantity = batch; quantity <= 500; quantity += batch) {
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
                int code = root.get("code").asInt();
                if (code == 0) {
                    maxQuantity = quantity;
                } else if (code == 1001) {
                    break;
                } else {
                    break;
                }
                Thread.sleep(200);
            }
            product.setStockQuantity(maxQuantity);
        }
        return product2s;
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
    }
}