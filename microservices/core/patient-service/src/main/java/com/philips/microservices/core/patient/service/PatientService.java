package com.philips.microservices.core.patient.service;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.philips.microservices.core.patient.model.Patient;

/**
 * Created by Bastijn Vissers on 16/12/15.
 */
@RestController
public class PatientService {

    /**
     * Sample usage: curl $HOST:$PORT/patient/1
     *
     * @param patientId
     * @return
     */
    @RequestMapping("/patient/{patientId}")
    public Patient getPatient(@PathVariable int patientId) {

        return new Patient(patientId, "name", "01-01-2000");
    }
}
