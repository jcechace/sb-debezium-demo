package net.cechacek.examples.debezium.sb.access.dto;

import jakarta.validation.constraints.NotNull;
import net.cechacek.examples.debezium.sb.access.domain.AccessLevel;

public record AccessRequest(
        @NotNull AccessLevel level
) {
}