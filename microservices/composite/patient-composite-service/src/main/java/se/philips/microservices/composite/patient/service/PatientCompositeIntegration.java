package se.philips.microservices.composite.patient.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import se.philips.microservices.core.patient.model.Patient;
import se.philips.microservices.core.observation.model.Observation;
import se.philips.microservices.core.episode.model.Episode;

import java.io.IOException;
import java.net.URI;
import java.util.List;

@Component
public class PatientCompositeIntegration {

    private static final Logger LOG = LoggerFactory.getLogger(PatientCompositeIntegration.class);

    @Autowired
    Util util;

    private RestTemplate restTemplate = new RestTemplate();

    // -------- //
    // PATIENTS //
    // -------- //

    public ResponseEntity<Patient> getPatient(int patientId) {

        URI uri = util.getServiceUrl("patient", "http://localhost:8081/patient");
        String url = uri.toString() + "/patient/" + patientId;
        LOG.debug("GetPatient from URL: {}", url);

        ResponseEntity<String> resultStr = restTemplate.getForEntity(url, String.class);
        LOG.debug("GetPatient http-status: {}", resultStr.getStatusCode());
        LOG.debug("GetPatient body: {}", resultStr.getBody());

        Patient patient = response2Patient(resultStr);
        LOG.debug("GetPatient.id: {}", patient.getPatientId());

        return util.createOkResponse(patient);
    }

    // --------------- //
    // OBSERVATIONS    //
    // --------------- //

    public ResponseEntity<List<Observation>> getObservations(int patientId) {
        try {
            LOG.info("GetObservations...");

            URI uri = util.getServiceUrl("observation", "http://localhost:8081/observation");

            String url = uri.toString() + "/observation?patientId=" + patientId;
            LOG.debug("GetObservations from URL: {}", url);

            ResponseEntity<String> resultStr = restTemplate.getForEntity(url, String.class);
            LOG.debug("GetObservations http-status: {}", resultStr.getStatusCode());
            LOG.debug("GetObservations body: {}", resultStr.getBody());

            List<Observation> observations = response2Observations(resultStr);
            LOG.debug("GetObservations.cnt {}", observations.size());

            return util.createOkResponse(observations);
        } catch (Throwable t) {
            LOG.error("GetObservations error", t);
            throw t;
        }
    }

    // -------- //
    // EPISODES //
    // -------- //

    public ResponseEntity<List<Episode>> getEpisodes(int patientId) {
        LOG.info("GetEpisodes...");

        URI uri = util.getServiceUrl("episode", "http://localhost:8081/episode");

        String url = uri.toString() + "/episode?patientId=" + patientId;
        LOG.debug("GetEpisodes from URL: {}", url);

        ResponseEntity<String> resultStr = restTemplate.getForEntity(url, String.class);
        LOG.debug("GetEpisodes http-status: {}", resultStr.getStatusCode());
        LOG.debug("GetEpisodes body: {}", resultStr.getBody());

        List<Episode> episodes = response2Episodes(resultStr);
        LOG.debug("GetEpisodes.cnt {}", episodes.size());

        return util.createOkResponse(episodes);
    }

    // ----- //
    // UTILS //
    // ----- //

    private ObjectReader patientReader = null;
    private ObjectReader getPatientReader() {

        if (patientReader != null) return patientReader;

        ObjectMapper mapper = new ObjectMapper();
        return patientReader = mapper.reader(Patient.class);
    }

    private ObjectReader episodesReader = null;
    private ObjectReader getEpisodesReader() {
        if (episodesReader != null) return episodesReader;

        ObjectMapper mapper = new ObjectMapper();
        return episodesReader = mapper.reader(new TypeReference<List<Episode>>() {});
    }

    public Patient response2Patient(ResponseEntity<String> response) {
        try {
            return getPatientReader().readValue(response.getBody());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // TODO: Gereralize with <T> method, skip objectReader objects!
    private List<Observation> response2Observations(ResponseEntity<String> response) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            List list = mapper.readValue(response.getBody(), new TypeReference<List<Observation>>() {});
            List<Observation> observations = list;
            return observations;

        } catch (IOException e) {
            LOG.warn("IO-err. Failed to read JSON", e);
            throw new RuntimeException(e);

        } catch (RuntimeException re) {
            LOG.warn("RTE-err. Failed to read JSON", re);
            throw re;
        }
    }

    private List<Episode> response2Episodes(ResponseEntity<String> response) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            List list = mapper.readValue(response.getBody(), new TypeReference<List<Episode>>() {});
            List<Episode> episodes = list;
            return episodes;

        } catch (IOException e) {
            LOG.warn("IO-err. Failed to read JSON", e);
            throw new RuntimeException(e);

        } catch (RuntimeException re) {
            LOG.warn("RTE-err. Failed to read JSON", re);
            throw re;
        }
    }
}
