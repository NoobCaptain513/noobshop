package com.app.noobshop.infrastructure.redis.properties.config;


import com.app.noobshop.infrastructure.redis.config.YamlPropertySourceFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource(value = "classpath:redisCache.yml" , encoding = "UTF-8", factory = YamlPropertySourceFactory.class)
public class RedisCacheConfig {
}
