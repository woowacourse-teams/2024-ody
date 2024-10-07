package com.ody.common.domain;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
public class BaseEntity {

    @Column(updatable = false, columnDefinition = "TIMESTAMP(6)")
    @NotNull
    @CreatedDate()
    private LocalDateTime createdAt;

    @Column(columnDefinition = "TIMESTAMP(6)")
    @NotNull
    @LastModifiedDate
    private LocalDateTime updatedAt;
}
