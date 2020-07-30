package com.github.bikeholik.ads.indexer;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Builder
@Getter
public class DataSample<M, D> {
    @JsonIgnore
    private final String sampleId;
    @NotNull
    private final LocalDate timestamp;
    private final M metric;
    private final D dimension;
}
