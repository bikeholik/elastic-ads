package com.github.bikeholik.ads.query;

import lombok.RequiredArgsConstructor;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;

import java.util.function.Supplier;

@RequiredArgsConstructor
enum Resolution implements Supplier<AggregationBuilder> {
    DAY(DateHistogramInterval.DAY),
    WEEK(DateHistogramInterval.WEEK),
    MONTH(DateHistogramInterval.MONTH);

    private final DateHistogramInterval internal;

    @Override
    public AggregationBuilder get() {
        return AggregationBuilders.dateHistogram("date")
                .field("timestamp")
                .calendarInterval(internal);
    }
}
