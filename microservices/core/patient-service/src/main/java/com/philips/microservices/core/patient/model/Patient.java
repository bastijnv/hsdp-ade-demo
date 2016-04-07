package com.philips.microservices.core.patient.model;

/**
 * Created by Bastijn Vissers on 16/12/15.
 */
public class Patient {
    private int patientId;
    private String name;
    private String birthDate;

    public Patient() {

    }

    public Patient(int patientId, String name, String birthDate) {
        this.patientId = patientId;
        this.name = name;
        this.birthDate = birthDate;
    }

    public int getPatientId() {
        return patientId;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }
}
