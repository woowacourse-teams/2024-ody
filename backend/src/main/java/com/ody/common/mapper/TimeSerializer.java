package com.ody.common.mapper;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import org.springframework.boot.jackson.JsonComponent;

@JsonComponent
public class TimeSerializer extends JsonSerializer<LocalTime> {

    private static final DateTimeFormatter TIME_FORMATTER = new DateTimeFormatterBuilder()
            .appendPattern("HH:mm")
            .toFormatter()
            .withZone(ZoneId.of("Asia/Seoul"));

    @Override
    public void serialize(
            LocalTime time,
            JsonGenerator jsonGenerator,
            SerializerProvider serializerProvider
    ) {
        try {
            jsonGenerator.writeString(TIME_FORMATTER.format(time));
        } catch (IOException exception) {
            throw new RuntimeException("시간 변환 과정에서 문제가 발생했습니다.");
        }
    }
}

