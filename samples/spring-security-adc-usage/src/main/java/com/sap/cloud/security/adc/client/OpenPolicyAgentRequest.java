package com.sap.cloud.security.adc.client;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TODO: extract as library
 */
public class OpenPolicyAgentRequest {
    private static final String USER = "user";
    private static final String ACTIONS = "actions";
    private static final String ATTRIBUTES = "attributes";
    private static final String ANY = "ANY";
    private static final String ALL = "ALL";

    private Map<String, Object> input = new HashMap<>();

    public OpenPolicyAgentRequest(String uniqueUserId) {
        input.put(USER, uniqueUserId);
    }

    public OpenPolicyAgentRequest withAnyAction(String... actions) {
        input.put(ACTIONS, new Actions(ANY, actions));
        return this;
    }

    public OpenPolicyAgentRequest withAttributeValue(String name, String value) {
        input.put(ATTRIBUTES, new Attribute(ANY, name, value));
        return this;
    }

    public static class Attribute {
        public final String name;
        public final List<String> values;

        public Attribute(String name, String... values) {
            this.name = name;
            this.values = Arrays.asList(values);
        }
    }

    public static class Actions {
        public final String aggregation;
        public final List<String> names;

        public Actions(String aggregation, String... actions) {
            this.aggregation = aggregation;
            this.names = Arrays.asList(actions);
        }
    }

    /**
     * Required for HttpMessageConverter.
     * @return
     */
    public Map<String, Object> getInput() {
        return this.input;
    }

}