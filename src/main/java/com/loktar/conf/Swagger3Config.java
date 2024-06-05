package com.loktar.conf;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;


@Configuration
public class Swagger3Config {
    private final LokTarConfig lokTarConfig;

    public Swagger3Config(LokTarConfig lokTarConfig) {
        this.lokTarConfig = lokTarConfig;
    }


    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Swagger3 API"))
                .servers(List.of(
                        new Server().url(lokTarConfig.getCommon().getLoktarUrl()).description("Server Url")
                ));
    }
}
