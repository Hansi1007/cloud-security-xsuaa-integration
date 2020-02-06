package com.sap.cloud.security.samples;

import com.sap.cloud.security.xsuaa.token.SpringSecurityContext;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    /**
     * An endpoint showing how to use Spring method security.
     * Only if the request principal has the given scope will the
     * method be called. Otherwise a 403 error will be returned.
     */
    @GetMapping(value = "/v1/method")
    @PreAuthorize("action('read')") // AND confidentiality=CONFIDENTIAL
    public String callMethodRemotely() {
        return "Read-protected method called! " + SpringSecurityContext.getToken().getUsername();
    }

}
