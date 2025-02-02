package com.ody.route.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(uniqueConstraints = {
        @UniqueConstraint(
                name = "uniqueClientTypeAndDate",
                columnNames = {"client_type", "date"}
        )
})
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ApiCall {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Enumerated(value = EnumType.STRING)
    private ClientType clientType;

    @NotNull
    private Integer count;

    @NotNull
    private LocalDate date;

    private Boolean enabled;

    public ApiCall(ClientType clientType, Integer count, LocalDate date, Boolean enabled) {
        this(null, clientType, count, date, enabled);
    }

    public ApiCall(ClientType clientType, Integer count, LocalDate date) {
        this(null, clientType, count, date, null);
    }

    public void increaseCount() {
        count++;
    }

    public void updateEnabled() {
        if (enabled == null) {
            enabled = true;
        }
        enabled = !enabled;
    }

    public boolean getEnabled() {
        return enabled == null || enabled;
    }
}
