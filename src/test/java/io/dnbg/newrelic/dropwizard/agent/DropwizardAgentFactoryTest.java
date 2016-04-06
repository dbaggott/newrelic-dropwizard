package io.dnbg.newrelic.dropwizard.agent;

import com.google.common.collect.ImmutableMap;
import com.newrelic.metrics.publish.Agent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@RunWith(MockitoJUnitRunner.class)
public class DropwizardAgentFactoryTest {
    private DropwizardAgentFactory factory;

    @Before
    public void setUp() throws Exception {
        factory = new DropwizardAgentFactory();
    }

    @Test
    public void shouldCreateAgentWithName() throws Exception {
        Agent agent = factory.createConfiguredAgent(ImmutableMap.<String, Object>of(
                "name", "app-name"
        ));

        assertThat(agent.getAgentName(), is("app-name"));
    }

}