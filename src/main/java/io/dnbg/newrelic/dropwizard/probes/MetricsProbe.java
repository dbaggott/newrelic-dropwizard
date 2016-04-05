package io.dnbg.newrelic.dropwizard.probes;

import io.dnbg.newrelic.dropwizard.responses.Metrics;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

public class MetricsProbe extends AbstractDropwizardProbe<Metrics> {
    public MetricsProbe(WebTarget adminWebResource) {
        super(adminWebResource.path("/metrics"));
    }

    @Override
    protected Metrics parse(Response webResponse) {
        return webResponse.readEntity(Metrics.class);
    }
}
