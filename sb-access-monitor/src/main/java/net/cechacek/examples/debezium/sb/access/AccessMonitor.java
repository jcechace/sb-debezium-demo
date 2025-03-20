package net.cechacek.examples.debezium.sb.access;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.handler.annotation.Payload;

import java.io.IOException;
import java.time.Instant;

@MessageEndpoint
public class AccessMonitor {

    private static final Logger LOG = LoggerFactory.getLogger(AccessMonitor.class);

    private final ObjectMapper objectMapper;
    private final AccessReporter reporter;

    public AccessMonitor(ObjectMapper objectMapper, AccessReporter reporter) {
        this.objectMapper = objectMapper;
        this.reporter = reporter;
    }

    @ServiceActivator(inputChannel = "debeziumInputChannel")
    public void record(@Payload byte[] event) {
        try {
            // get data
            var payload =  jsonPayload(event);
            var before = payload.get("before");
            var after = payload.get("after");
            var ts = payload.get("source").get("ts_ms").asLong();

            // Send them
            reporter.report(jsonToGrant(before, ts), jsonToGrant(after, ts));
        }
        catch (IOException e) {
            LOG.error("Error deserializing message", e);
        }
    }

    /**
     * Reads json tree from byte array
     * @param event change event as byte array
     * @return json representation of change event
     */
    private JsonNode jsonPayload(byte[] event) throws IOException {
        var jsonEvent = objectMapper.readTree(event);
        return jsonEvent.get("payload");
    }

    /**
     * Creates object from before/after filed of change event
     * @param record before/after field
     * @param ts source timestamp
     * @return object representation of access grant or null
     */
    private AccessReporter.AccessGrant jsonToGrant(JsonNode record, long ts) {
        if (record == null || record.isNull()) {
            return null;
        }

        return new AccessReporter.AccessGrant(
                record.get("username").asText(),
                record.get("service").asText(),
                record.get("level").asText(),
                Instant.ofEpochMilli(ts)
        );
    }
}
