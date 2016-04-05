package io.dnbg.newrelic.dropwizard.agent;

import io.dnbg.newrelic.dropwizard.responses.Metrics;
import com.newrelic.metrics.publish.util.Logger;

/**
 * Decorates an agent to provide additional reporting facilities around Metrics
 */
public class MetricAwareNewRelicAgent implements NewRelicAgent {
    private static final Logger logger = Logger.getLogger(MetricAwareNewRelicAgent.class);

    private NewRelicAgent agent;

    MetricAwareNewRelicAgent(NewRelicAgent agent) {
        this.agent = agent;
    }

    @Override
    public void reportMetric(String metricName, String units, Number value) {
        agent.reportMetric(metricName, units, value);
    }

    @Override
    public void reportMetric(String metricName, String units, int count, Number value, Number minValue, Number maxValue, Number sumOfSquares) {
        agent.reportMetric(metricName, units, count, value, minValue, maxValue, sumOfSquares);
    }

    public void reportGauge(String newRelicName, String units, Metrics.Gauge gauge) {
        if (gauge == null) {
            logMetricMissingWarning(newRelicName);
            return;
        }

        if (gauge.value instanceof Number) {
            agent.reportMetric(newRelicName, units, (Number) gauge.value);
        } else {
            logMetricNotANumber(newRelicName);
        }
    }

    public void reportGaugeAsPercentage(String newRelicName, String units, Metrics.Gauge gauge) {
        if (gauge == null) {
            logMetricMissingWarning(newRelicName);
            return;
        }

        if (gauge.value instanceof Number) {
            double value = ((Number) gauge.value).doubleValue() * 100;
            agent.reportMetric(newRelicName, units, value);
        } else {
            logMetricNotANumber(newRelicName);
        }
    }

    public void reportCounter(String newRelicName, String units, Metrics.Counter counter) {
        if (counter == null) {
            logMetricMissingWarning(newRelicName);
            return;
        }

        agent.reportMetric(newRelicName, units, counter.count);
    }

    public void reportMeterOneMinuteRate(String newRelicName, Metrics.Meter meter) {
        if (meter == null) {
            logMetricMissingWarning(newRelicName);
            return;
        }

        agent.reportMetric(newRelicName, meter.units, normalizeRate(meter.m1_rate));
    }

    /**
     * Creates multiple NR metrics from the specified Timer.
     * <p/>
     * For example, for calls to this method with a {@code newRelicPrefix} of "Jetty/Servlet/Request/Method/Get", the
     * following values would be reported to NR:
     * <pre>
     * Jetty/Servlet/Request/Method/Get/Rate/M1Rate
     * Jetty/Servlet/Request/Method/Get/Dur/P99
     * Jetty/Servlet/Request/Method/Get/DurP75
     * etc
     * </pre>
     */
    public void reportTimer(String newRelicPrefix, Metrics.Timer timer) {
        if (timer == null) {
            logMetricMissingWarning(newRelicPrefix);
            return;
        }

        agent.reportMetric(newRelicPrefix + "/Rate/M1Rate", timer.rate_units, normalizeRate(timer.m1_rate));
        agent.reportMetric(newRelicPrefix + "/Dur/Mean", timer.duration_units, timer.mean);
        agent.reportMetric(newRelicPrefix + "/Dur/P99", timer.duration_units, timer.p99);
        agent.reportMetric(newRelicPrefix + "/Dur/P95", timer.duration_units, timer.p95);
        agent.reportMetric(newRelicPrefix + "/Dur/P75", timer.duration_units, timer.p75);
        agent.reportMetric(newRelicPrefix + "/Dur/P50", timer.duration_units, timer.p50);
    }

    // round down to zero as /metrics tends to display very small numbers for a long time in the absence of new events
    private double normalizeRate(double rate) {
        return rate < 0.01 ? 0 : rate;
    }

    private void logMetricMissingWarning(String newRelicName) {
        logger.warn("Not reporting ", newRelicName, " dropwizard metric missing");
    }

    private void logMetricNotANumber(String newRelicName) {
        logger.warn("Not reporting ", newRelicName, " dropwizard value is not a number");
    }
}
