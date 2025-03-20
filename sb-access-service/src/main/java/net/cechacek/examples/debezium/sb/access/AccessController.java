package net.cechacek.examples.debezium.sb.access;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.cechacek.examples.debezium.sb.access.dto.AccessRequest;
import net.cechacek.examples.debezium.sb.access.domain.Access;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

/**
 * Rest API for level management
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(value = AccessController.PATH)
public class AccessController {
    public static final String PATH = "/access";

    private final AccessService accessService;

    @GetMapping
    public List<Access> list() {
        return accessService.findAll();
    }


    @GetMapping(path = "/users/{user}/services/{service}")
    public ResponseEntity<Access> get(@PathVariable String user, @PathVariable String service) {
        return accessService
                .findByUserAndService(user, service)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping(path = "/users/{user}/services/{service}")
    public ResponseEntity<Access> put(@PathVariable String user, @PathVariable String service,
                                @RequestBody(required = true) @Valid AccessRequest request) {
        log.info("Received request to grant level to service {} for user {}", service, user);
        var access = new Access(user, service, request.level());

        return update(access)
                .or(() -> create(access))
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @DeleteMapping(path = "/users/{user}/services/{service}")
    public ResponseEntity<Void> delete(@PathVariable String user, @PathVariable String service) {
        log.info("Received request to revoke level to service {} for user {}", service, user);
        accessService.deleteByUserAndService(user, service);
        return ResponseEntity.noContent().build();
    }

    private Optional<ResponseEntity<Access>> update(Access access) {
        return accessService.update(access).map(ResponseEntity::ok);
    }

    private Optional<ResponseEntity<Access>> create(Access access) {
        return accessService.create(access).map(this::createdResponse);
    }

    private ResponseEntity<Access> createdResponse(Access access) {
        var uri = getAccessUri(access.user(), access.service());
        return ResponseEntity.created(uri).body(access);
    }

    private URI getAccessUri(String user, String service) {
        return MvcUriComponentsBuilder
                .fromMethodCall(on(AccessController.class).get(user, service))
                .buildAndExpand()
                .encode().toUri();
    }
}
