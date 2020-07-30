package com.github.bikeholik.ads.batch;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.DefaultFieldSet;
import org.springframework.validation.BindException;

import static org.assertj.core.api.Assertions.assertThat;

public class JacksonFieldSetMapperTest {

    private final FieldSetMapper<Wrapper> mapper = new JacksonFieldSetMapper<>(new ObjectMapper(), Wrapper.class);

    @Test
    public void mapFieldSet() throws BindException {
        Wrapper wrapper = mapper.mapFieldSet(new DefaultFieldSet(new String[]{"x", "y"}, new String[]{"test", "other"}));

        assertThat(wrapper.getTest()).isEqualTo("x");
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @RequiredArgsConstructor
    @Getter
    static class Wrapper {
        private final String test;
    }
}