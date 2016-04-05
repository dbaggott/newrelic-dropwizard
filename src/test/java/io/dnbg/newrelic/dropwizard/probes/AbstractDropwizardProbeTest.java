package io.dnbg.newrelic.dropwizard.probes;

import io.dnbg.newrelic.dropwizard.responses.HealthCheck;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AbstractDropwizardProbeTest {
    private static final String PROBE_RESULT = "probe-result";
    @Mock private WebTarget webResource;
    @Mock private ObjectMapper objectMapper;
    @Mock private Invocation.Builder requestBuilder;
    @Mock private Response webResponse;
    @Mock private HealthCheck healthCheck;
    private DropwizardProbe<String> probe;

    @Before
    public void setUp() throws Exception {
        when(webResource.getUri()).thenReturn(new URI("http://someserver:8081/probe/path"));
        when(webResource.request(MediaType.APPLICATION_JSON_TYPE)).thenReturn(requestBuilder);
        when(requestBuilder.header(HttpHeaders.USER_AGENT, "New Relic Dropwizard Agent")).thenReturn(requestBuilder);
        when(requestBuilder.get()).thenReturn(webResponse);

        probe = new AbstractDropwizardProbe<String>(webResource) {
            @Override protected String parse(Response webResponse) {
                return PROBE_RESULT;
            }
        };
    }

    @Test
    public void responseShouldContainProbeResult() throws Exception {
        assertThat(probe.probe().getResult(), is(PROBE_RESULT));
    }

    @Test
    public void responseShouldIncludeHttpCode() throws Exception {
        given(webResponse.getStatus()).willReturn(123);

        assertThat(probe.probe().getHttpStatusCode(), is(123));
    }

    @Test
    public void shouldHandleExceptionWhileMakingHttpRequest() throws Exception {
        given(requestBuilder.get()).willThrow(new RuntimeException("thrown for test"));

        assertThat(probe.probe().serverResponded(), is(false));
    }

    @Test
    public void shouldHandleExceptionWhileParsing() throws Exception {
        DropwizardProbe<String> probeWithParsingFailure = new AbstractDropwizardProbe<String>(webResource) {
            @Override protected String parse(Response webResponse) {
                throw new RuntimeException("thrown for test");
            }
        };

        assertThat(probe.probe().serverResponded(), is(true));
    }
}