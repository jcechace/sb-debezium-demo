package net.cechacek.examples.debezium.sb.access;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * Reports changes to access to Discord.
 * Only considers MANAGE level access
 */
@Component
public class DiscordHookReporter implements AccessReporter {

    public static final String REPORTED_LEVEL = "MANAGE";
    public static final String DISCORD_USERNAME = "Access Monitor";

    private final RestClient client;

    public DiscordHookReporter(@Value("${monitor.discord.webhook}") String url) {
        this.client = RestClient.builder()
                .baseUrl(url)
                .build();
    }

    @Override
    public boolean matches(AccessGrant grant) {
        return grant != null && grant.level().equals(REPORTED_LEVEL);
    }

    @Override
    public void reportGranted(AccessGrant grant) {
        report(grant, true);
    }

    @Override
    public void reportRevoked(AccessGrant before) {
        report(before, false);
    }

    /**
     * Sends message to discord webhook
     * @param grant access grant object
     * @param granted true if granted, false if revoked
     */
    private void report(AccessGrant grant, boolean granted) {


        client.post()
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of(
                        "username", DISCORD_USERNAME,
                        "content", message(grant, granted)
                ))
                .retrieve()
                .toBodilessEntity();
    }

    /**
     * Constructs message template;
     *
     * @param grant access grant object
     * @param granted true if granted, false if revoked
     * @return message to be sent
     */
    private String message(AccessGrant grant, boolean granted) {
        var formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        var when = LocalDateTime.ofInstant(grant.timestamp(), ZoneId.systemDefault());
        var operation = granted ? "granted" : "revoked";

        return """
                **%s** access **%s** at __%s__
                ```
                User: %s
                Service: %s
                ```
                
                """.formatted(
                        grant.level(),
                        operation,
                        when.format(formatter),
                        grant.user(),
                        grant.service()
        );
    }
}
