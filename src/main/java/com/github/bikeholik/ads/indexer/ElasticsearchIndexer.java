package com.github.bikeholik.ads.indexer;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Set;

@RequiredArgsConstructor
@Component
@Slf4j
class ElasticsearchIndexer implements Indexer {
    private final RestHighLevelClient client;
    private final ObjectMapper objectMapper;
    private final Validator validator;
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy");

    @Override
    @SneakyThrows
    public <M, D> void index(Collection<DataSample<M, D>> data) {
        log.info("Index {} samples", data.size());
        BulkRequest bulkRequest = data.stream()
                .filter(this::isValid)
                .map(this::toIndexRequest)
                .collect(BulkRequest::new, BulkRequest::add, (bulk1, bulk2) -> bulk1.add(bulk2.requests()));
        client.bulk(bulkRequest, RequestOptions.DEFAULT);
    }

    private <M, D> IndexRequest toIndexRequest(DataSample<M, D> data) {
        return new IndexRequest(getIndex(data))
                .id(data.getSampleId())
                .source(toJson(data), XContentType.JSON);
    }

    @SneakyThrows
    private <M, D> String toJson(DataSample<M, D> data) {
        return objectMapper.writeValueAsString(data);
    }

    private <M, D> boolean isValid(DataSample<M, D> data) {
        Set<ConstraintViolation<DataSample<M, D>>> constraintViolations = validator.validate(data);
        if (!constraintViolations.isEmpty()) {
            log.warn("Data invalid. Errors: {}", constraintViolations);
            return false;
        } else {
            return true;
        }
    }

    private String getIndex(DataSample<?, ?> data) {
        return "ad-stats-" + dateTimeFormatter.format(data.getTimestamp());
    }
}
