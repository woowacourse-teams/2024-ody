package com.ody.route.mapper;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.ody.route.domain.Duration;
import java.io.IOException;
import org.springframework.boot.jackson.JsonComponent;

@JsonComponent
public class CustomDurationDeserializer extends JsonDeserializer<Duration> {

    @Override
    public Duration deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) {
        JsonNode node = null;
        try {
            node = jsonParser.getCodec()
                    .readTree(jsonParser);
            return parse(node);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Duration parse(JsonNode node) {
        try {
            return new Duration(
                    node.get("result")
                            .get("path")
                            .get(0)
                            .get("info")
                            .get("totalTime")
                            .asInt()
            );
        } catch (NullPointerException e) {
            throw new RuntimeException("node가 null 이거나, json 형식이 일치하지 않습니다.");
        }
    }
}
