package sample.spring.adc;

import com.sap.cloud.security.adc.client.ADCService;
import com.sap.cloud.security.adc.client.SpringADCService;
import com.sap.cloud.security.adc.spring.ADCSpringSecurityExpressionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class MethodSecurityConfiguration extends GlobalMethodSecurityConfiguration {

	@Autowired(required = false)
	private RestTemplate restTemplate;

	@Value("${ADC_URL:http://localhost:8181}")
	private String adcUrl;

	@Override
	protected MethodSecurityExpressionHandler createExpressionHandler() {
		RestTemplate restTemplate = this.restTemplate != null ? this.restTemplate : new RestTemplate();
		ADCService adcService = new SpringADCService(restTemplate);
		ADCSpringSecurityExpressionHandler expressionHandler =
				ADCSpringSecurityExpressionHandler.getInstance(adcService, URI.create(adcUrl));
		return expressionHandler;
	}
}
