package com.philips.microservices.composite.patient.model;

public class EpisodeSummary {

    private int episodeId;
    private String referral;
    private String tac;

    public EpisodeSummary(int episodeId, String referral, String tac) {
        this.episodeId = episodeId;
        this.referral = referral;
        this.tac = tac;
    }

    public int getEpisodeId() {
        return episodeId;
    }

    public String getReferral() {
        return referral;
    }

    public String getTac() {
        return tac;
    }
}
