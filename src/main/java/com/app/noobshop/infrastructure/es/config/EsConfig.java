package com.app.noobshop.infrastructure.es.config;

import lombok.RequiredArgsConstructor;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class EsConfig {

    private final EsProperties esProperties;

    @Bean(destroyMethod = "close")
    public RestHighLevelClient restHighLevelClient() {
        String uris = esProperties.getUris();
        String address = uris.replace("http://", "").replace("https://", "");
        String[] parts = address.split(":");
        String host = parts[0];
        int port = Integer.parseInt(parts[1]);
        return new RestHighLevelClient(RestClient.builder(new HttpHost(host, port, "http")));
    }
}
