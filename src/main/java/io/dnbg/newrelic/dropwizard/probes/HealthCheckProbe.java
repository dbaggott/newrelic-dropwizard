package io.dnbg.newrelic.dropwizard.probes;

import io.dnbg.newrelic.dropwizard.responses.HealthCheck;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

public class HealthCheckProbe extends AbstractDropwizardProbe<HealthCheck> {
    public HealthCheckProbe(WebTarget adminWebResource) {
        super(adminWebResource.path("/healthcheck"));
    }

    @Override
    protected HealthCheck parse(Response webResponse) {
        return webResponse.readEntity(HealthCheck.class);
    }
}
