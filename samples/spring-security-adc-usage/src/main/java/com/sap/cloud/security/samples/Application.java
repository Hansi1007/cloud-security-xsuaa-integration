package com.sap.cloud.security.samples;

import com.sap.cloud.security.adc.ADCExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {
	private static Logger LOGGER = LoggerFactory.getLogger(Application.class);

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);

		// ADC_URL TODO for local testing... Docker
		try {
			LOGGER.info("Start OPA");
			ADCExecutor.get().start();
			//OpenPolicyAgentExecutor.get().ping();
		} catch (Exception e){
			LOGGER.error("OPA Start error: {}", e.getMessage());
		}
	}
}
