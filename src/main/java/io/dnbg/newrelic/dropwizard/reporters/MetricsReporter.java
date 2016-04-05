package io.dnbg.newrelic.dropwizard.reporters;

import io.dnbg.newrelic.dropwizard.agent.MetricAwareNewRelicAgent;
import io.dnbg.newrelic.dropwizard.responses.DropwizardResponse;
import io.dnbg.newrelic.dropwizard.responses.Metrics;
import com.newrelic.metrics.publish.util.Logger;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MetricsReporter implements NewRelicReporter<Metrics> {
    private static final Logger logger = Logger.getLogger(MetricsReporter.class);

    private static final Pattern GC_COUNT_PATTERN = Pattern.compile("jvm\\.gc\\.([^\\.]+)\\.count");
    private static final Pattern GC_TIME_PATTERN = Pattern.compile("jvm\\.gc\\.([^.]+)\\.time");
    private static final Pattern MEMORY_POOL_USAGE_PATTERN = Pattern.compile("jvm\\.memory\\.pools\\.([^\\.]+)\\.usage");
    private static final Pattern CONNECTION_FACTORY_PATTERN = Pattern.compile("org\\.eclipse\\.jetty\\.server\\.HttpConnectionFactory\\.([0-9]+)\\.connections");

    private MetricAwareNewRelicAgent agent;

    public MetricsReporter(MetricAwareNewRelicAgent agent) {
        this.agent = agent;
    }

    @Override
    public void report(DropwizardResponse<Metrics> data) {
        if (!data.serverResponded()) {
            // note: we don't log anything to NR here and rely on HealthCheckReporter to report non-responding servers
            logger.warn("No response available, unable to report metrics: ", data);
            return;
        }

        Metrics metrics = data.getResult();
        reportLogback(metrics);
        reportServlet(metrics);
        reportJetty(metrics);
        reportJvmMemoryAndGarbageCollection(metrics);
        reportJvmThreads(metrics);
        reportJvmBuffers(metrics);
    }

    private void reportLogback(Metrics metrics) {
        agent.reportMeterOneMinuteRate("Logback/M1RateAll", metrics.getMeter("ch.qos.logback.core.Appender.all"));
        agent.reportMeterOneMinuteRate("Logback/M1Rate/Error", metrics.getMeter("ch.qos.logback.core.Appender.error"));
        agent.reportMeterOneMinuteRate("Logback/M1Rate/Warn", metrics.getMeter("ch.qos.logback.core.Appender.warn"));
        agent.reportMeterOneMinuteRate("Logback/M1Rate/Info", metrics.getMeter("ch.qos.logback.core.Appender.info"));
        agent.reportMeterOneMinuteRate("Logback/M1Rate/Debug", metrics.getMeter("ch.qos.logback.core.Appender.debug"));
        agent.reportMeterOneMinuteRate("Logback/M1Rate/Trace", metrics.getMeter("ch.qos.logback.core.Appender.trace"));
    }

    private void reportServlet(Metrics metrics) {
        agent.reportMeterOneMinuteRate("Jetty/Servlet/ResponseCodeM1Rate/1xx", metrics.getMeter("io.dropwizard.jetty.MutableServletContextHandler.1xx-responses"));
        agent.reportMeterOneMinuteRate("Jetty/Servlet/ResponseCodeM1Rate/2xx", metrics.getMeter("io.dropwizard.jetty.MutableServletContextHandler.2xx-responses"));
        agent.reportMeterOneMinuteRate("Jetty/Servlet/ResponseCodeM1Rate/3xx", metrics.getMeter("io.dropwizard.jetty.MutableServletContextHandler.3xx-responses"));
        agent.reportMeterOneMinuteRate("Jetty/Servlet/ResponseCodeM1Rate/4xx", metrics.getMeter("io.dropwizard.jetty.MutableServletContextHandler.4xx-responses"));
        agent.reportMeterOneMinuteRate("Jetty/Servlet/ResponseCodeM1Rate/5xx", metrics.getMeter("io.dropwizard.jetty.MutableServletContextHandler.5xx-responses"));

        agent.reportGaugeAsPercentage("Jetty/Servlet/ResponseCodePercentage/4xx", "% (1 min)", metrics.getGauge("io.dropwizard.jetty.MutableServletContextHandler.percent-4xx-1m"));
        agent.reportGaugeAsPercentage("Jetty/Servlet/ResponseCodePercentage/5xx", "% (1 min)", metrics.getGauge("io.dropwizard.jetty.MutableServletContextHandler.percent-5xx-1m"));

        agent.reportTimer("Jetty/Servlet/Request/Method/Connect", metrics.getTimer("io.dropwizard.jetty.MutableServletContextHandler.connect-requests"));
        agent.reportTimer("Jetty/Servlet/Request/Method/Delete", metrics.getTimer("io.dropwizard.jetty.MutableServletContextHandler.delete-requests"));
        agent.reportTimer("Jetty/Servlet/Request/Method/Get", metrics.getTimer("io.dropwizard.jetty.MutableServletContextHandler.get-requests"));
        agent.reportTimer("Jetty/Servlet/Request/Method/Head", metrics.getTimer("io.dropwizard.jetty.MutableServletContextHandler.head-requests"));
        agent.reportTimer("Jetty/Servlet/Request/Method/Move", metrics.getTimer("io.dropwizard.jetty.MutableServletContextHandler.move-requests"));
        agent.reportTimer("Jetty/Servlet/Request/Method/Options", metrics.getTimer("io.dropwizard.jetty.MutableServletContextHandler.options-requests"));
        agent.reportTimer("Jetty/Servlet/Request/Method/Post", metrics.getTimer("io.dropwizard.jetty.MutableServletContextHandler.post-requests"));
        agent.reportTimer("Jetty/Servlet/Request/Method/Put", metrics.getTimer("io.dropwizard.jetty.MutableServletContextHandler.put-requests"));
        agent.reportTimer("Jetty/Servlet/Request/Method/Trace", metrics.getTimer("io.dropwizard.jetty.MutableServletContextHandler.trace-requests"));
        agent.reportTimer("Jetty/Servlet/Request/Method/Other", metrics.getTimer("io.dropwizard.jetty.MutableServletContextHandler.other-requests"));

        agent.reportTimer("Jetty/Servlet/Request/Dispatches", metrics.getTimer("io.dropwizard.jetty.MutableServletContextHandler.dispatches"));

        agent.reportCounter("Jetty/Servlet/Request/Active/Dispatches", "requests", metrics.getCounter("io.dropwizard.jetty.MutableServletContextHandler.active-dispatches"));
        agent.reportCounter("Jetty/Servlet/Request/Active/Suspended", "requests", metrics.getCounter("io.dropwizard.jetty.MutableServletContextHandler.active-suspended"));

        agent.reportMeterOneMinuteRate("Jetty/Servlet/Request/AsyncM1Rate/dispatches", metrics.getMeter("io.dropwizard.jetty.MutableServletContextHandler.async-dispatches"));
        agent.reportMeterOneMinuteRate("Jetty/Servlet/Request/AsyncM1Rate/timeouts", metrics.getMeter("io.dropwizard.jetty.MutableServletContextHandler.async-timeouts"));
    }

    private void reportJetty(Metrics metrics) {
        agent.reportGauge("Jetty/Server/QueuedThreadPool/Counts/ThreadsCurrent", "threads", metrics.getGauge("org.eclipse.jetty.util.thread.QueuedThreadPool.dw.size"));
        agent.reportGauge("Jetty/Server/QueuedThreadPool/Counts/JobsWaiting", "jobs", metrics.getGauge("org.eclipse.jetty.util.thread.QueuedThreadPool.dw.jobs"));
        agent.reportGaugeAsPercentage("Jetty/Server/QueuedThreadPool/Utilization/Max", "% (based on max thread count)", metrics.getGauge("org.eclipse.jetty.util.thread.QueuedThreadPool.dw.utilization-max"));
        agent.reportGaugeAsPercentage("Jetty/Server/QueuedThreadPool/Utilization/Current", "% (based on current thread count)", metrics.getGauge("org.eclipse.jetty.util.thread.QueuedThreadPool.dw.utilization"));

        // port is configurable so cannot assume 8080/8081 (cannot rely on plugin configuration to identify ports as
        // there may be a proxy)
        for (Map.Entry<String, Metrics.Timer> entry : metrics.timers.entrySet()) {
            String dropwizardMetricName = entry.getKey();
            Matcher matcher;
            if ((matcher = CONNECTION_FACTORY_PATTERN.matcher(dropwizardMetricName)).matches()) {
                String port = matcher.group(1);
                agent.reportTimer("Jetty/Server/HttpConnectionFactory/" + port + "/Connections", entry.getValue());
            }
        }
    }

    private void reportJvmMemoryAndGarbageCollection(Metrics metrics) {
        agent.reportGauge("JVM/Memory/Heap/Initial", "bytes", metrics.getGauge("jvm.memory.heap.init"));
        agent.reportGauge("JVM/Memory/Heap/Usage/Committed", "bytes", metrics.getGauge("jvm.memory.heap.committed"));
        agent.reportGauge("JVM/Memory/Heap/Usage/Max", "bytes", metrics.getGauge("jvm.memory.heap.max"));
        agent.reportGauge("JVM/Memory/Heap/Usage/Used", "bytes", metrics.getGauge("jvm.memory.heap.used"));
        agent.reportGaugeAsPercentage("JVM/Memory/Heap/Usage", "%", metrics.getGauge("jvm.memory.heap.usage"));

        agent.reportGauge("JVM/Memory/NonHeap/Initial", "bytes", metrics.getGauge("jvm.memory.non-heap.init"));
        agent.reportGauge("JVM/Memory/NonHeap/Usage/Committed", "bytes", metrics.getGauge("jvm.memory.non-heap.committed"));
        agent.reportGauge("JVM/Memory/NonHeap/Usage/Max", "bytes", metrics.getGauge("jvm.memory.non-heap.max"));
        agent.reportGauge("JVM/Memory/NonHeap/Usage/Used", "bytes", metrics.getGauge("jvm.memory.non-heap.used"));
        agent.reportGaugeAsPercentage("JVM/Memory/NonHeap/Usage", "%", metrics.getGauge("jvm.memory.non-heap.usage"));

        // Metric names for "jvm.gc.XXX.count|time" and ""jvm.memory.pools.XXX.usage" vary by jvm
        for (Map.Entry<String, Metrics.Gauge> entry : metrics.gauges.entrySet()) {
            String dropwizardMetricName = entry.getKey();
            Matcher matcher;
            if ((matcher = MEMORY_POOL_USAGE_PATTERN.matcher(dropwizardMetricName)).matches()) {
                String pool = matcher.group(1);
                agent.reportGaugeAsPercentage("JVM/Memory/PoolUsage/" + pool, "%", metrics.getGauge(entry.getKey()));
            } else if ((matcher = GC_COUNT_PATTERN.matcher(dropwizardMetricName)).matches()) {
                String type = matcher.group(1);
                agent.reportMetric("JVM/GC/Collections/Count/" + type, "count", (Number) entry.getValue().value);
            } else if ((matcher = GC_TIME_PATTERN.matcher(dropwizardMetricName)).matches()) {
                String type = matcher.group(1);
                agent.reportMetric("JVM/GC/Collections/Time/" + type, "ms", (Number) entry.getValue().value);
            }
        }
    }

    private void reportJvmBuffers(Metrics metrics) {
        agent.reportGauge("JVM/Buffers/Direct/Capacity", "bytes", metrics.getGauge("jvm.buffers.direct.capacity"));
        agent.reportGauge("JVM/Buffers/Direct/Count", "buffers", metrics.getGauge("jvm.buffers.direct.count"));
        agent.reportGauge("JVM/Buffers/Direct/Used", "bytes", metrics.getGauge("jvm.buffers.direct.used"));
        agent.reportGauge("JVM/Buffers/Mapped/Capacity", "bytes", metrics.getGauge("jvm.buffers.mapped.capacity"));
        agent.reportGauge("JVM/Buffers/Mapped/Count", "buffers", metrics.getGauge("jvm.buffers.mapped.count"));
        agent.reportGauge("JVM/Buffers/Mapped/Used", "bytes", metrics.getGauge("jvm.buffers.mapped.used"));
    }

    private void reportJvmThreads(Metrics metrics) {
        agent.reportGauge("JVM/Threads/Count/Total", "threads", metrics.getGauge("jvm.threads.count"));
        agent.reportGauge("JVM/Threads/Count/Daemon", "threads", metrics.getGauge("jvm.threads.daemon.count"));
        agent.reportMetric("JVM/Threads/Count/Deadlocked", "threads", readDeadlockedThreadCount(metrics));

        agent.reportGauge("JVM/Threads/State/New", "threads", metrics.getGauge("jvm.threads.new.count"));
        agent.reportGauge("JVM/Threads/State/Runnable", "threads", metrics.getGauge("jvm.threads.runnable.count"));
        agent.reportGauge("JVM/Threads/State/Blocked", "threads", metrics.getGauge("jvm.threads.blocked.count"));
        agent.reportGauge("JVM/Threads/State/Waiting", "threads", metrics.getGauge("jvm.threads.waiting.count"));
        agent.reportGauge("JVM/Threads/State/TimedWaiting", "threads", metrics.getGauge("jvm.threads.timed_waiting.count"));
        agent.reportGauge("JVM/Threads/State/Terminated", "threads", metrics.getGauge("jvm.threads.terminated.count"));
    }

    private int readDeadlockedThreadCount(Metrics metrics) {
        Metrics.Gauge gauge = metrics.getGauge("jvm.threads.deadlocks");
        if (gauge == null) {
            logger.warn("Dropwizard gauge 'jvm.threads.deadlocks' does not exist: unable to report deadlocks");
            return 0;
        }

        if (gauge.value instanceof List) {
            return ((List) gauge.value).size();
        } else {
            logger.warn("Dropwizard gauge 'jvm.threads.deadlocks' is not a List: unable to report deadlocks");
            return 0;
        }
    }
}
