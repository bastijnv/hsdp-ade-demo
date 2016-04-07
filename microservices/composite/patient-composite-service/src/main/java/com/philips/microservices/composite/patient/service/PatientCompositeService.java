package com.philips.microservices.composite.patient.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.philips.microservices.composite.patient.model.PatientAggregated;
import com.philips.microservices.util.ServiceUtils;
import com.philips.microservices.core.patient.model.Patient;
import com.philips.microservices.core.observation.model.Observation;
import com.philips.microservices.core.episode.model.Episode;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import java.util.Date;
import java.util.List;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;


@Produces(APPLICATION_JSON)
@Consumes(APPLICATION_JSON)
@RestController
public class PatientCompositeService {

    private static final Logger LOG = LoggerFactory.getLogger(PatientCompositeService.class);

    @Autowired
    PatientCompositeIntegration integration;

    @Autowired
    ServiceUtils util;

    @RequestMapping("/")
    public String getPatient() {
        return "{\"timestamp\":\"" + new Date() + "\",\"content\":\"Hello from PatientAPi\"}";
    }

    @RequestMapping("/patient/{patientId}")
    public ResponseEntity<PatientAggregated> getPatient(@PathVariable int patientId) {

        // 1. First get mandatory patient information
        ResponseEntity<Patient> patientResult = integration.getPatient(patientId);

        if (!patientResult.getStatusCode().is2xxSuccessful()) {
            // We can't proceed, return whatever fault we got from the getPatient call
            return util.createResponse(null, patientResult.getStatusCode());
        }

        // 2. Get optional observations
        List<Observation> observations = null;
        try {
            ResponseEntity<List<Observation>> observationResult = integration.getObservations(patientId);
            if (!observationResult.getStatusCode().is2xxSuccessful()) {
                // Something went wrong with getObservations, simply skip the observation-information in the response
                LOG.debug("Call to getObservations failed: {}", observationResult.getStatusCode());
            } else {
                observations = observationResult.getBody();
            }
        } catch (Throwable t) {
            LOG.error("getPatient error ", t);
            throw t;
        }


        // 3. Get optional episodes
        ResponseEntity<List<Episode>> episodesResult = integration.getEpisodes(patientId);
        List<Episode> episodes = null;
        if (!episodesResult.getStatusCode().is2xxSuccessful()) {
            // Something went wrong with getEpisodes, simply skip the episode-information in the response
            LOG.debug("Call to getEpisodes failed: {}", episodesResult.getStatusCode());
        } else {
            episodes = episodesResult.getBody();
        }

        return util.createOkResponse(new PatientAggregated(patientResult.getBody(), observations, episodes));
    }
}
