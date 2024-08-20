package com.ody.member.domain;

import com.ody.common.exception.OdyUnauthorizedException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DeviceToken {

    @Column(name = "deviceToken", unique = true, nullable = false)
    @NotBlank
    private String value;

    public DeviceToken(String value) {
        this.value = value;
    }
}
