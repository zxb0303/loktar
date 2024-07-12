package com.loktar.conf;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;


@Configuration
public class RestTemplateConfig {
    @Bean
    public RestTemplate restTemplate() {
        // 创建一个 HttpMessageConverter 列表
        List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
        // 添加 StringHttpMessageConverter，使用 UTF-8 字符集
        messageConverters.add(new StringHttpMessageConverter(Charset.forName("UTF-8")));
        // 添加 FormHttpMessageConverter，用于处理表单数据
        messageConverters.add(new FormHttpMessageConverter());
        // 添加 ByteArrayHttpMessageConverter，用于处理字节数组
        messageConverters.add(new ByteArrayHttpMessageConverter());
        // 创建并配置 MappingJackson2HttpMessageConverter
        MappingJackson2HttpMessageConverter jackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
        ObjectMapper objectMapper = jackson2HttpMessageConverter.getObjectMapper();
        // 配置 ObjectMapper 允许未加引号的控制字符
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
        // 配置 ObjectMapper 使用 SNAKE_CASE 命名策略
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        // 将配置好的 MappingJackson2HttpMessageConverter 添加到列表中
        messageConverters.add(jackson2HttpMessageConverter);
        // 创建 RestTemplate 实例，并设置消息转换器列表
        RestTemplate template = new RestTemplate(messageConverters);
        return template;
    }
}
