package net.cechacek.examples.debezium.sb.access;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccessRepository extends JpaRepository<AccessEntity, Long> {
    void deleteByUserAndService(String user, String service);
    Optional<AccessEntity> findByUserAndService(String user, String service);
}
