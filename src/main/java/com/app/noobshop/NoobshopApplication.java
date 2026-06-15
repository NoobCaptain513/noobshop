package com.app.noobshop;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
@MapperScan("com.app.noobshop.mapper")
@EnableAspectJAutoProxy(exposeProxy = true)
@EnableScheduling
@EnableAsync
public class NoobshopApplication {

    public static void main(String[] args) {
        SpringApplication.run(NoobshopApplication.class, args);
    }

}
