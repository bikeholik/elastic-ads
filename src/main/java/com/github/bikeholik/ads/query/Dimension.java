package com.github.bikeholik.ads.query;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;

import java.util.function.Supplier;

@RequiredArgsConstructor
@Getter
enum Dimension implements Supplier<AggregationBuilder> {
    DATASOURCE(dimension("datasource")), CAMPAIGN(dimension("campaign"));

    private final String fieldName;

    @Override
    public AggregationBuilder get() {
        return AggregationBuilders.terms(name().toLowerCase())
                .field(fieldName);
    }

    private static String dimension(String fieldName) {
        return "dimension." + fieldName;
    }
}
