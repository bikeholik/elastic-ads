package com.github.bikeholik.ads.query;

import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.MultiBucketsAggregation;
import org.elasticsearch.search.aggregations.metrics.NumericMetricsAggregation;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ResultsCollectorTest {

    @Test
    void getResults() {
        ResultsCollector collector = new ResultsCollector(Arrays.asList(
                aggregation("x", 1), aggregation("y", 10)),
                Collections.singleton(Metric.CLICKS));

        List<Result> results = collector.getResults();

        assertThat(results).hasSize(2);
    }

    private Aggregation aggregation(String datasource, int clicks) {
        return new MultiBucketsAggregation() {
            @Override
            public List<? extends Bucket> getBuckets() {
                return Collections.singletonList(new Bucket() {
                    @Override
                    public Object getKey() {
                        return datasource;
                    }

                    @Override
                    public String getKeyAsString() {
                        return datasource;
                    }

                    @Override
                    public long getDocCount() {
                        return 0;
                    }

                    @Override
                    public Aggregations getAggregations() {
                        return new Aggregations(Collections.singletonList(new NumericMetricsAggregation.SingleValue() {
                            @Override
                            public double value() {
                                return clicks;
                            }

                            @Override
                            public String getValueAsString() {
                                return String.valueOf(clicks);
                            }

                            @Override
                            public String getName() {
                                return Metric.CLICKS.getAggregationName();
                            }

                            @Override
                            public String getType() {
                                return null;
                            }

                            @Override
                            public Map<String, Object> getMetaData() {
                                return null;
                            }

                            @Override
                            public XContentBuilder toXContent(XContentBuilder builder, Params params) throws IOException {
                                return null;
                            }
                        }));
                    }

                    @Override
                    public XContentBuilder toXContent(XContentBuilder builder, Params params) throws IOException {
                        return null;
                    }
                });
            }

            @Override
            public String getName() {
                return "datasource";
            }

            @Override
            public String getType() {
                return null;
            }

            @Override
            public Map<String, Object> getMetaData() {
                return null;
            }

            @Override
            public XContentBuilder toXContent(XContentBuilder builder, Params params) throws IOException {
                return null;
            }
        };
    }
}