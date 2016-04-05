package io.dnbg.newrelic.dropwizard.agent;

import com.google.common.collect.ImmutableMap;
import org.junit.Test;

import java.util.Collections;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class AgentPropertiesTest {
    @Test
    public void shouldParseConfiguredValues() throws Exception {
        ImmutableMap<String, Object> rawProperties = ImmutableMap.<String, Object>of(
                "name", "MyApp",
                "host", "some.host.com",
                "adminPort", 123,
                "adminPath", "/myApp");
        AgentProperties properties = AgentProperties.parse(rawProperties);
        assertThat(properties.getName(), is("MyApp"));
        assertThat(properties.getHost(), is("some.host.com"));
        assertThat(properties.getAdminPort(), is(123));
        assertThat(properties.getAdminPath(), is("/myApp"));
    }

    @Test
    public void adminPortShouldDefaultTo8081() throws Exception {
        AgentProperties properties = AgentProperties.parse(Collections.<String, Object>emptyMap());
        assertThat(properties.getAdminPort(), is(8081));
    }

    @Test
    public void missingNameShouldDefaultToHost() throws Exception {
        AgentProperties properties = AgentProperties.parse(ImmutableMap.<String, Object>of("host", "some.host.com"));
        assertThat(properties.getName(), is("some.host.com"));
    }

    @Test
    public void emptyNameShouldDefaultToHost() throws Exception {
        ImmutableMap<String, Object> rawProperties = ImmutableMap.<String, Object>of(
                "host", "some.host.com",
                "name", "");
        AgentProperties properties = AgentProperties.parse(rawProperties);
        assertThat(properties.getName(), is("some.host.com"));
    }
}