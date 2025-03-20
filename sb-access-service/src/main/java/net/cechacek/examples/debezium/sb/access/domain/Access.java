package net.cechacek.examples.debezium.sb.access.domain;

import net.cechacek.examples.debezium.sb.access.AccessEntity;

public record Access(
        String user,
        String service,
        AccessLevel level) {

    public static Access fromEntity(AccessEntity entity) {
        return new Access(entity.getUser(), entity.getService(), entity.getLevel());
    }
}
