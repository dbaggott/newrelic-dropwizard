package io.dnbg.newrelic.dropwizard.agent;

import io.dnbg.newrelic.dropwizard.responses.Metrics;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

@RunWith(MockitoJUnitRunner.class)
public class MetricAwareNewRelicAgentTest {
    @Mock private NewRelicAgent newRelicAgent;
    private MetricAwareNewRelicAgent agent;

    @Before
    public void setUp() throws Exception {
        agent = new MetricAwareNewRelicAgent(newRelicAgent);
    }

    @Test
    public void shouldReportGaugeAsPercentageCorrectly() throws Exception {
        Metrics.Gauge gauge = new Metrics.Gauge();
        gauge.value = 0.123;

        agent.reportGaugeAsPercentage("name", "units", gauge);

        verify(newRelicAgent).reportMetric("name", "units", 12.3);
    }

    @Test
    public void shouldReportOneMinuteRateCorrectly() throws Exception {
        Metrics.Meter meter = new Metrics.Meter();
        meter.m1_rate = 0.1;
        meter.units = "something / time";

        agent.reportMeterOneMinuteRate("name", meter);

        verify(newRelicAgent).reportMetric("name", "something / time", 0.1);
    }

    @Test
    public void reportOneMinuteRateShouldFloorSmallValuesToZero() throws Exception {
        Metrics.Meter meter = new Metrics.Meter();
        meter.m1_rate = 0.001;
        meter.units = "something / time";

        agent.reportMeterOneMinuteRate("name", meter);

        verify(newRelicAgent).reportMetric("name", "something / time", 0.0);
    }

    @Test
    public void shouldReportTimerCorrectly() throws Exception {
        Metrics.Timer timer = new Metrics.Timer();
        timer.p50 = 0.5;
        timer.p75 = 0.75;
        timer.p95 = 0.95;
        timer.p98 = 0.98;
        timer.p99 = 0.99;
        timer.p999 = 0.999;
        timer.mean = 0.51;
        timer.duration_units = "duration units";
        timer.m1_rate = 1.2;
        timer.rate_units = "rate units";

        agent.reportTimer("Prefix", timer);

        verify(newRelicAgent).reportMetric("Prefix/Rate/M1Rate", "rate units", 1.2);
        verify(newRelicAgent).reportMetric("Prefix/Dur/P50", "duration units", 0.5);
        verify(newRelicAgent).reportMetric("Prefix/Dur/P75", "duration units", 0.75);
        verify(newRelicAgent).reportMetric("Prefix/Dur/P95", "duration units", 0.95);
        verify(newRelicAgent).reportMetric("Prefix/Dur/P99", "duration units", 0.99);
        verify(newRelicAgent).reportMetric("Prefix/Dur/Mean", "duration units", 0.51);
    }

    @Test
    public void reportMetricShouldFloorSmallRatesToZero() throws Exception {
        Metrics.Timer timer = new Metrics.Timer();
        timer.m1_rate = 0.001;
        timer.rate_units = "rate units";

        agent.reportTimer("Prefix", timer);

        verify(newRelicAgent).reportMetric("Prefix/Rate/M1Rate", "rate units", 0.0);
    }

    @Test
    public void shouldNotThrowExceptionWhenMetricIsMissing() throws Exception {
        agent.reportGauge("NewRelicName", "units", null);
        agent.reportGaugeAsPercentage("NewRelicName", "units", null);
        agent.reportCounter("NewRelicName", "units", null);
        agent.reportMeterOneMinuteRate("NewRelicName", null);
        agent.reportTimer("NewRelicPrefix", null);

        verifyZeroInteractions(newRelicAgent);
    }
}