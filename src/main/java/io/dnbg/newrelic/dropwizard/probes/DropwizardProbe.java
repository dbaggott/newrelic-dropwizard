package io.dnbg.newrelic.dropwizard.probes;

import io.dnbg.newrelic.dropwizard.responses.DropwizardResponse;

/**
 * Probes a Dropwizard application to obtain data representing the running state of the application (eg /healthcheck or
 * /metrics).
 *
 * @param <T> The type of data contained within the response
 */
public interface DropwizardProbe<T> {
    DropwizardResponse<T> probe();
}
