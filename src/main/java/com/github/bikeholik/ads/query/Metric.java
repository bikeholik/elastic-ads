package com.github.bikeholik.ads.query;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.elasticsearch.script.Script;
import org.elasticsearch.search.aggregations.*;

import java.util.Collections;
import java.util.Set;
import java.util.function.Consumer;

@RequiredArgsConstructor
@Getter
enum Metric implements Consumer<AggregationBuilder> {
    CLICKS(AggregationBuilders.sum("totalClicks")
            .field(metric("clicks"))),
    IMPRESSIONS(AggregationBuilders.sum("totalViews")
            .field(metric("impressions"))),
    CTR(PipelineAggregatorBuilders.bucketScript("ctr",
            ImmutableMap.of("clicks", "totalClicks", "views", "totalViews"),
            new Script("params.clicks / params.views * 100"))) {
        @Override
        public Set<Metric> getIngredientMetrics() {
            return Sets.newHashSet(CLICKS, IMPRESSIONS);
        }
    };

    private static String metric(String fieldName) {
        return "metric." + fieldName;
    }

    private final BaseAggregationBuilder aggregationBuilder;

    public Set<Metric> getIngredientMetrics() {
        return Collections.emptySet();
    }

    @Override
    public void accept(AggregationBuilder builder) {
        if (aggregationBuilder instanceof AggregationBuilder) {
            builder.subAggregation((AggregationBuilder) aggregationBuilder);
        } else {
            builder.subAggregation((PipelineAggregationBuilder) aggregationBuilder);
        }
    }

    String getAggregationName() {
        if (aggregationBuilder instanceof AggregationBuilder) {
            return ((AggregationBuilder) aggregationBuilder).getName();
        } else {
            return ((PipelineAggregationBuilder) aggregationBuilder).getName();
        }
    }
}
