package io.dnbg.newrelic.dropwizard.fixtures;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableSet;
import com.google.common.io.Resources;
import io.dnbg.newrelic.dropwizard.responses.HealthCheck;
import io.dnbg.newrelic.dropwizard.responses.Metrics;

import java.io.IOException;

public class Fixtures {
    public static final ImmutableSet<String> ALL_METRICS_VERSIONS = ImmutableSet.of(
            "fixtures/metrics-dropwizard-0.7.1.json",
            "fixtures/metrics-dropwizard-0.8.0.json",
            "fixtures/metrics-dropwizard-0.8.1.json",
            "fixtures/metrics-dropwizard-0.8.1-on-startup.json",
            "fixtures/metrics-dropwizard-0.8.2.json",
            "fixtures/metrics-dropwizard-0.8.2-on-startup.json",
            "fixtures/metrics-dropwizard-0.8.4.json",
            "fixtures/metrics-dropwizard-0.8.4-on-startup.json",
            "fixtures/metrics-dropwizard-0.8.5.json",
            "fixtures/metrics-dropwizard-0.8.5-on-startup.json",
            "fixtures/metrics-dropwizard-0.9.0.json",
            "fixtures/metrics-dropwizard-0.9.0-on-startup.json",
            "fixtures/metrics-dropwizard-0.9.2.json",
            "fixtures/metrics-dropwizard-0.9.2-on-startup.json"
    );

    public static Metrics metricsFor(String path) throws IOException {
        return fromFixture(path, Metrics.class);
    }

    public static HealthCheck healthCheckFor(String path) throws IOException {
        return fromFixture(path, HealthCheck.class);
    }

    private static <T> T fromFixture(String path, Class<T> valueType) throws IOException {
        String json = Resources.toString(Resources.getResource(path), Charsets.UTF_8);
        return new ObjectMapper().readValue(json, valueType);
    }
}
