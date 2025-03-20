package net.cechacek.examples.debezium.sb.access;

import jakarta.persistence.*;
import lombok.*;
import net.cechacek.examples.debezium.sb.access.domain.AccessLevel;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@With
@Entity
@Table(name = "access", uniqueConstraints = @UniqueConstraint(columnNames = {"username", "service"}))
public class AccessEntity {
    @Id
    @SequenceGenerator(name = "access_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "access_id_seq")
    private Long id;
    @Column(name = "username", nullable = false, updatable = false)
    private String user;
    @Column(nullable = false, updatable = false)
    private String service;
    @Enumerated(EnumType.STRING)
    private AccessLevel level;
}
