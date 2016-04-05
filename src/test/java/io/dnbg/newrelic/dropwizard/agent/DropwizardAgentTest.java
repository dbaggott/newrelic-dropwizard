package io.dnbg.newrelic.dropwizard.agent;

import io.dnbg.newrelic.dropwizard.probes.DropwizardProbe;
import io.dnbg.newrelic.dropwizard.reporters.NewRelicReporter;
import io.dnbg.newrelic.dropwizard.responses.DropwizardResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class DropwizardAgentTest {
    @Mock private DropwizardProbe<Object> firstProbe;
    @Mock private DropwizardResponse<Object> firstResult;
    @Mock private NewRelicReporter<Object> firstReporter;
    @Mock private DropwizardProbe<Object> secondProbe;
    @Mock private DropwizardResponse<Object> secondResult;
    @Mock private NewRelicReporter<Object> secondReporter;

    @Test
    public void shouldReportAgentNameCorrectly() throws Exception {
        DropwizardAgent agent = new DropwizardAgent("agent-name");
        assertThat(agent.getAgentName(), equalTo("agent-name"));
    }

    @Test
    public void shouldPollProbesAndReporters() throws Exception {
        given(firstProbe.probe()).willReturn(firstResult);
        given(secondProbe.probe()).willReturn(secondResult);

        DropwizardAgent agent = new DropwizardAgent("agent");
        agent.add(firstProbe, firstReporter);
        agent.add(secondProbe, secondReporter);

        agent.pollCycle();

        verify(firstReporter).report(firstResult);
        verify(secondReporter).report(secondResult);
    }

    @Test
    public void earlierFailuresShouldNotPreventSubsequentPolls() throws Exception {
        given(firstProbe.probe()).willThrow(new RuntimeException("thrown for test"));
        given(secondProbe.probe()).willReturn(secondResult);

        DropwizardAgent agent = new DropwizardAgent("agent");
        agent.add(firstProbe, firstReporter);
        agent.add(secondProbe, secondReporter);

        agent.pollCycle();

        verify(secondReporter).report(secondResult);
    }

    @Test(expected = IllegalArgumentException.class)
    public void agentNameCannotBeNull() throws Exception {
        new DropwizardAgent(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void agentNameCannotBeEmpty() throws Exception {
        new DropwizardAgent("");
    }
}