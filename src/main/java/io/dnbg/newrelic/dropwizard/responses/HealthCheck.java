package io.dnbg.newrelic.dropwizard.responses;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

/**
 * Represents the json response of a Dropwizard /healthcheck admin request.
 * <p/>
 * An individual check can be healthy or unhealthy and can optional include errors and messages:
 * <pre> {@code
 * {
 *   "withoutMessage" : {
 *     "healthy" : true
 *   },
 *   "withMessage" : {
 *     "healthy" : false,
 *     "message" : "connection pool healthy"
 *   },
 *   "withError" : {
 *     "healthy" : false,
 *     "message" : "Login failed for user",
 *     "error" : {
 *       "message" : "Login failed for user 'unknown'.",
 *       "stack" : [ "net.sourceforge.jtds.jdbc.SQLDiagnostic.addDiagnostic(SQLDiagnostic.java:372)",
 *                   "etc" ]
 *     }
 *   }
 * }
 * }</pre>
 */
@JsonDeserialize(using = HealthCheck.Deserializer.class)
public class HealthCheck {
    private Map<String, Status> statuses;

    public HealthCheck(Map<String, Status> statuses) {
        this.statuses = statuses;
    }

    public Set<String> healthCheckNames() {
        return statuses.keySet();
    }

    public boolean isHealthy(String name) {
        return statuses.get(name).healthy;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Status {
        public boolean healthy;
    }

    static class Deserializer extends StdDeserializer<HealthCheck> {
        public Deserializer() {
            super(HealthCheck.class);
        }

        @Override
        public HealthCheck deserialize(JsonParser parser, DeserializationContext context) throws IOException {
            Map<String, Status> statuses = parser.readValueAs(new TypeReference<Map<String, Status>>() {
            });
            return new HealthCheck(statuses);
        }
    }
}
