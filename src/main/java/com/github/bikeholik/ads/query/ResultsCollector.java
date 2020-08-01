package com.github.bikeholik.ads.query;

import lombok.Getter;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.bucket.MultiBucketsAggregation;
import org.elasticsearch.search.aggregations.metrics.NumericMetricsAggregation;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

class ResultsCollector {
    static final String VIRTUAL_TOP_LEVEL_AGG = "top_level_agg";
    private final Result.ResultBuilder resultBuilder = Result.builder();
    @Getter
    private final List<Result> results = new LinkedList<>();

    public ResultsCollector(List<Aggregation> aggregations, Set<Metric> metrics) {
        Set<String> expectedMetricAggregations = metrics.stream().map(Metric::getAggregationName).collect(Collectors.toSet());
        aggregations.forEach(aggregation -> collect(aggregation, expectedMetricAggregations));
    }

    private void collect(Aggregation aggregation, Set<String> expectedMetricAggregations) {
        if (aggregation instanceof MultiBucketsAggregation) {
            ((MultiBucketsAggregation) aggregation).getBuckets().forEach(bucket -> {
                if (!Objects.equals(aggregation.getName(), VIRTUAL_TOP_LEVEL_AGG)) {
                    resultBuilder.item(aggregation.getName(), bucket.getKeyAsString());
                }
                List<Aggregation> subAggregations = bucket.getAggregations().asList();
                if (subAggregations.stream().allMatch(aggs -> aggs instanceof NumericMetricsAggregation.SingleValue)) {
                    subAggregations
                            .stream()
                            .filter(subAggregation -> expectedMetricAggregations.contains(subAggregation.getName()))
                            .forEach(subAggregation -> resultBuilder.item(
                                    subAggregation.getName(),
                                    ((NumericMetricsAggregation.SingleValue) subAggregation).value()));
                    results.add(resultBuilder.build());
                } else {
                    subAggregations.forEach(subAggregation -> collect(subAggregation, expectedMetricAggregations));
                }
            });
        }
    }
}
