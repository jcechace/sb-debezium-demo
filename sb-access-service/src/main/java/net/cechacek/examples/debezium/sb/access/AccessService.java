package net.cechacek.examples.debezium.sb.access;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import net.cechacek.examples.debezium.sb.access.domain.Access;
import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Propagation;
//import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service for managing user level to services.
 */
@RequiredArgsConstructor
@Service
@Transactional(Transactional.TxType.SUPPORTS)
//@Transactional(propagation = Propagation.SUPPORTS)
public class AccessService {

    private final AccessRepository accessRepository;

    /**
     * Find all level grants.
     * @return list of level grants
     */
    public List<Access> findAll() {
        return accessRepository.findAll()
                .stream()
                .map(Access::fromEntity)
                .toList();
    }


    public Optional<Access> findByUserAndService(String user, String service) {
        return accessRepository
                .findByUserAndService(user, service)
                .map(Access::fromEntity);
    }

    /**
     * Update level grant
     * @param access level grant
     * @return updated level grant
     */
    @Transactional(Transactional.TxType.REQUIRED)
//    @Transactional(propagation = Propagation.REQUIRED)
    public Optional<Access> update(Access access) {
        return accessRepository
                .findByUserAndService(access.user(), access.service())
                .map(entity -> {
                    entity.setLevel(access.level());
                    return entity;
                })
                //.map(accessRepository::save)
                .map(Access::fromEntity);
    }

    /**
     * Create level grant
     * @param access level grant
     * @return created level grant
     */
    @Transactional(Transactional.TxType.REQUIRED)
//    @Transactional(propagation = Propagation.REQUIRED)
    public Optional<Access> create(Access access) {
        var entity = new AccessEntity(null, access.user(), access.service(), access.level());

        return Optional.of(entity)
                .map(accessRepository::save)
                .map(Access::fromEntity);
    }

    /**
     * Delete level grant
     * @param user user
     * @param service service
     */
    @Transactional(Transactional.TxType.REQUIRED)
//    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteByUserAndService(String user, String service) {
        accessRepository.deleteByUserAndService(user, service);
    }
}
