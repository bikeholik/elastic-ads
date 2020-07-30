package com.github.bikeholik.ads.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.BatchConfigurer;
import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@EnableBatchProcessing
@Configuration
@Slf4j
public class InfrastructureComponentsConfig {

    public static final String BEAN_NAME_INDEX_MANAGER = "elasticsearchIndexManager";

    @Bean
    public BatchConfigurer batchConfigurer() {
        return new DefaultBatchConfigurer() {
            @Override
            public void setDataSource(DataSource dataSource) {
                log.info("Using in-memory components");
            }
        };
    }
}
