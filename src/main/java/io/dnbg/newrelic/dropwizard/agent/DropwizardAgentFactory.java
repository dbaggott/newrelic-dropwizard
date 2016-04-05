package io.dnbg.newrelic.dropwizard.agent;

import io.dnbg.newrelic.dropwizard.probes.HealthCheckProbe;
import io.dnbg.newrelic.dropwizard.probes.MetricsProbe;
import io.dnbg.newrelic.dropwizard.reporters.HealthCheckReporter;
import io.dnbg.newrelic.dropwizard.reporters.MetricsReporter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.newrelic.metrics.publish.Agent;
import com.newrelic.metrics.publish.AgentFactory;
import com.newrelic.metrics.publish.configuration.ConfigurationException;
import com.newrelic.metrics.publish.util.Logger;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import java.util.Map;

public class DropwizardAgentFactory extends AgentFactory {
    private static final Logger logger = Logger.getLogger(DropwizardAgentFactory.class);
    private ObjectMapper objectMapper;

    public DropwizardAgentFactory() {
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public Agent createConfiguredAgent(Map<String, Object> pluginProperties) throws ConfigurationException {
        AgentProperties properties = AgentProperties.parse(pluginProperties);
        logger.info("Creating agent using: ", properties);

        DropwizardAgent agent = new DropwizardAgent(properties.getName());
        registerProbes(properties, agent);
        return agent;
    }

    private void registerProbes(AgentProperties properties, DropwizardAgent agent) {
        MetricAwareNewRelicAgent metricsAwareAgent = new MetricAwareNewRelicAgent(agent);
        WebTarget adminWebResource = createAdminWebResource(properties.getHost(), properties.getAdminPort(), properties.getAdminPath());

        agent.add(new HealthCheckProbe(adminWebResource), new HealthCheckReporter(metricsAwareAgent));
        agent.add(new MetricsProbe(adminWebResource), new MetricsReporter(metricsAwareAgent));
    }

    private WebTarget createAdminWebResource(String host, int port, String adminPath) {
        return ClientBuilder
                .newBuilder()
                .register(new JacksonJsonProvider(objectMapper))
                .build()
                .target("http://" + host + ":" + port + adminPath);
    }
}
