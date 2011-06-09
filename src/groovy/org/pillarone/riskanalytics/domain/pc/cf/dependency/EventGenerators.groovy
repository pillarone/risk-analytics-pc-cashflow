package org.pillarone.riskanalytics.domain.pc.cf.dependency

import org.pillarone.riskanalytics.core.packets.PacketList
import org.pillarone.riskanalytics.domain.utils.math.distribution.DistributionModifier
import org.pillarone.riskanalytics.core.components.DynamicComposedComponent
import org.pillarone.riskanalytics.domain.utils.math.distribution.FrequencyDistributionType
import org.pillarone.riskanalytics.domain.utils.math.copula.PerilCopulaType

/**
 * @author jessika.walter (at) intuitive-collaboration (dot) com
 */
class EventGenerators extends DynamicComposedComponent {

    PacketList<EventDependenceStream> outEventSeverities = new PacketList<EventDependenceStream>(EventDependenceStream);
    PacketList<SystematicFrequencyPacket> outEventFrequencies = new PacketList<SystematicFrequencyPacket>(SystematicFrequencyPacket);

    public MultipleProbabilitiesCopula createDefaultSubComponent() {
        MultipleProbabilitiesCopula newComponent = new MultipleProbabilitiesCopula(
                modifier: DistributionModifier.getDefault(), parmFrequencyDistribution: FrequencyDistributionType.getDefault(),
                parmCopulaStrategy: PerilCopulaType.getDefault())
        return newComponent
    }

    protected void doCalculation() {
        for (MultipleProbabilitiesCopula component: componentList) {
            component.start()
        }
    }

    public void wire() {
        replicateOutChannels this, 'outEventSeverities'
        replicateOutChannels this, 'outEventFrequencies'
    }

}
