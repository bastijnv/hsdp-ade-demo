package com.philips.microservices.api.patient;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = PatientApiServiceApplication.class)
@WebAppConfiguration
public class PatientApiServiceApplicationTests {

	@Test
	public void contextLoads() {
	}

}
