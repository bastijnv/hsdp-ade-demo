package se.philips.microservices.core.episode.model;

public class Episode {
    private int patientId;
    private int episodeId;
    private String referral;
    private String tac;
    private String tos;

    public Episode() {
    }

    public Episode(int patientId, int episodeId, String referral, String tac, String tos) {
        this.patientId = patientId;
        this.episodeId = episodeId;
        this.referral = referral;
        this.tac = tac;
        this.tos = tos;
    }

    public int getPatientId() {
        return patientId;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

    public int getEpisodeId() {
        return episodeId;
    }

    public void setEpisodeId(int episodeId) {
        this.episodeId = episodeId;
    }

    public String getReferral() {
        return referral;
    }

    public void setReferral(String referral) {
        this.referral = referral;
    }

    public String getTac() {
        return tac;
    }

    public void setTac(String tac) {
        this.tac = tac;
    }

    public String getTos() {
        return tos;
    }

    public void setTos(String tos) {
        this.tos = tos;
    }
}
