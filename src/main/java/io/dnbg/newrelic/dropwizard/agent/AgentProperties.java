package io.dnbg.newrelic.dropwizard.agent;

import com.google.common.base.MoreObjects;
import com.google.common.base.Strings;

import java.util.Map;

class AgentProperties {
    private Map<String, Object> properties;

    static AgentProperties parse(Map<String, Object> properties) {
        AgentProperties agentProperties = new AgentProperties();
        agentProperties.properties = properties;
        return agentProperties;
    }

    private AgentProperties() {
    }

    String getName() {
        String name = (String) properties.get("name");
        return Strings.isNullOrEmpty(name) ? getHost() : name;
    }

    String getHost() {
        return (String) properties.get("host");
    }

    int getAdminPort() {
        if (properties.containsKey("adminPort")) {
            return ((Number) properties.get("adminPort")).intValue();
        } else {
            return 8081; // DW default
        }
    }

    String getAdminPath() {
        return Strings.nullToEmpty((String) properties.get("adminPath"));
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .addValue(properties)
                .toString();
    }
}
