package io.dnbg.newrelic.dropwizard.agent;

import com.newrelic.metrics.publish.Agent;

/**
 * Interface for {@link Agent}
 */
public interface NewRelicAgent {
    /**
     * Report a metric with a name, unit(s) and value. The {@link Number} value is converted to a {@code float}. The
     * count is assumed to be 1, while minValue and maxValue are set to value. Sum of squares is calculated as the value
     * squared. If the value is {@code null}, the reporting is skipped.
     *
     * @param metricName the name of the metric
     * @param units      the units to report
     * @param value      the Number value to report
     */
    void reportMetric(String metricName, String units, Number value);

    /**
     * Report a metric with a name, unit(s), count, value, minValue, maxValue and sumOfSquares. All {@link Number}
     * values are converted to {@code floats}. If any of the values are {@code null}, the reporting is skipped.
     *
     * @param metricName   the name of the metric
     * @param units        the units to report
     * @param count        the number of things being measured
     * @param value        the Number value to report
     * @param minValue     the minimum Number value to report
     * @param maxValue     the maximum Number value to report
     * @param sumOfSquares the sum of squared values to report
     */
    void reportMetric(String metricName, String units, int count, Number value, Number minValue, Number maxValue, Number sumOfSquares);
}
