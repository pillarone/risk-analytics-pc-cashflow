package org.pillarone.riskanalytics.domain.pc.cf.creditrisk;

import org.pillarone.riskanalytics.core.packets.Packet;
import org.pillarone.riskanalytics.domain.utils.constant.Rating;

import java.util.HashMap;
import java.util.Map;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class DefaultProbabilities extends Packet {

    public Map<Rating, Double> defaultProbability = new HashMap<Rating, Double>();

    public DefaultProbabilities() {
    }

    public Double getDefaultProbability(Rating rating) {
        return defaultProbability.get(rating);
    }

}
