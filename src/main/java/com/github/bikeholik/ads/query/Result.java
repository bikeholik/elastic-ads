package com.github.bikeholik.ads.query;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

import java.util.Map;

@Builder
class Result {
    @Getter(onMethod_ = @JsonAnyGetter)
    @Singular
    private final Map<String, Object> items;
}
