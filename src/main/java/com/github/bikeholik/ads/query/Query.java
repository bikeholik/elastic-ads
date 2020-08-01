package com.github.bikeholik.ads.query;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.RangeQueryBuilder;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

@RequiredArgsConstructor
@ToString
@Getter
class Query {

    private final Aggregations aggregations;
    @Valid
    private final Filters filters;
    @NotEmpty
    private final Set<Metric> metrics;

    @RequiredArgsConstructor
    @ToString
    @Getter
    static class Aggregations {
        private final List<Dimension> dimensions;
        private final Resolution time;
    }

    @RequiredArgsConstructor
    @ToString
    @Getter
    static class Filters {
        private final Map<Dimension, Set<String>> dimension;
        @Valid
        private final Range range;
    }

    @RequiredArgsConstructor
    @ToString
    @Getter
    static class Range implements Consumer<BoolQueryBuilder> {
        @NotNull
        private final LocalDate from;
        @NotNull
        private final LocalDate to;

        @Override
        public void accept(BoolQueryBuilder builder) {
            builder.must(new RangeQueryBuilder("timestamp")
                    .from(from)
                    .to(to));
        }
    }
}
