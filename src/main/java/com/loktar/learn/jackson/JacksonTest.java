package com.loktar.learn.jackson;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.loktar.domain.github.GithubRepository;
import lombok.SneakyThrows;

import java.util.List;

public class JacksonTest {
    public static void main(String[] args) throws Exception {
        //常见示例
        //test1();
        //创建json对象、属性有数组
        //test2();
        //jsonstring转list对象
        //List<GithubRelease> githubReleases = objectMapper.readValue(responseBody, new TypeReference<List<GithubRelease>>(){});
        //jsonstring转jsonobject对象
        //IPUtil.getip();
        //java对象转json字符串 json字符串转java对象
        //test3();
        //序列化反序列化策略
        test4();
        //忽略字段
        //objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        //过滤null属性
        //objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)
        // 详解
        // https://developer.aliyun.com/article/862151

    }

    @SneakyThrows
    private static void test4() {
        GithubRepository githubRepository = new GithubRepository();
        githubRepository.setRepositoryId(1);
        githubRepository.setLastTagId(2);
        githubRepository.setRepository("sss");

        // 驼峰命名，字段的首字母小写. {"animalName":"sam","animalSex":1,"animalWeight":100}
        ObjectMapper mapper1 = new ObjectMapper();
        mapper1.setPropertyNamingStrategy(PropertyNamingStrategies.LOWER_CAMEL_CASE);
        System.out.println(mapper1.writeValueAsString(githubRepository));

        // 驼峰命名，字段的首字母大写. {"AnimalName":"sam","AnimalSex":1,"AnimalWeight":100}
        ObjectMapper mapper2 = new ObjectMapper();
        mapper2.setPropertyNamingStrategy(PropertyNamingStrategies.UPPER_CAMEL_CASE);
        System.out.println(mapper2.writeValueAsString(githubRepository));

        // 字段小写，多个单词以下划线_分隔. {"animal_name":"sam","animal_sex":1,"animal_weight":100}
        ObjectMapper mapper3 = new ObjectMapper();
        mapper3.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        System.out.println(mapper3.writeValueAsString(githubRepository));

        // 字段小写，多个单词以中横线-分隔. {"animal-name":"sam","animal-sex":1,"animal-weight":100}
        ObjectMapper mapper4 = new ObjectMapper();
        mapper4.setPropertyNamingStrategy(PropertyNamingStrategies.KEBAB_CASE);
        System.out.println(mapper4.writeValueAsString(githubRepository));

        // 字段小写，多个单词间无分隔符. {"animalname":"sam","animalsex":1,"animalweight":100}
        ObjectMapper mapper5 = new ObjectMapper();
        mapper5.setPropertyNamingStrategy(PropertyNamingStrategies.LOWER_CASE);
        System.out.println(mapper5.writeValueAsString(githubRepository));

        // 字段小写，多个单词以点号.分隔. {"animal.name":"sam","animal.sex":1,"animal.weight":100}
        ObjectMapper mapper6 = new ObjectMapper();
        mapper6.setPropertyNamingStrategy(PropertyNamingStrategies.LOWER_DOT_CASE);
        System.out.println(mapper6.writeValueAsString(githubRepository));

        // 字段大写，多个单词以下划线_分隔. {"ANIMAL_NAME":"sam","ANIMAL_SEX":1,"ANIMAL_WEIGHT":100}
        ObjectMapper mapper7 = new ObjectMapper();
        mapper7.setPropertyNamingStrategy(PropertyNamingStrategies.UPPER_SNAKE_CASE);
        System.out.println(mapper7.writeValueAsString(githubRepository));
    }

    @SneakyThrows
    private static void test3() {
        // java对象转JSON字符串
        GithubRepository githubRepository = new GithubRepository();
        githubRepository.setRepositoryId(1);
        githubRepository.setLastTagId(2);
        githubRepository.setRepository("1111");
        String githubRepositoryStr = new ObjectMapper().writeValueAsString(githubRepository);
        System.out.println(githubRepositoryStr);
        //JSON字符串转java对象
        GithubRepository newGithubRepository2 = new ObjectMapper().readValue(githubRepositoryStr, GithubRepository.class);
    }

    private static void test2() {
        //添加对象
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode obj = objectMapper.createObjectNode();
        ObjectNode arguments = objectMapper.createObjectNode();
        ArrayNode idsArray = objectMapper.createArrayNode();
        Integer[] ids = new Integer[]{1, 2, 3};
        for (Integer id : ids) {
            idsArray.add(id);
        }
        obj.put("method", "torrent-remove");
        arguments.put("delete-local-data", false);
        arguments.set("ids", idsArray);
        obj.set("arguments", arguments);
        System.out.println(obj.toString());
    }

    @SneakyThrows
    private static void test1() {

        ObjectMapper objectMapper = new ObjectMapper();
        //json对象创建
        ObjectNode jsonObject1 = objectMapper.createObjectNode();
        jsonObject1.put("姓名", "张三");
        jsonObject1.put("年龄", "18");
        jsonObject1.put("地理", 70);
        System.out.println(jsonObject1);
        //json数组创建1
        ArrayNode jsonArray1 = objectMapper.createArrayNode();
        ObjectNode jsonObject2 = objectMapper.createObjectNode();
        jsonObject2.put("姓名", "李四");
        jsonObject2.put("年龄", "19");
        jsonObject2.put("地理", 80);
        jsonArray1.add(jsonObject1);
        jsonArray1.add(jsonObject2);
        System.out.println(jsonArray1);
        //json数组创建2
        ArrayNode jsonArray2 = objectMapper.createArrayNode();
        jsonArray2.add("张三");
        jsonArray2.add("李四");
        System.out.println(jsonArray2);
        //json对象取值
        String name1 = jsonObject1.get("姓名").asText();
        String age1 = jsonObject1.get("年龄").asText();
        System.out.println(name1);
        System.out.println(age1);
        JsonNode obj = jsonArray1.get(0);
        String name2 = obj.get("姓名").asText();
        System.out.println(name2);
        //遍历获取json数组中对象的值
        for (JsonNode j : jsonArray1) {
            System.out.println(j.get("姓名").asText());
        }
        //JSON 对象转字符串
        String str = jsonObject1.toString();

        //字符串转 JSON 对象
        ObjectNode jsonObjectNew = (ObjectNode) objectMapper.readTree(str);

        //json数组与字符串的转换
        String str1 = "[\"张三\",\"李四\",\"王五\"]";
        ObjectMapper mapper = new ObjectMapper();
        //字符串转json数组
        ArrayNode jsonArray = (ArrayNode)mapper.readTree(str1);
        //json数组转字符串
        String s = jsonArray.toString();
        System.out.println(s);
        //json字符串数组转List
        List<String> list = mapper.readValue(str1, new TypeReference<List<String>>(){});

        //json字符串与java对象的转换
        GithubRepository githubRepository = new GithubRepository();
        githubRepository.setRepositoryId(1);
        githubRepository.setLastTagId(2);
        githubRepository.setRepository("1111");

        //java对象转JSON字符串
        String githubRepositoryStr = new ObjectMapper().writeValueAsString(githubRepository);

        System.out.println(githubRepositoryStr);
        //JSON字符串转java对象
        GithubRepository newGithubRepository = mapper.readValue(githubRepositoryStr, GithubRepository.class);
        GithubRepository newGithubRepository2 = new ObjectMapper().readValue(githubRepositoryStr, GithubRepository.class);

        System.out.println(newGithubRepository.toString());
        System.out.println(newGithubRepository2.toString());
    }
}
