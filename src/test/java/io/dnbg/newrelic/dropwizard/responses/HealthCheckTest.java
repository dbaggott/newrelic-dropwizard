package io.dnbg.newrelic.dropwizard.responses;

import io.dnbg.newrelic.dropwizard.fixtures.Fixtures;
import org.junit.Test;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class HealthCheckTest {
    @Test
    public void shouldDeserializeHealthyCorrectly() throws Exception {
        HealthCheck result = Fixtures.healthCheckFor("fixtures/healthcheck-healthy.json");
        assertThat(result.healthCheckNames(), contains("checkWithMessage", "checkWithoutMessage"));
        assertThat(result.isHealthy("checkWithMessage"), is(true));
        assertThat(result.isHealthy("checkWithoutMessage"), is(true));
    }

    @Test
    public void shouldDeserializeUnhealthyCorrectly() throws Exception {
        HealthCheck result = Fixtures.healthCheckFor("fixtures/healthcheck-unhealthy.json");
        assertThat(result.healthCheckNames(), contains("checkWithMessage", "checkWithoutMessage", "checkWithError"));
        assertThat(result.isHealthy("checkWithMessage"), is(false));
        assertThat(result.isHealthy("checkWithoutMessage"), is(false));
        assertThat(result.isHealthy("checkWithError"), is(false));
    }
}