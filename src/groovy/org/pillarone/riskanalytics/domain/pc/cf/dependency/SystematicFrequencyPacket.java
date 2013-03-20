package org.pillarone.riskanalytics.domain.pc.cf.dependency;

import org.pillarone.riskanalytics.core.packets.Packet;
import org.pillarone.riskanalytics.domain.utils.math.distribution.DistributionModified;
import org.pillarone.riskanalytics.domain.utils.math.distribution.RandomDistribution;
import org.pillarone.riskanalytics.domain.utils.math.distribution.RandomFrequencyDistribution;

import java.util.List;

/**
 * @author jessika.walter (at) intuitive-collaboration (dot) com
 */
public class SystematicFrequencyPacket extends Packet {

    private RandomFrequencyDistribution frequencyDistribution;
    private DistributionModified frequencyModifier;
    private List<String> targets;

    public RandomFrequencyDistribution getFrequencyDistribution() {
        return frequencyDistribution;
    }

    public void setFrequencyDistribution(RandomFrequencyDistribution frequencyDistribution) {
        this.frequencyDistribution = frequencyDistribution;
    }

    public DistributionModified getFrequencyModifier() {
        return frequencyModifier;
    }

    public void setFrequencyModifier(DistributionModified frequencyModifier) {
        this.frequencyModifier = frequencyModifier;
    }

    public List<String> getTargets() {
        return targets;
    }

    public void setTargets(List<String> targets) {
        this.targets = targets;
    }
}
