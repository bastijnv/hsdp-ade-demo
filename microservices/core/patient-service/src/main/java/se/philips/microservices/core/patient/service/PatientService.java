package se.philips.microservices.core.patient.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import se.philips.microservices.core.patient.model.Patient;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

/**
 * Created by Bastijn Vissers on 16/12/15.
 */
@Produces(APPLICATION_JSON)
@Consumes(APPLICATION_JSON)
@RestController
public class PatientService {

    private static final Logger LOG = LoggerFactory.getLogger(PatientService.class);

    /**
     * Sample usage: curl $HOST:$PORT/patient/1
     *
     * @param patientId
     * @return
     */
    @RequestMapping("/patient/{patientId}")
    public Patient getPatient(@PathVariable int patientId) {
        LOG.info("/patient called");
        return new Patient(patientId, "name", "01-01-2000");
    }
}
