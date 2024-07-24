package com.ody.route.mapper;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.ody.route.dto.CalculateDurationResponse;
import java.io.IOException;
import java.util.Optional;
import java.util.OptionalLong;
import org.springframework.boot.jackson.JsonComponent;

@JsonComponent
public class CalculateDurationResponseDeserializer extends JsonDeserializer<CalculateDurationResponse> {

    @Override
    public CalculateDurationResponse deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) {
        try {
            JsonNode node = jsonParser.getCodec()
                    .readTree(jsonParser);
            return parse(node);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private CalculateDurationResponse parse(JsonNode node) {
        Optional<String> code = find(node, "code");
        Optional<String> message = find(node, "message", "msg");
        OptionalLong minutes = findMinutes(node);
        return new CalculateDurationResponse(code, message, minutes);
    }

    private Optional<String> find(JsonNode node, String... fieldName) {
        for (String field : fieldName) {
            JsonNode nodeName = node.findPath(field);
            if (!nodeName.isMissingNode()) {
                return Optional.of(nodeName.textValue());
            }
        }
        return Optional.empty();
    }

    private OptionalLong findMinutes(JsonNode node) {
        try {
            long minutes = node.get("result")
                    .get("path")
                    .get(0)
                    .get("info")
                    .get("totalTime")
                    .asLong();
            return OptionalLong.of(minutes);
        } catch (NullPointerException e) {
            return OptionalLong.empty();
        }
    }
}
