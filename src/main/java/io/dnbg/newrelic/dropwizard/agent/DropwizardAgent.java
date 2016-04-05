package io.dnbg.newrelic.dropwizard.agent;

import io.dnbg.newrelic.dropwizard.probes.DropwizardProbe;
import io.dnbg.newrelic.dropwizard.reporters.NewRelicReporter;
import io.dnbg.newrelic.dropwizard.responses.DropwizardResponse;
import com.google.common.base.MoreObjects;
import com.google.common.base.Strings;
import com.newrelic.metrics.publish.Agent;
import com.newrelic.metrics.publish.util.Logger;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;

class DropwizardAgent extends Agent implements NewRelicAgent {
    private static final Logger logger = Logger.getLogger(DropwizardAgent.class);

    private static final String GUID = "io.dnbg.newrelic.dropwizard";
    private static final String VERSION = "1.0.1"; // keep in sync with pom.xml version

    private String agentName;
    private List<PollTask> pollTasks;

    DropwizardAgent(String agentName) {
        super(GUID, VERSION);
        logger.info("Version ", VERSION);
        checkArgument(!Strings.isNullOrEmpty(agentName));
        this.agentName = agentName;
        this.pollTasks = new ArrayList<PollTask>();
    }

    @Override
    public void pollCycle() {
        for (PollTask task : pollTasks) {
            try {
                task.run();
            } catch (Exception e) {
                logger.error(e, "Problem polling: ", task);
            }
        }
    }

    <T> void add(DropwizardProbe<T> probe, NewRelicReporter<T> reporter) {
        pollTasks.add(new PollTask<T>(probe, reporter));
    }

    @Override
    public String getAgentName() {
        return agentName;
    }

    @Override public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("agentName", agentName)
                .add("pollTasks", pollTasks)
                .toString();
    }

    private class PollTask<T> {
        private DropwizardProbe<T> probe;
        private NewRelicReporter<T> reporter;

        PollTask(DropwizardProbe<T> probe, NewRelicReporter<T> reporter) {
            this.probe = probe;
            this.reporter = reporter;
        }

        void run() {
            DropwizardResponse<T> response = probe.probe();
            reporter.report(response);
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this)
                    .add("probe", probe)
                    .add("reporter", reporter)
                    .toString();
        }
    }
}
