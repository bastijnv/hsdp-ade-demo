package se.philips.microservices.core.observation.model;

public class Observation {
    private int patientId;
    private int observationId;
    private String type;
    private int value;
    private String date;

    public Observation() {
    }

    public Observation(int patientId, int observationId, String type, int value, String date) {
        this.patientId = patientId;
        this.observationId = observationId;
        this.type = type;
        this.value = value;
        this.date = date;
    }

    public int getPatientId() {
        return patientId;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

    public int getObservationId() {
        return observationId;
    }

    public void setObervationId(int observationId) {
        this.observationId = observationId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
