package io.dnbg.newrelic.dropwizard.responses;

import io.dnbg.newrelic.dropwizard.fixtures.Fixtures;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.number.IsCloseTo.closeTo;

public class MetricsTest {
    private Metrics metrics;

    @Before
    public void setUp() throws Exception {
        metrics = Fixtures.metricsFor("fixtures/metrics.json");
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldDeserializeGauges() throws Exception {
        assertThat((Integer) metrics.getGauge("int").value, is(1));
        assertThat((Double) metrics.getGauge("double").value, closeTo(0.056430816650390625, 0));
        assertThat((List<String>) metrics.getGauge("stringArray").value, contains("first", "second"));
    }

    @Test
    public void shouldDeserializeCounters() throws Exception {
        assertThat(metrics.getCounter("counter").count, is(1L));
    }

    @Test
    public void shouldDeserializeHistogram() throws Exception {
        Metrics.Histogram histogram = metrics.getHistogram("histogram");

        assertThat(histogram.count, is(71395619L));
        assertThat(histogram.min, closeTo(0.1, 0));
        assertThat(histogram.max, closeTo(656036, 0));
        assertThat(histogram.mean, closeTo(7331.137159533074, 0));
        assertThat(histogram.stddev, closeTo(41176.632195974824, 0));
        assertThat(histogram.p50, closeTo(128.0, 0));
        assertThat(histogram.p75, closeTo(1340.25, 0));
        assertThat(histogram.p95, closeTo(25276.55, 0));
        assertThat(histogram.p98, closeTo(67771.27999999997, 0));
        assertThat(histogram.p99, closeTo(191443.7100000005, 0));
        assertThat(histogram.p999, closeTo(654688.5150000001, 0));
    }

    @Test
    public void shouldDeserializeMeter() throws Exception {
        Metrics.Meter meter = metrics.getMeter("meter");

        assertThat(meter.count, is(20L));
        assertThat(meter.m1_rate, closeTo(0.970084298542596, 0));
        assertThat(meter.m5_rate, closeTo(3.013074625818629, 0));
        assertThat(meter.m15_rate, closeTo(3.6395131280614716, 0));
        assertThat(meter.mean_rate, closeTo(0.2179997368743176, 0));
        assertThat(meter.units, is("events/second"));
    }

    @Test
    public void shouldDeserializeTimer() throws Exception {
        Metrics.Timer timer = metrics.getTimer("timer");

        assertThat(timer.count, is(213129L));
        assertThat(timer.m1_rate, closeTo(0.017163163404961646, 0));
        assertThat(timer.m5_rate, closeTo(0.037938755998770855, 0));
        assertThat(timer.m15_rate, closeTo(0.04910168634728172, 0));
        assertThat(timer.mean_rate, closeTo(0.052878364032099695, 0));
        assertThat(timer.rate_units, is("calls/second"));
        assertThat(timer.min, closeTo(58.82701595300001, 0));
        assertThat(timer.max, closeTo(341.999257165, 0));
        assertThat(timer.mean, closeTo(67.00363584754572, 0));
        assertThat(timer.stddev, closeTo(30.069746144819348, 0));
        assertThat(timer.p50, closeTo(59.9983916515, 0));
        assertThat(timer.p75, closeTo(59.99949046825, 0));
        assertThat(timer.p95, closeTo(120.39979615434964, 0));
        assertThat(timer.p98, closeTo(196.67973126061986, 0));
        assertThat(timer.p99, closeTo(222.4197314987001, 0));
        assertThat(timer.p999, closeTo(340.46224397356724, 0));
    }

    @Test
    public void shouldDeserializeAllVersionsWithoutExceptions() throws Exception {
        for (String metrics : Fixtures.ALL_METRICS_VERSIONS) {
            Fixtures.metricsFor(metrics);
        }
    }
}