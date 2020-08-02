package com.github.bikeholik.ads.batch;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.bikeholik.ads.config.InfrastructureComponentsConfig;
import com.github.bikeholik.ads.indexer.Indexer;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.LineTokenizer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.io.UrlResource;

import java.net.MalformedURLException;
import java.util.stream.Collectors;

@Configuration
@EnableConfigurationProperties(ImportProperties.class)
@ConditionalOnProperty(value = "import.enabled", matchIfMissing = true)
class ImportConfig {

    @Bean
    public ItemReader<AdStats> itemReader(ImportProperties importProperties, ObjectMapper objectMapper) throws MalformedURLException {
        FlatFileItemReader<AdStats> csvFileReader = new FlatFileItemReader<>();
        csvFileReader.setResource(new UrlResource(importProperties.getUrl()));
        csvFileReader.setLinesToSkip(1);
        csvFileReader.setLineMapper(statsLineMapper(objectMapper));
        csvFileReader.setMaxItemCount(importProperties.getMaxItemsCount());
        return csvFileReader;
    }

    @Bean
    public ItemWriter<AdStats> itemWriter(Indexer indexer) {
        return items -> indexer.index(items.stream()
                .map(AdStats::toDataSample)
                .collect(Collectors.toList()));
    }

    @Bean
    public Step importJobStep(ItemReader<AdStats> reader,
                              ItemWriter<AdStats> writer,
                              StepBuilderFactory stepBuilderFactory,
                              ImportProperties importProperties) {
        return stepBuilderFactory.get("importJobStep")
                .<AdStats, AdStats>chunk(importProperties.getChunkSize())
                .reader(reader)
                .writer(writer)
                .build();
    }

    @Bean
    @DependsOn(InfrastructureComponentsConfig.BEAN_NAME_INDEX_MANAGER)
    public Job importJob(Step importJobStep,
                         JobBuilderFactory jobBuilderFactory) {
        return jobBuilderFactory.get("importJob")
                .incrementer(new RunIdIncrementer())
                .flow(importJobStep)
                .end()
                .build();
    }

    private LineMapper<AdStats> statsLineMapper(ObjectMapper objectMapper) {
        DefaultLineMapper<AdStats> mapper = new DefaultLineMapper<>();
        mapper.setLineTokenizer(adsLineTokenizer());
        mapper.setFieldSetMapper(new JacksonFieldSetMapper<>(objectMapper, AdStats.class));
        return mapper;
    }

    private LineTokenizer adsLineTokenizer() {
        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
        tokenizer.setDelimiter(",");
        tokenizer.setNames("datasource", "campaign", "date", "clicks", "impressions");
        return tokenizer;
    }
}
