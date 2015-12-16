package se.philips.microservices.composite.patient.model;

public class ObservationSummary {

    private int observationId;
    private String type;
    private int value;

    public ObservationSummary(int observationId, String type, int value) {
        this.observationId = observationId;
        this.type = type;
        this.value = value;
    }

    public int getObservationId() {
        return observationId;
    }

    public String getType() {
        return type;
    }

    public int getValue() {
        return value;
    }
}
