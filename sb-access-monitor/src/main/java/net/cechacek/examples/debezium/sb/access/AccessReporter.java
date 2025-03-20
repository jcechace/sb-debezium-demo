package net.cechacek.examples.debezium.sb.access;

import java.time.Instant;
import java.util.Objects;

/**
 * Reports changes to user's service access level
 */
public interface AccessReporter {

    /**
     * Representation of service access granted to user
     */
    record AccessGrant(String user, String service, String level, Instant timestamp) {
        @Override
        public boolean equals(Object o) {
            if (!(o instanceof AccessGrant that)) {
                return false;
                }
            return Objects.equals(user, that.user) && Objects.equals(level, that.level) && Objects.equals(service, that.service);
        }

        @Override
        public int hashCode() {
            return Objects.hash(user, service, level);
        }
    }

    /**
     * Indicates whether change qualifies to be reported by this reporter
     *
     * @param grant grant value
     * @return true if change qualifies
     */
    boolean matches(AccessGrant grant);

    /**
     * Reports that access was granted
     *
     * @param after
     */
    void reportGranted(AccessGrant after);

    /**
     * Reports that access was revoked
     * @param before
     */
    void reportRevoked(AccessGrant before);

    /**
     * Reports change to access levels
     * @param before previous access
     * @param after current access
     */
    default void report(AccessGrant before, AccessGrant after) {
       if (matches(after)) {
           reportGranted(after);
       }

       if (matches(before)) {
           reportRevoked(before);
       }
    }
}
