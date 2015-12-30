package se.philips.microservices.core.observation.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import se.philips.microservices.core.observation.model.Observation;
import se.philips.microservices.core.observation.service.util.SetProcTimeBean;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import java.util.ArrayList;
import java.util.List;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

/**
 * Created by Bastijn Vissers on 16/12/15.
 */
@Produces(APPLICATION_JSON)
@Consumes(APPLICATION_JSON)
@RestController
public class ObservationService {

    private static final Logger LOG = LoggerFactory.getLogger(ObservationService.class);

    @Autowired
    private SetProcTimeBean setProcTimeBean;

    /**
     * Sample usage: curl $HOST:$PORT/observation?patientId=1
     *
     * @param patientId
     * @return
     */
    @RequestMapping("/observation")
    public List<Observation> getObservations(
            @RequestParam(value = "patientId",  required = true) int patientId) {

        int pt = setProcTimeBean.calculateProcessingTime();
        LOG.info("/observation called, processing time: {}", pt);

        sleep(pt);

        List<Observation> list = new ArrayList<>();
        list.add(new Observation(patientId, 1, "Steps", 100, "16-12-2015"));
        list.add(new Observation(patientId, 2, "HearthRate", 63, "16-12-2015"));
        list.add(new Observation(patientId, 3, "Steps", 400, "17-12-2015"));

        LOG.info("/observation response size: {}", list.size());

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
