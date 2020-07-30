package com.github.bikeholik.ads;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class ElasticAdsApplication {

    public static void main(String[] args) {
        SpringApplication.run(ElasticAdsApplication.class, args);
    }

}
