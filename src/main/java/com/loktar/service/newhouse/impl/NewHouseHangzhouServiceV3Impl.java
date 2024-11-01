package com.loktar.service.newhouse.impl;


import com.azure.ai.documentintelligence.models.AnalyzeResult;
import com.azure.ai.documentintelligence.models.DocumentTable;
import com.azure.ai.documentintelligence.models.DocumentTableCell;
import com.loktar.conf.LokTarConfig;
import com.loktar.conf.LokTarConstant;
import com.loktar.domain.newhouse.NewHouseHangzhouV3;
import com.loktar.domain.newhouse.NewHouseHangzhouV3Detail;
import com.loktar.domain.newhouse.NewHouseHangzhouV3Presell;
import com.loktar.domain.newhouse.NewHouseHangzhouV3PresellBuild;
import com.loktar.mapper.newhouse.NewHouseHangzhouV3DetailMapper;
import com.loktar.mapper.newhouse.NewHouseHangzhouV3Mapper;
import com.loktar.mapper.newhouse.NewHouseHangzhouV3PresellBuildMapper;
import com.loktar.mapper.newhouse.NewHouseHangzhouV3PresellMapper;
import com.loktar.service.newhouse.NewHouseHangzhouV3Service;
import com.loktar.util.AzureDocIntelligenceUtil;
import com.loktar.util.PicUtil;
import com.loktar.util.RedisUtil;
import com.loktar.util.UUIDUtil;
import lombok.SneakyThrows;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.time.Duration;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;


@Service
public class NewHouseHangzhouServiceV3Impl implements NewHouseHangzhouV3Service {

    private final static String URL_NEW_HOUSE_MEMBER_LOGIN = "https://www.hangzhou.gov.cn/hzyhzx/member/login/showUserResult.do";

    private final static String URL_NEW_HOUSE_DATA = "https://zwfw.fgj.hangzhou.gov.cn/hzfcweb_ifs/interaction/wsslc?szcq=&mjfw=&fwyt=&lpmc={0}&yszbh=&xmdz=";

    private final static String URL_NEW_HOUSE_PRESELL_DATA = "https://zwfw.fgj.hangzhou.gov.cn/hzfcweb_ifs/interaction/wsslc_detail_yfyj?id={0}";

    private final static String URL_NEW_HOUSE_PRESELL_BUILD_DATA = "https://zwfw.fgj.hangzhou.gov.cn/hzfcweb_ifs/interaction/wsslc_detail_yfyj?id={0}&yszbh={1}";

    private final static String URL_NEW_HOUSE_DETAIL_DATA = "https://zwfw.fgj.hangzhou.gov.cn/hzfcweb_ifs/interaction/wsslc_detail_yfyj?p={0}&id={1}&yszbh={2}&zrzid={3}";

    private final static String COOKIE_STR = "zh_choose_undefined=s; HZSESSIONID={0}; arialoadData=false";

    private final static String COOKIE_NAME_HZSESSIONID = "HZSESSIONID";

    private final static Map<String, Integer> STATUS_MAP = new HashMap<>();

    private final NewHouseHangzhouV3Mapper newHouseHangzhouV3Mapper;

    private final NewHouseHangzhouV3PresellMapper newHouseHangzhouV3PresellMapper;

    private final NewHouseHangzhouV3PresellBuildMapper newHouseHangzhouV3PresellBuildMapper;

    private final RedisUtil redisUtil;


    private final LokTarConfig lokTarConfig;
    private final NewHouseHangzhouV3DetailMapper newHouseHangzhouV3DetailMapper;

    public NewHouseHangzhouServiceV3Impl(NewHouseHangzhouV3Mapper newHouseHangzhouV3Mapper, NewHouseHangzhouV3PresellMapper newHouseHangzhouV3PresellMapper, NewHouseHangzhouV3PresellBuildMapper newHouseHangzhouV3PresellBuildMapper, RedisUtil redisUtil, LokTarConfig lokTarConfig, NewHouseHangzhouV3DetailMapper newHouseHangzhouV3DetailMapper) {
        this.newHouseHangzhouV3Mapper = newHouseHangzhouV3Mapper;
        this.newHouseHangzhouV3PresellMapper = newHouseHangzhouV3PresellMapper;
        this.newHouseHangzhouV3PresellBuildMapper = newHouseHangzhouV3PresellBuildMapper;
        this.redisUtil = redisUtil;
        this.lokTarConfig = lokTarConfig;
        STATUS_MAP.put("可售", 1);
        STATUS_MAP.put("已售", 2);
        STATUS_MAP.put("已经预定", 3);
        STATUS_MAP.put("限制房产", 4);
        this.newHouseHangzhouV3DetailMapper = newHouseHangzhouV3DetailMapper;
    }

    @Override
    @SneakyThrows
    public void memberLogin() {
        Thread.sleep(2000);
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(URL_NEW_HOUSE_MEMBER_LOGIN))
                .timeout(Duration.ofSeconds(10))
                .header(LokTarConstant.HTTP_HEADER_USER_AGENT_NAME, LokTarConstant.HTTP_HEADER_USER_AGENT_VALUE)
                .GET()
                .build();
        HttpResponse<byte[]> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofByteArray());
        Map<String, String> sessionCookies = extractCookies(response);
        String redisValue = MessageFormat.format(COOKIE_STR, sessionCookies.get(COOKIE_NAME_HZSESSIONID));
        redisUtil.set(LokTarConstant.REDIS_KEY_NEWHOUSE_COOKIE, redisValue,60*30);
    }

    @Override
    @SneakyThrows
    public NewHouseHangzhouV3 getNewHouseData(String houseName) {
        Thread.sleep(2000);
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(MessageFormat.format(URL_NEW_HOUSE_DATA, houseName)))
                .timeout(Duration.ofSeconds(10))
                .header(LokTarConstant.HTTP_HEADER_USER_AGENT_NAME, LokTarConstant.HTTP_HEADER_USER_AGENT_VALUE)
                .header(LokTarConstant.HTTP_HEADER_ACCEPT_NAME, LokTarConstant.HTTP_HEADER_ACCEPT_VALUE_HTML)
                .header(LokTarConstant.HTTP_HEADER_ACCEPT_ENCODING_NAME, LokTarConstant.HTTP_HEADER_ACCEPT_ENCODING_VALUE_GZIP)
                .header(LokTarConstant.HTTP_HEADER_ACCEPT_LANGUAGE_NAME, LokTarConstant.HTTP_HEADER_ACCEPT_LANGUAGE_VALUE_CN)
                .header(LokTarConstant.HTTP_HEADER_CONTENT_TYPE_NAME, LokTarConstant.HTTP_HEADER_CONTENT_TYPE_VALUE_HTML)
                .header(LokTarConstant.HTTP_HEADER_COOKIE_NAME, (String) redisUtil.get(LokTarConstant.REDIS_KEY_NEWHOUSE_COOKIE))
                .GET()
                .build();
        HttpResponse<byte[]> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofByteArray());
        byte[] responseBody = response.body();
        String contentEncoding = response.headers().firstValue(LokTarConstant.HTTP_HEADER_CONTENT_ENCODING_NAME).orElse("");
        if (LokTarConstant.HTTP_HEADER_CONTENT_ENCODING_VALUE_GZIP.equalsIgnoreCase(contentEncoding)) {
            try (GZIPInputStream gzipInputStream = new GZIPInputStream(new ByteArrayInputStream(responseBody))) {
                responseBody = gzipInputStream.readAllBytes();
            }
        }
        String responseString = new String(responseBody);
        NewHouseHangzhouV3 newHouseHangzhouV3 = new NewHouseHangzhouV3();
        Document document = Jsoup.parse(responseString);
        Element houseElement = document.selectFirst("[class=content]").selectFirst("[class=loupan]").selectFirst("[class=loupan_left]");
        List<Element> lis = houseElement.select("li");
        String onclickStr = lis.getFirst().select("a").attr("onclick");
        Pattern r = Pattern.compile("goToWsslDetail\\(\"([^\"]+)\"\\)");
        Matcher m = r.matcher(onclickStr);
        String houseId = null;
        if (m.find()) {
            houseId = m.group(1);
        }
        newHouseHangzhouV3.setHouseId(UUIDUtil.randomUUID());
        newHouseHangzhouV3.setTempHouseId(houseId);
        newHouseHangzhouV3.setName(lis.get(0).select("a").text());
        Element usefulElement = lis.get(1);
        usefulElement.select("span").remove();
        newHouseHangzhouV3.setUseful(usefulElement.text());
        Element addressElement = lis.get(3);
        addressElement.select("span").remove();
        newHouseHangzhouV3.setAddress(addressElement.text());
        Element companyElement = lis.get(4);
        companyElement.select("span").remove();
        newHouseHangzhouV3.setCompany(companyElement.text());
        Element phoneElement = lis.get(5);
        phoneElement.select("span").remove();
        newHouseHangzhouV3.setPhone(phoneElement.text());
        NewHouseHangzhouV3 existNewHouseHangzhouV3 = newHouseHangzhouV3Mapper.selectByName(newHouseHangzhouV3.getName());
        if (ObjectUtils.isEmpty(existNewHouseHangzhouV3)) {
            newHouseHangzhouV3Mapper.insert(newHouseHangzhouV3);
            return newHouseHangzhouV3;
        } else {
            existNewHouseHangzhouV3.setTempHouseId(houseId);
            newHouseHangzhouV3Mapper.updateByPrimaryKey(existNewHouseHangzhouV3);
            return existNewHouseHangzhouV3;
        }
    }

    @Override
    @SneakyThrows
    public List<NewHouseHangzhouV3Presell> getNewHousePresellDataByHouseId(String houseId) {
        Thread.sleep(2000);
        NewHouseHangzhouV3 newHouseHangzhouV3 = newHouseHangzhouV3Mapper.selectByPrimaryKey(houseId);
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(MessageFormat.format(URL_NEW_HOUSE_PRESELL_DATA, newHouseHangzhouV3.getTempHouseId())))
                .timeout(Duration.ofSeconds(10))
                .header(LokTarConstant.HTTP_HEADER_USER_AGENT_NAME, LokTarConstant.HTTP_HEADER_USER_AGENT_VALUE)
                .header(LokTarConstant.HTTP_HEADER_ACCEPT_NAME, LokTarConstant.HTTP_HEADER_ACCEPT_VALUE_HTML)
                .header(LokTarConstant.HTTP_HEADER_ACCEPT_ENCODING_NAME, LokTarConstant.HTTP_HEADER_ACCEPT_ENCODING_VALUE_GZIP)
                .header(LokTarConstant.HTTP_HEADER_ACCEPT_LANGUAGE_NAME, LokTarConstant.HTTP_HEADER_ACCEPT_LANGUAGE_VALUE_CN)
                .header(LokTarConstant.HTTP_HEADER_CONTENT_TYPE_NAME, LokTarConstant.HTTP_HEADER_CONTENT_TYPE_VALUE_HTML)
                .header(LokTarConstant.HTTP_HEADER_COOKIE_NAME, (String) redisUtil.get(LokTarConstant.REDIS_KEY_NEWHOUSE_COOKIE))
                .GET()
                .build();
        HttpResponse<byte[]> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofByteArray());
        byte[] responseBody = response.body();
        String contentEncoding = response.headers().firstValue(LokTarConstant.HTTP_HEADER_CONTENT_ENCODING_NAME).orElse("");
        if (LokTarConstant.HTTP_HEADER_CONTENT_ENCODING_VALUE_GZIP.equalsIgnoreCase(contentEncoding)) {
            try (GZIPInputStream gzipInputStream = new GZIPInputStream(new ByteArrayInputStream(responseBody))) {
                responseBody = gzipInputStream.readAllBytes();
            }
        }
        String responseString = new String(responseBody);
        Document document = Jsoup.parse(responseString);
        List<Element> LinkDivs = document.select("[class=lptypebarin]");
        List<Element> presellAs = LinkDivs.getFirst().select("a");
        List<NewHouseHangzhouV3Presell> newHouseHangzhouV3Presells = new ArrayList<>();
        for (Element presellA : presellAs) {
            String presellId = presellA.text();
            if (ObjectUtils.isEmpty(newHouseHangzhouV3PresellMapper.selectByPrimaryKey(presellId))) {
                NewHouseHangzhouV3Presell newHouseHangzhouV3Presell = new NewHouseHangzhouV3Presell();
                newHouseHangzhouV3Presell.setPresellId(presellId);
                newHouseHangzhouV3Presell.setHouseId(newHouseHangzhouV3.getHouseId());
                newHouseHangzhouV3Presell.setStatus(0);
                newHouseHangzhouV3Presells.add(newHouseHangzhouV3Presell);
            }
        }
        if (CollectionUtils.isNotEmpty(newHouseHangzhouV3Presells)) {
            newHouseHangzhouV3PresellMapper.insertBatch(newHouseHangzhouV3Presells);
        }
        return newHouseHangzhouV3Presells;
    }

    @Override
    @SneakyThrows
    public List<NewHouseHangzhouV3PresellBuild> getNewHousePresellBuildDataByHouseId(String houseId) {
        NewHouseHangzhouV3 newHouseHangzhouV3 = newHouseHangzhouV3Mapper.selectByPrimaryKey(houseId);
        List<NewHouseHangzhouV3Presell> newHouseHangzhouV3Presells = newHouseHangzhouV3PresellMapper.selectByHouseId(houseId);
        List<NewHouseHangzhouV3PresellBuild> newHouseHangzhouV3PresellBuilds = new ArrayList<>();

        for (NewHouseHangzhouV3Presell newHouseHangzhouV3Presell : newHouseHangzhouV3Presells) {
            Thread.sleep(2000);
            HttpClient httpClient = HttpClient.newHttpClient();
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(MessageFormat.format(URL_NEW_HOUSE_PRESELL_BUILD_DATA, newHouseHangzhouV3.getTempHouseId(), newHouseHangzhouV3Presell.getPresellId())))
                    .timeout(Duration.ofSeconds(10))
                    .header(LokTarConstant.HTTP_HEADER_USER_AGENT_NAME, LokTarConstant.HTTP_HEADER_USER_AGENT_VALUE)
                    .header(LokTarConstant.HTTP_HEADER_ACCEPT_NAME, LokTarConstant.HTTP_HEADER_ACCEPT_VALUE_HTML)
                    .header(LokTarConstant.HTTP_HEADER_ACCEPT_ENCODING_NAME, LokTarConstant.HTTP_HEADER_ACCEPT_ENCODING_VALUE_GZIP)
                    .header(LokTarConstant.HTTP_HEADER_ACCEPT_LANGUAGE_NAME, LokTarConstant.HTTP_HEADER_ACCEPT_LANGUAGE_VALUE_CN)
                    .header(LokTarConstant.HTTP_HEADER_CONTENT_TYPE_NAME, LokTarConstant.HTTP_HEADER_CONTENT_TYPE_VALUE_HTML)
                    .header(LokTarConstant.HTTP_HEADER_COOKIE_NAME, (String) redisUtil.get(LokTarConstant.REDIS_KEY_NEWHOUSE_COOKIE))
                    .GET()
                    .build();
            HttpResponse<byte[]> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofByteArray());
            byte[] responseBody = response.body();
            String contentEncoding = response.headers().firstValue(LokTarConstant.HTTP_HEADER_CONTENT_ENCODING_NAME).orElse("");
            if (LokTarConstant.HTTP_HEADER_CONTENT_ENCODING_VALUE_GZIP.equalsIgnoreCase(contentEncoding)) {
                try (GZIPInputStream gzipInputStream = new GZIPInputStream(new ByteArrayInputStream(responseBody))) {
                    responseBody = gzipInputStream.readAllBytes();
                }
            }
            String responseString = new String(responseBody);
            Document document = Jsoup.parse(responseString);
            List<Element> LinkDivs = document.select("[class=lptypebarin]");
            List<Element> buildAs = LinkDivs.get(1).select("a");
            for (Element buildA : buildAs) {
                String href = buildA.attr("href");
                Pattern r = Pattern.compile("yszbh=([^&]+)&zrzid=([^&]+)");
                Matcher m = r.matcher(href);
                String buildId = null;
                if (m.find()) {
                    buildId = m.group(2);
                }
                if (ObjectUtils.isEmpty(newHouseHangzhouV3PresellBuildMapper.selectByPrimaryKey(buildId))) {
                    NewHouseHangzhouV3PresellBuild newHouseHangzhouV3PresellBuild = new NewHouseHangzhouV3PresellBuild();
                    newHouseHangzhouV3PresellBuild.setBuildId(buildId);
                    newHouseHangzhouV3PresellBuild.setBuildNo(buildA.text());
                    newHouseHangzhouV3PresellBuild.setPresellId(newHouseHangzhouV3Presell.getPresellId());
                    newHouseHangzhouV3PresellBuild.setHouseId(houseId);
                    newHouseHangzhouV3PresellBuilds.add(newHouseHangzhouV3PresellBuild);
                }
            }
        }
        if (CollectionUtils.isNotEmpty(newHouseHangzhouV3PresellBuilds)) {
            newHouseHangzhouV3PresellBuildMapper.insertBatch(newHouseHangzhouV3PresellBuilds);
        }
        return newHouseHangzhouV3PresellBuilds;
    }

    @Override
    @SneakyThrows
    public List<NewHouseHangzhouV3Detail> getNewHouseDetailByHouseId(String houseId) {
        NewHouseHangzhouV3 newHouseHangzhouV3 = newHouseHangzhouV3Mapper.selectByPrimaryKey(houseId);
        List<NewHouseHangzhouV3PresellBuild> newHouseHangzhouV3PresellBuilds = newHouseHangzhouV3PresellBuildMapper.selectByHouseId(houseId);
        for (NewHouseHangzhouV3PresellBuild newHouseHangzhouV3PresellBuild : newHouseHangzhouV3PresellBuilds) {
            int page = 1;
            int pageSize = 1;
            while (page <= pageSize) {
                Thread.sleep(2000);
                HttpClient httpClient = HttpClient.newHttpClient();
                HttpRequest httpRequest = HttpRequest.newBuilder()
                        .uri(URI.create(MessageFormat.format(URL_NEW_HOUSE_DETAIL_DATA, page, newHouseHangzhouV3.getTempHouseId(), newHouseHangzhouV3PresellBuild.getPresellId(), newHouseHangzhouV3PresellBuild.getBuildId())))
                        .timeout(Duration.ofSeconds(10))
                        .header(LokTarConstant.HTTP_HEADER_USER_AGENT_NAME, LokTarConstant.HTTP_HEADER_USER_AGENT_VALUE)
                        .header(LokTarConstant.HTTP_HEADER_ACCEPT_NAME, LokTarConstant.HTTP_HEADER_ACCEPT_VALUE_HTML)
                        .header(LokTarConstant.HTTP_HEADER_ACCEPT_ENCODING_NAME, LokTarConstant.HTTP_HEADER_ACCEPT_ENCODING_VALUE_GZIP)
                        .header(LokTarConstant.HTTP_HEADER_ACCEPT_LANGUAGE_NAME, LokTarConstant.HTTP_HEADER_ACCEPT_LANGUAGE_VALUE_CN)
                        .header(LokTarConstant.HTTP_HEADER_CONTENT_TYPE_NAME, LokTarConstant.HTTP_HEADER_CONTENT_TYPE_VALUE_HTML)
                        .header(LokTarConstant.HTTP_HEADER_COOKIE_NAME, (String) redisUtil.get(LokTarConstant.REDIS_KEY_NEWHOUSE_COOKIE))
                        .GET()
                        .build();
                HttpResponse<byte[]> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofByteArray());
                byte[] responseBody = response.body();
                String contentEncoding = response.headers().firstValue(LokTarConstant.HTTP_HEADER_CONTENT_ENCODING_NAME).orElse("");
                if (LokTarConstant.HTTP_HEADER_CONTENT_ENCODING_VALUE_GZIP.equalsIgnoreCase(contentEncoding)) {
                    try (GZIPInputStream gzipInputStream = new GZIPInputStream(new ByteArrayInputStream(responseBody))) {
                        responseBody = gzipInputStream.readAllBytes();
                    }
                }
                String responseString = new String(responseBody);
                Document document = Jsoup.parse(responseString);
                Element em = document.selectFirst("[class=current]");
                pageSize = Integer.parseInt(em.attr("data-total-pages"));
                Elements scripts = document.getElementsByTag("script");
                Pattern pattern = Pattern.compile("convertBase64UrlToBlob\\(\"(.*?)\"\\)");
                String base64String = null;
                for (Element script : scripts) {
                    Matcher matcher = pattern.matcher(script.html());
                    if (matcher.find()) {
                        base64String = matcher.group(1);
                        break;
                    }
                }
                base64String = base64String.replace("\\/", "/");
                String path = MessageFormat.format(lokTarConfig.getPath().getNewhouseOriginal(), houseId, newHouseHangzhouV3PresellBuild.getPresellId(), newHouseHangzhouV3PresellBuild.getBuildId());
                String filename = String.format("%04d", page) + LokTarConstant.PIC_SUFFIX_PNG;
                //TODO 打印
                System.out.println(path + filename);
                saveBase64ToPNG(base64String, path + filename);
                page = page + 1;
            }
        }


        return List.of();
    }

    @Override
    @SneakyThrows
    public void getNewHouseDetailByHouseIdAndAzure(String houseId) {
        List<NewHouseHangzhouV3Detail> newHouseHangzhouV3Details = new ArrayList<>();
        List<NewHouseHangzhouV3PresellBuild> newHouseHangzhouV3PresellBuilds = newHouseHangzhouV3PresellBuildMapper.selectByHouseId(houseId);
        for (NewHouseHangzhouV3PresellBuild newHouseHangzhouV3PresellBuild : newHouseHangzhouV3PresellBuilds) {
            String pngfilesFoldpath = MessageFormat.format(lokTarConfig.getPath().getNewhouseOriginal(), houseId, newHouseHangzhouV3PresellBuild.getPresellId(), newHouseHangzhouV3PresellBuild.getBuildId());
            String jpgfilesFoldpath = MessageFormat.format(lokTarConfig.getPath().getNewhouseCover(), houseId, newHouseHangzhouV3PresellBuild.getPresellId(), newHouseHangzhouV3PresellBuild.getBuildId());
            File dir = new File(pngfilesFoldpath);
            String[] pngfilesName = dir.list();
            for (String pngfileName : pngfilesName) {
                String pngfilePath = pngfilesFoldpath + pngfileName;
                String jpgfilePath = jpgfilesFoldpath + pngfileName.replace(LokTarConstant.PIC_SUFFIX_PNG, LokTarConstant.PIC_SUFFIX_JPG);
                //TODO 打印
                System.out.println(jpgfilePath);
                PicUtil.converPNGtoJPG(pngfilePath, jpgfilePath);
                AnalyzeResult analyzeLayoutResult = AzureDocIntelligenceUtil.getAnalyze(LokTarConstant.AZURE_DOCINTELLIGENCE_MODEL_ID, jpgfilePath,"1");
                DocumentTable documentTable = analyzeLayoutResult.getTables().getFirst();
                List<DocumentTableCell> cells = documentTable.getCells();
                for (int rowIndex = 0; rowIndex < documentTable.getRowCount(); rowIndex++) {
                    if ("楼栋".equals(getDocumentTableCellByRowAndColown(cells, rowIndex, 0).getContent())) {
                        continue;
                    }
                    NewHouseHangzhouV3Detail newHouseHangzhouV3Detail = new NewHouseHangzhouV3Detail();
                    newHouseHangzhouV3Detail.setDetailId(UUIDUtil.randomUUID());
                    newHouseHangzhouV3Detail.setBuildId(newHouseHangzhouV3PresellBuild.getBuildId());
                    newHouseHangzhouV3Detail.setPresellId(newHouseHangzhouV3PresellBuild.getPresellId());
                    newHouseHangzhouV3Detail.setHouseId(houseId);
                    newHouseHangzhouV3Detail.setBuildNo(newHouseHangzhouV3PresellBuild.getBuildNo());
                    newHouseHangzhouV3Detail.setUnitNo(getDocumentTableCellByRowAndColown(cells, rowIndex, 0).getContent());
                    newHouseHangzhouV3Detail.setRoomNo(getDocumentTableCellByRowAndColown(cells, rowIndex, 1).getContent());
                    newHouseHangzhouV3Detail.setBuildArea(getDocumentTableCellByRowAndColown(cells, rowIndex, 2).getContent());
                    newHouseHangzhouV3Detail.setInnerArea(getDocumentTableCellByRowAndColown(cells, rowIndex, 3).getContent());
                    newHouseHangzhouV3Detail.setAreaRate(getDocumentTableCellByRowAndColown(cells, rowIndex, 4).getContent());
                    newHouseHangzhouV3Detail.setUnitPrice(getDocumentTableCellByRowAndColown(cells, rowIndex, 5).getContent());
                    newHouseHangzhouV3Detail.setTotalPrice(getDocumentTableCellByRowAndColown(cells, rowIndex, 6).getContent());
                    String statusStr = getDocumentTableCellByRowAndColown(cells, rowIndex, 7).getContent().replaceAll("[^一-龥]", "");
                    newHouseHangzhouV3Detail.setStatus(STATUS_MAP.get(statusStr));
                    newHouseHangzhouV3Details.add(newHouseHangzhouV3Detail);
                }
                System.out.println(newHouseHangzhouV3Details.size());
            }

        }
        if (!CollectionUtils.isEmpty(newHouseHangzhouV3Details)) {
            newHouseHangzhouV3DetailMapper.insertBatch(newHouseHangzhouV3Details);
        }
    }

    private DocumentTableCell getDocumentTableCellByRowAndColown(List<DocumentTableCell> cells, int rowIndex, int colownIndex) {
        Optional<DocumentTableCell> documentTableCell = cells.stream().filter(cell -> cell.getRowIndex() == rowIndex && cell.getColumnIndex() == colownIndex)
                .findFirst();
        return documentTableCell.orElse(null);
    }


    public static Map<String, String> extractCookies(HttpResponse<byte[]> response) {
        Map<String, String> cookies = new HashMap<>();
        List<String> setCookies = response.headers().allValues("Set-Cookie");
        for (String cookie : setCookies) {
            String[] cookieParts = cookie.split(";")[0].split("=", 2);
            if (cookieParts.length == 2) {
                cookies.put(cookieParts[0], cookieParts[1]);
            }
        }
        return cookies;
    }

    @SneakyThrows
    private static void saveBase64ToPNG(String base64String, String path) {
        String base64Image = base64String.split(",")[1];
        byte[] imageBytes = Base64.getDecoder().decode(base64Image);
        Path outputPath = Paths.get(path);
        Files.createDirectories(outputPath.getParent());
        FileOutputStream fos = new FileOutputStream(outputPath.toFile());
        fos.write(imageBytes);
    }
}
