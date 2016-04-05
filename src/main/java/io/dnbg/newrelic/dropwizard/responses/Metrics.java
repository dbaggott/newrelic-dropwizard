package io.dnbg.newrelic.dropwizard.responses;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Map;

/**
 * Represents the json response of a Dropwizard /metrics admin request.
 * <p/>
 * It's possible that any of the metrics will report an 'error' attribute instead of their normal attributes.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Metrics {
    public String version;
    public Map<String, Gauge> gauges;
    public Map<String, Counter> counters;
    public Map<String, Histogram> histograms;
    public Map<String, Meter> meters;
    public Map<String, Timer> timers;

    public Counter getCounter(String name) {
        return counters.get(name);
    }

    public Gauge getGauge(String name) {
        return gauges.get(name);
    }

    public Histogram getHistogram(String name) {
        return histograms.get(name);
    }

    public Meter getMeter(String name) {
        return meters.get(name);
    }

    public Timer getTimer(String name) {
        return timers.get(name);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Gauge {
        public Object value;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Histogram {
        public long count;
        public double min;
        public double max;
        public double mean;
        public double stddev;
        public double p50;
        public double p75;
        public double p95;
        public double p98;
        public double p99;
        public double p999;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Meter {
        public long count;
        public double m1_rate;
        public double m5_rate;
        public double m15_rate;
        public double mean_rate;
        public String units;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Timer {
        public long count;
        // histogram-based
        public double min;
        public double max;
        public double mean;
        public double stddev;
        public double p50;
        public double p75;
        public double p95;
        public double p98;
        public double p99;
        public double p999;
        public String duration_units;
        // meter-based
        public double m1_rate;
        public double m5_rate;
        public double m15_rate;
        public double mean_rate;
        public String rate_units;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Counter {
        public long count;
    }
}
