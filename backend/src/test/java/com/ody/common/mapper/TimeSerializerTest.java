package com.ody.common.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

@JsonTest
class TimeSerializerTest {

    @Autowired
    private JacksonTester<LocalTime> localTimeSerializer;

    @DisplayName("LocalTime은 나노초를 절삭하여 직렬화된다")
    @Test
    public void testSerialize() throws Exception {
        LocalTime now = LocalTime.now();
        JsonContent<LocalTime> result = this.localTimeSerializer.write(now);
        assertThat(result).extractingJsonPathStringValue("$")
                .isEqualTo(now.withSecond(0).withNano(0).toString());
    }
}
