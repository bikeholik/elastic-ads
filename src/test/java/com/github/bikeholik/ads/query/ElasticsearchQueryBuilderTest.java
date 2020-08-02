package com.github.bikeholik.ads.query;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import static org.assertj.core.api.Assertions.assertThat;

class ElasticsearchQueryBuilderTest {

    private final ElasticsearchQueryBuilder builder = new ElasticsearchQueryBuilder();

    @Test
    void buildQueryOnlyCtr() {
        SearchSourceBuilder searchSourceBuilder = builder.buildQuery(new Query(null, null, Collections.singleton(Metric.CTR)));

        assertThat(searchSourceBuilder.aggregations().count()).isEqualTo(1);
        AggregationBuilder aggregationBuilder = searchSourceBuilder.aggregations().getAggregatorFactories().iterator().next();
        assertThat(aggregationBuilder.getSubAggregations()).hasSize(2);
        assertThat(aggregationBuilder.getPipelineAggregations()).hasSize(1);
    }

    @Test
    void buildQueryDimensionMetricAndFilter() {
        SearchSourceBuilder searchSourceBuilder = builder.buildQuery(new Query(
                new Query.Aggregations(Collections.emptyList(), Resolution.MONTH),
                new Query.Filters(Collections.singletonMap(Dimension.CAMPAIGN, Collections.singleton("test")), null),
                new HashSet<>(Arrays.asList(Metric.CTR, Metric.CLICKS))));

        assertThat(searchSourceBuilder.aggregations().count()).isEqualTo(1);
        assertThat(searchSourceBuilder.query()).isInstanceOf(BoolQueryBuilder.class);
    }
}