package com.sap.cloud.security.adc.client;

import java.net.URI;

/**
 * TODO: extract as library
 */
public interface ADCService {
    OpenPolicyAgentResponse isUserAuthorized(URI adcUri, OpenPolicyAgentRequest request);
}
