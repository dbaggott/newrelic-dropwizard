package io.dnbg.newrelic.dropwizard;

import io.dnbg.newrelic.dropwizard.agent.DropwizardAgentFactory;
import com.newrelic.metrics.publish.Runner;
import com.newrelic.metrics.publish.configuration.ConfigurationException;

public class Main {
    public static void main(String[] args) {
        try {
            Runner runner = new Runner();
            DropwizardAgentFactory agentFactory = new DropwizardAgentFactory();
            runner.add(agentFactory);
            runner.setupAndRun(); // Never returns
        } catch (ConfigurationException e) {
            System.err.println("ERROR: " + e.getMessage());
            System.exit(-1);
        }
    }
}
