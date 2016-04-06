package com.philips.microservices.api.patient.service;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.apache.log4j.MDC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import com.philips.microservices.util.ServiceUtils;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import java.net.URI;
import java.security.Principal;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;


@Produces(APPLICATION_JSON)
@Consumes(APPLICATION_JSON)
@RestController
public class PatientApiService {

    private static final Logger LOG = LoggerFactory.getLogger(PatientApiService.class);

    @Autowired
    ServiceUtils util;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private LoadBalancerClient loadBalancer;

    @RequestMapping("/{patientId}")
    @HystrixCommand(fallbackMethod = "defaultPatientComposite")
    public ResponseEntity<String> getPatientComposite(
        @PathVariable int patientId,
        @RequestHeader(value="Authorization") String authorizationHeader,
        Principal currentUser) {

        MDC.put("patientId", patientId);
        LOG.info("PatientApi: User={}, Auth={}, called with patientId={}", currentUser.getName(), authorizationHeader, patientId);
        URI uri = loadBalancer.choose("patientcomposite").getUri();
        String url = uri.toString() + "/patient/" + patientId;
        LOG.debug("GetPatientComposite from URL: {}", url);

        ResponseEntity<String> result = restTemplate.getForEntity(url, String.class);
        LOG.info("GetPatientComposite http-status: {}", result.getStatusCode());
        LOG.debug("GetPatientComposite body: {}", result.getBody());

        return util.createResponse(result);
    }

    /**
     * Fallback method for getPatientComposite()
     *
     * @param patientId
     * @return
     */
    public ResponseEntity<String> defaultPatientComposite(
        @PathVariable int patientId,
        @RequestHeader(value="Authorization") String authorizationHeader,
        Principal currentUser) {

        LOG.warn("Using fallback method for patient-composite-service. User={}, Auth={}, called with patientId={}", currentUser.getName(), authorizationHeader, patientId);
        return new ResponseEntity<String>("", HttpStatus.BAD_GATEWAY);
    }
}
