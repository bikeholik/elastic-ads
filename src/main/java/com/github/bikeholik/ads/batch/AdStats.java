package com.github.bikeholik.ads.batch;

import com.github.bikeholik.ads.indexer.DataSample;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@RequiredArgsConstructor
@Getter
class AdStats {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yy");
    private final String datasource;
    private final String campaign;
    private final String date;
    private final long clicks;
    private final long impressions;

    DataSample<Metrics, Dimensions> toDataSample() {
        return DataSample.<Metrics, Dimensions>builder()
                .timestamp(LocalDate.parse(date, DATE_TIME_FORMATTER))
                .sampleId(DigestUtils.sha1Hex(date + campaign + datasource))
                .dimension(Dimensions.of(datasource, campaign))
                .metric(Metrics.of(clicks, impressions))
                .build();
    }

    @RequiredArgsConstructor(staticName = "of")
    @Getter
    static class Dimensions {
        private final String datasource;
        private final String campaign;
    }

    @RequiredArgsConstructor(staticName = "of")
    @Getter
    static class Metrics {
        private final long clicks;
        private final long impressions;
    }
}
