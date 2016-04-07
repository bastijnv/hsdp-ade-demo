package com.philips.microservices.composite.patient.model;

import com.philips.microservices.core.patient.model.Patient;
import com.philips.microservices.core.observation.model.Observation;
import com.philips.microservices.core.episode.model.Episode;

import java.util.List;
import java.util.stream.Collectors;

public class PatientAggregated {
    private int patientId;
    private String name;
    private String birthDate;
    private List<ObservationSummary> observations;
    private List<EpisodeSummary> episodes;

    public PatientAggregated(Patient patient, List<Observation> observations, List<Episode> episodes) {

        // 1. Setup patient info
        this.patientId = patient.getPatientId();
        this.name = patient.getName();
        this.birthDate = patient.getBirthDate();

        // 2. Copy summary observation info, if available
        if (observations != null)
            this.observations = observations.stream()
                .map(r -> new ObservationSummary(r.getObservationId(), r.getType(), r.getValue()))
                .collect(Collectors.toList());

        // 3. Copy summary episode info, if available
        if (episodes != null)
            this.episodes = episodes.stream()
                .map(r -> new EpisodeSummary(r.getEpisodeId(), r.getReferral(), r.getTac()))
                .collect(Collectors.toList());
    }

    public int getPatientId() {
        return patientId;
    }

    public String getName() {
        return name;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public List<ObservationSummary> getObservations() {
        return observations;
    }

    public List<EpisodeSummary> getEpisodes() {
        return episodes;
    }
}
