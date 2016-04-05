package io.dnbg.newrelic.dropwizard.probes;

import io.dnbg.newrelic.dropwizard.responses.DropwizardResponse;
import com.newrelic.metrics.publish.util.Logger;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Provides basic probing functionality to read the status code in the http response and handle exceptions.
 */
abstract class AbstractDropwizardProbe<T> implements DropwizardProbe<T> {
    private static final Logger logger = Logger.getLogger(AbstractDropwizardProbe.class);

    private final WebTarget webResource;

    AbstractDropwizardProbe(WebTarget webResource) {
        this.webResource = checkNotNull(webResource);
    }

    @Override
    final public DropwizardResponse<T> probe() {
        try {
            Response webResponse = webResource
                    .request(MediaType.APPLICATION_JSON_TYPE)
                    .header(HttpHeaders.USER_AGENT, "New Relic Dropwizard Agent")
                    .get();
            T result = parse(webResponse);
            return DropwizardResponse.of(webResponse.getStatus(), result);
        } catch (Exception e) {
            logger.error(e, "Unable to get response from ", webResource.getUri());
            return DropwizardResponse.of(e);
        }
    }

    /**
     * Implementing classes should override to parse the request-specific data
     */
    protected abstract T parse(Response webResponse);
}
