package se.philips.microservices.core.episode.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import se.philips.microservices.core.episode.model.Episode;
import se.philips.microservices.core.episode.service.util.SetProcTimeBean;

import java.util.ArrayList;
import java.util.List;

@RestController
public class EpisodeService {

    private static final Logger LOG = LoggerFactory.getLogger(EpisodeService.class);

    @Autowired
    private SetProcTimeBean setProcTimeBean;

    /**
     * Sample usage: curl $HOST:$PORT/episode?patientId=1
     *
     * @param patientId
     * @return
     */
    @RequestMapping("/episode")
    public List<Episode> getEpisodes(
            @RequestParam(value = "patientId",  required = true) int patientId) {

        int pt = setProcTimeBean.calculateProcessingTime();
        LOG.info("/episodes called, processing time: {}", pt);

        sleep(pt);

        List<Episode> list = new ArrayList<>();
        list.add(new Episode(patientId, 1, "Acme", "eCAC", "Standard"));
        list.add(new Episode(patientId, 2, "Acme", "eTrAC", "Standard"));
        list.add(new Episode(patientId, 3, "Acme", "eTrAC", "Standard"));

        LOG.info("/episodes response size: {}", list.size());

        return list;
    }

    private void sleep(int pt) {
        try {
            Thread.sleep(pt);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sample usage:
     *
     *  curl "http://localhost:10002/set-processing-time?minMs=1000&maxMs=2000"
     *
     * @param minMs
     * @param maxMs
     */
    @RequestMapping("/set-processing-time")
    public void setProcessingTime(
        @RequestParam(value = "minMs", required = true) int minMs,
        @RequestParam(value = "maxMs", required = true) int maxMs) {

        LOG.info("/set-processing-time called: {} - {} ms", minMs, maxMs);

        setProcTimeBean.setDefaultProcessingTime(minMs, maxMs);
    }
}
