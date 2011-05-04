package org.pillarone.riskanalytics.domain.pc.cf.dependency;

import org.pillarone.riskanalytics.core.packets.Packet;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author jessika.walter (at) intuitive-collaboration (dot) com
 */
public class DependenceStream extends Packet {

    public DependenceStream() {
    }

    public DependenceStream(Map<String, Number> dependenceStream) {
        this.dependenceStream = dependenceStream;
    }

    public DependenceStream(List<String> targets, List<Number> probabilities) {
        Map<String, Number> dependenceStream = new HashMap<String, Number>(targets.size());
        for (int i = 0; i < targets.size(); i++) {
            dependenceStream.put(targets.get(i), probabilities.get(i));
        }
        this.dependenceStream = dependenceStream;
    }

    private Map<String, Number> dependenceStream;

    public Map<String, Number> getDependenceStream() {
        return dependenceStream;
    }

    public void setDependenceStream(Map<String, Number> dependenceStream) {
        this.dependenceStream = dependenceStream;
    }
}
