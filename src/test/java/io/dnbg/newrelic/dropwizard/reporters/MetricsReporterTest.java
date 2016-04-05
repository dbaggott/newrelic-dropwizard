package io.dnbg.newrelic.dropwizard.reporters;

import io.dnbg.newrelic.dropwizard.agent.MetricAwareNewRelicAgent;
import io.dnbg.newrelic.dropwizard.fixtures.Fixtures;
import io.dnbg.newrelic.dropwizard.responses.DropwizardResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class MetricsReporterTest {
    @Mock private MetricAwareNewRelicAgent agent;
    private MetricsReporter reporter;

    @Before
    public void setUp() throws Exception {
        reporter = new MetricsReporter(agent);
    }

    @Test
    public void shouldReportAllVersionsWithoutExceptions() throws Exception {
        for (String version : Fixtures.ALL_METRICS_VERSIONS) {
            reporter.report(DropwizardResponse.of(200, Fixtures.metricsFor(version)));
        }
    }
}