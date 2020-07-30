package com.github.bikeholik.ads.batch;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RequiredArgsConstructor
public class JacksonFieldSetMapper<T> implements FieldSetMapper<T> {
    private final ObjectMapper objectMapper;
    private final Class<T> type;

    @Override
    public T mapFieldSet(FieldSet fieldSet) throws BindException {
        return objectMapper.convertValue(toMap(fieldSet), type);
    }

    private Map<String, Object> toMap(FieldSet fieldSet) {
        return IntStream.range(0, fieldSet.getFieldCount())
                .boxed()
                .collect(Collectors.toMap(i -> fieldSet.getNames()[i], i -> fieldSet.getValues()[i]));
    }
}
