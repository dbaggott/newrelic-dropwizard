package io.dnbg.newrelic.dropwizard.agent;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class MetricAwareNewRelicAgentTest {
    @Mock private NewRelicAgent newRelicAgent;
    private MetricAwareNewRelicAgent agent;

    @Before
    public void setUp() throws Exception {
        agent = new MetricAwareNewRelicAgent(newRelicAgent);
    }

    @Test
    public void shouldNotThrowExceptionWhenMetricIsMissing() throws Exception {
        agent.reportGauge("NewRelicName", "units", null);
        agent.reportGaugeAsPercentage("NewRelicName", "units", null);
        agent.reportCounter("NewRelicName", "units", null);
        agent.reportMeterOneMinuteRate("NewRelicName", null);
        agent.reportTimer("NewRelicPrefix", null);
    }
}