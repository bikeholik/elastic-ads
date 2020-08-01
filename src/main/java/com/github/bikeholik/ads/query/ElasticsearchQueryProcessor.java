package com.github.bikeholik.ads.query;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchAllQueryBuilder;
import org.elasticsearch.index.query.TermsQueryBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Component
@Slf4j
class ElasticsearchQueryProcessor {
    private final RestHighLevelClient client;

    @SneakyThrows
    public List<Result> execute(Query query) {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder().size(0);
        getQuery(query)
                .ifPresent(searchSourceBuilder::query);
        searchSourceBuilder.aggregation(buildAggregations(query));
        log.debug("Query to execute {}", searchSourceBuilder);
        SearchResponse response = client.search(
                new SearchRequest("ad-stats")
                        .source(searchSourceBuilder),
                RequestOptions.DEFAULT);
        log.debug("Query result: {}", response);
        return new ResultsCollector(response.getAggregations().asList(), query.getMetrics()).getResults();
    }

    private AggregationBuilder buildAggregations(Query query) {
        AggregationBuilderWrapper aggregationBuilderWrapper = Optional.ofNullable(query.getAggregations())
                .flatMap(filters -> Stream.concat(
                        Stream.ofNullable(filters.getTime()),
                        Stream.ofNullable(filters.getDimensions())
                                .flatMap(List::stream)
                                .distinct())
                        .map(Supplier::get)
                        .map(AggregationBuilderWrapper::new)
                        .reduce(AggregationBuilderWrapper::withSubAggregation))
                .orElseGet(() -> new AggregationBuilderWrapper(AggregationBuilders.filters(ResultsCollector.VIRTUAL_TOP_LEVEL_AGG, new MatchAllQueryBuilder())));

        query.getMetrics().stream()
                .flatMap(metric -> Stream.concat(Stream.of(metric), metric.getIngredientMetrics().stream()))
                .distinct()
                .forEach(metric -> metric.accept(aggregationBuilderWrapper.getLastAggregationBuilder()));
        return aggregationBuilderWrapper.getAggregationBuilder();
    }

    private Optional<BoolQueryBuilder> getQuery(Query query) {
        return Optional.ofNullable(query.getFilters())
                .map(filters -> Stream.concat(
                        Stream.ofNullable(filters.getRange()),
                        Stream.ofNullable(filters.getDimension())
                                .map(Map::entrySet)
                                .flatMap(Set::stream)
                                .map(entry -> DimensionFilter.of(entry.getKey(), entry.getValue())))
                        .collect(BoolQueryBuilder::new, (builder, visitor) -> visitor.accept(builder), BoolQueryBuilder::must));
    }

    @RequiredArgsConstructor(staticName = "of")
    private static class DimensionFilter implements Consumer<BoolQueryBuilder> {
        private final Dimension dimension;
        private final Set<String> values;

        @Override
        public void accept(BoolQueryBuilder builder) {
            builder.must(new TermsQueryBuilder(dimension.getFieldName(), values));
        }
    }

    @Getter
    private static class AggregationBuilderWrapper {
        private final AggregationBuilder aggregationBuilder;
        private AggregationBuilder lastAggregationBuilder;

        private AggregationBuilderWrapper(AggregationBuilder aggregationBuilder) {
            this.aggregationBuilder = aggregationBuilder;
            this.lastAggregationBuilder = aggregationBuilder;
        }

        public AggregationBuilderWrapper withSubAggregation(AggregationBuilderWrapper wrapper) {
            lastAggregationBuilder.subAggregation(wrapper.aggregationBuilder);
            lastAggregationBuilder = wrapper.aggregationBuilder;
            return this;
        }
    }

}
