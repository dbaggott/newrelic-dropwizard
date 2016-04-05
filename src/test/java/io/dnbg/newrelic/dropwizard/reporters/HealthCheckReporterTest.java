package io.dnbg.newrelic.dropwizard.reporters;

import io.dnbg.newrelic.dropwizard.agent.NewRelicAgent;
import io.dnbg.newrelic.dropwizard.responses.DropwizardResponse;
import io.dnbg.newrelic.dropwizard.responses.HealthCheck;
import com.google.common.collect.ImmutableSet;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class HealthCheckReporterTest {
    @Mock private NewRelicAgent agent;
    @Mock private HealthCheck healthCheckResult;
    private HealthCheckReporter reporter;

    @Before
    public void setUp() throws Exception {
        reporter = new HealthCheckReporter(agent);
    }

    @Test
    public void shouldReport200ResponseCorrectly() throws Exception {
        reporter.report(DropwizardResponse.of(200, healthCheckResult));

        verify(agent).reportMetric("Health/Unhealthy/Overall", "bool", 0);
        verify(agent).reportMetric("Health/HttpCode/200", "bool", 1);
    }

    @Test
    public void shouldReportNon200ResponseCorrectly() throws Exception {
        reporter.report(DropwizardResponse.of(123, healthCheckResult));

        verify(agent).reportMetric("Health/Unhealthy/Overall", "bool", 1);
        verify(agent).reportMetric("Health/HttpCode/123", "bool", 1);
    }

    @Test
    public void shouldReportIndividualChecks() throws Exception {
        given(healthCheckResult.healthCheckNames()).willReturn(ImmutableSet.of("firstCheck", "secondCheck", "thirdCheck"));
        given(healthCheckResult.isHealthy("firstCheck")).willReturn(true);
        given(healthCheckResult.isHealthy("secondCheck")).willReturn(false);
        given(healthCheckResult.isHealthy("thirdCheck")).willReturn(true);

        reporter.report(DropwizardResponse.of(200, healthCheckResult));

        verify(agent).reportMetric("Health/Unhealthy/Individual/firstCheck", "bool", 0);
        verify(agent).reportMetric("Health/Unhealthy/Individual/secondCheck", "bool", 1);
        verify(agent).reportMetric("Health/Unhealthy/Individual/thirdCheck", "bool", 0);
    }

    @Test
    public void shouldReportNoResponse() throws Exception {
        reporter.report(DropwizardResponse.<HealthCheck>of(new Exception()));

        verify(agent).reportMetric("Health/Unhealthy/Overall", "bool", 1);
        verify(agent).reportMetric("Health/HttpCode/NoResponse", "bool", 1);
        verify(agent).reportMetric("Health/Unhealthy/Individual/NoData", "bool", 1);
    }
}