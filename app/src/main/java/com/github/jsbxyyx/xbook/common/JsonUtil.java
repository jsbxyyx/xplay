package com.github.jsbxyyx.xbook.common;

import com.fasterxml.jackson.core.JsonFactoryBuilder;
import com.fasterxml.jackson.core.StreamReadConstraints;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;

import java.io.IOException;

/**
 * @author jsbxyyx
 */
public class JsonUtil {

    private static JsonMapper mapper = JsonMapper.builder(
                    new JsonFactoryBuilder()
                            .streamReadConstraints(
                                    StreamReadConstraints
                                            .builder()
                                            .maxStringLength(Integer.MAX_VALUE)
                                            .build()
                            )
                            .build()
            )
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .enable(MapperFeature.PROPAGATE_TRANSIENT_MARKER)
            .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
            .build();

    public static JsonMapper getMapper() {
        return mapper;
    }

    public static JsonNode readTree(String json) {
        try {
            return mapper.readTree(json);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T convertValue(Object fromValue, TypeReference<T> toValueTypeRef) {
        return mapper.convertValue(fromValue, toValueTypeRef);
    }

    public static <T> T fromJson(String content, Class<T> clazz) {
        try {
            return mapper.readValue(content, clazz);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String toJson(Object object) {
        try {
            return mapper.writeValueAsString(object);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
