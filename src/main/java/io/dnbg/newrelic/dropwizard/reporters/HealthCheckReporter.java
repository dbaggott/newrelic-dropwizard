package io.dnbg.newrelic.dropwizard.reporters;

import io.dnbg.newrelic.dropwizard.agent.NewRelicAgent;
import io.dnbg.newrelic.dropwizard.responses.DropwizardResponse;
import io.dnbg.newrelic.dropwizard.responses.HealthCheck;

public class HealthCheckReporter implements NewRelicReporter<HealthCheck> {
    private NewRelicAgent agent;

    public HealthCheckReporter(NewRelicAgent agent) {
        this.agent = agent;
    }

    @Override
    public void report(DropwizardResponse<HealthCheck> response) {
        if (!response.serverResponded()) {
            agent.reportMetric("Health/Unhealthy/Overall", "bool", asInt(true));
            agent.reportMetric("Health/HttpCode/NoResponse", "bool", asInt(true));
            agent.reportMetric("Health/Unhealthy/Individual/NoData", "bool", asInt(true));
            return;
        }

        agent.reportMetric("Health/Unhealthy/Overall", "bool", asInt(response.getHttpStatusCode() != 200));
        agent.reportMetric("Health/HttpCode/" + response.getHttpStatusCode(), "bool", asInt(true));
        HealthCheck result = response.getResult();
        for (String check : result.healthCheckNames()) {
            agent.reportMetric("Health/Unhealthy/Individual/" + check, "bool", asInt(!result.isHealthy(check)));
        }
    }

    private int asInt(boolean value) {
        return value ? 1 : 0;
    }
}
