package com.ody.route.mapper;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.ody.route.dto.OdsayResponse;
import java.io.IOException;
import java.util.Optional;
import java.util.OptionalLong;
import org.springframework.boot.jackson.JsonComponent;

@JsonComponent
public class OdsayResponseDeserializer extends JsonDeserializer<OdsayResponse> {

    @Override
    public OdsayResponse deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) {
        try {
            JsonNode node = jsonParser.getCodec()
                    .readTree(jsonParser);
            return parse(node);
        } catch (IOException exception) {
            throw new RuntimeException(exception.getMessage());
        }
    }

    private OdsayResponse parse(JsonNode node) {
        Optional<String> code = find(node, "code");
        Optional<String> message = find(node, "message", "msg");
        OptionalLong minutes = findMinutes(node);
        return new OdsayResponse(code, message, minutes);
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
        } catch (NullPointerException exception) {
            return OptionalLong.empty();
        }
    }
}
