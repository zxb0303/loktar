package com.loktar.web.test;

import lombok.SneakyThrows;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MainTest {
    private static String URL = "https://gqqy.chinatorch.org.cn/xonlinereport/inforeport/DataInnocom/saveOrUpdateDataEprIntellectualPropert.do";

    @SneakyThrows
    public static void main(String[] args) throws IOException {
        HttpClient httpClient = HttpClient.newHttpClient();
        String boundary = "----WebKitFormBoundarysHJr6cRtiNdKLCU1"; // 固定边界字符串
        Map<String, String> fields = new HashMap<>();
        fields.put("dataEprIntellectualPropert.Pzscqbh", "01");
        fields.put("dataEprIntellectualPropert.Plb", "2");
        fields.put("dataEprIntellectualPropert.Psqh", "ZL202021193654.8");
        fields.put("dataEprIntellectualPropert.Psqxmmc", "3D打印机喷料装置用的颗粒料进料机构");
        fields.put("dataEprIntellectualPropert.Phdfs", "3");
        fields.put("dataEprIntellectualPropert.Psqrq", "2024-05-01");
        fields.put("dataEprIntellectualPropert.id", "");
        fields.put("dataInnocom.id", "");
        fields.put("dataInnocomId", "");
        fields.put("dataId", "309c6ce9f63f11eebba900163e479657");
        fields.put("dataEprIntellectualPropert.dataId", "");
        fields.put("dataEprIntellectualPropert.entId", "");
        fields.put("dataEprIntellectualPropert.createDate", "");
        fields.put("dataEprIntellectualPropert.fromIp", "");
        fields.put("dataEprIntellectualPropert.pssxmbh", "");
        fields.put("dataEprIntellectualPropert.cpa", "");
        fields.put("curYear", "0");
        fields.put("struts.token.name", "token");
        fields.put("token", "GAUPWJLDOHR4IDZ6MKDVNH6TX0OSFL7D");

        byte[] requestBody = buildMultipartBody(fields, Path.of("F:/test.pdf"), boundary);

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(URL))
                .timeout(Duration.ofSeconds(20))
                .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36")
                .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7")
                .header("Referer", "https://gqqy.chinatorch.org.cn/xonlinereport/inforeport/DataInnocom/addDataEprIntellectualPropert.do")
                .header("Accept-Encoding", "gzip, deflate, br, zstd")
                .header("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8")
                .header("Cookie", "JSESSIONID=3437B521D73E4983076348AF34232735; insert_cookie=83593967; AD_VALUE=3a3505d5")
                .POST(HttpRequest.BodyPublishers.ofByteArray(requestBody))
                .build();

        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        String responseBody = response.body();
        System.out.println(responseBody);
    }

    private static String generateBoundary() {
        return "----WebKitFormBoundary" + UUID.randomUUID().toString().replace("-", "");
    }

    private static byte[] buildMultipartBody(Map<String, String> fields, Path filePath, String boundary) throws IOException {
        StringBuilder sb = new StringBuilder();

        for (Map.Entry<String, String> entry : fields.entrySet()) {
            sb.append("--").append(boundary).append("\r\n");
            sb.append("Content-Disposition: form-data; name=\"").append(entry.getKey()).append("\"\r\n\r\n");
            sb.append(entry.getValue()).append("\r\n");
        }

        sb.append("--").append(boundary).append("\r\n");
        sb.append("Content-Disposition: form-data; name=\"upload\"; filename=\"").append(filePath.getFileName()).append("\"\r\n");
        sb.append("Content-Type: ").append(Files.probeContentType(filePath)).append("\r\n\r\n");

        byte[] fileBytes = Files.readAllBytes(filePath);
        byte[] headerBytes = sb.toString().getBytes();
        byte[] footerBytes = ("\r\n--" + boundary + "--\r\n").getBytes();

        byte[] requestBody = new byte[headerBytes.length + fileBytes.length + footerBytes.length];
        System.arraycopy(headerBytes, 0, requestBody, 0, headerBytes.length);
        System.arraycopy(fileBytes, 0, requestBody, headerBytes.length, fileBytes.length);
        System.arraycopy(footerBytes, 0, requestBody, headerBytes.length + fileBytes.length, footerBytes.length);

        return requestBody;
    }
}

