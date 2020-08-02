package com.github.bikeholik.ads.query;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
@Slf4j
class ElasticsearchQueryProcessor {
    private final RestHighLevelClient client;
    private final ElasticsearchQueryBuilder builder;

    @SneakyThrows
    public List<Result> execute(Query query) {
        SearchSourceBuilder searchSourceBuilder = builder.buildQuery(query);
        log.debug("Query to execute {}", searchSourceBuilder);
        SearchResponse response = client.search(
                new SearchRequest("ad-stats")
                        .source(searchSourceBuilder),
                RequestOptions.DEFAULT);
        log.debug("Query result: {}", response);
        return new ResultsCollector(response.getAggregations().asList(), query.getMetrics()).getResults();
    }
}
