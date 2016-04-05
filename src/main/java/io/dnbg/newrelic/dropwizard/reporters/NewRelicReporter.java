package io.dnbg.newrelic.dropwizard.reporters;

import io.dnbg.newrelic.dropwizard.responses.DropwizardResponse;

public interface NewRelicReporter<T> {
    void report(DropwizardResponse<T> data);
}
