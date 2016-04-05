package io.dnbg.newrelic.dropwizard.responses;

import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class DropwizardResponseTest {
    @Test
    public void responsePropertiesShouldBeSet() throws Exception {
        DropwizardResponse<String> response = DropwizardResponse.of(200, "result");
        assertThat(response.serverResponded(), is(true));
        assertThat(response.getHttpStatusCode(), is(200));
        assertThat(response.getResult(), is("result"));
    }

    @Test
    public void errorResponseShouldIndicateServerDidNotRespond() throws Exception {
        DropwizardResponse<String> response = DropwizardResponse.of(new RuntimeException());
        assertThat(response.serverResponded(), is(false));
    }

    @Test(expected = IllegalStateException.class)
    public void shouldNotBeAbleToAccessErrorResult() throws Exception {
        DropwizardResponse.of(new RuntimeException()).getResult();
    }

    @Test(expected = IllegalStateException.class)
    public void shouldNotBeAbleToAccessErrorHttpStatusCode() throws Exception {
        DropwizardResponse.of(new RuntimeException()).getHttpStatusCode();
    }
}
