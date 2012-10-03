package org.pillarone.riskanalytics.domain.pc.cf.indexing

import org.pillarone.riskanalytics.core.components.ComposedComponent
import org.pillarone.riskanalytics.core.packets.PacketList
import org.pillarone.riskanalytics.core.wiring.PortReplicatorCategory
import org.pillarone.riskanalytics.core.wiring.WiringUtils
import org.pillarone.riskanalytics.domain.pc.cf.dependency.EventDependenceStream

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class Indices extends ComposedComponent {

    PacketList<FactorsPacket> outFactors = new PacketList<FactorsPacket>(FactorsPacket)
    PacketList<EventDependenceStream> inEventSeverities = new PacketList<EventDependenceStream>(EventDependenceStream)

    FrequencyIndices subFrequencyIndices = new FrequencyIndices()
    RunOffIndices subRunOffIndices = new RunOffIndices()
    SeverityIndices subSeverityIndices = new SeverityIndices()
    PolicyIndices subPolicyIndices = new PolicyIndices()
    PremiumIndices subPremiumIndices = new PremiumIndices()

    @Override
    void wire() {
        WiringUtils.use(PortReplicatorCategory) {
            this.outFactors = subRunOffIndices.outFactors
            this.outFactors = subSeverityIndices.outFactors
            this.outFactors = subFrequencyIndices.outFactors
            this.outFactors = subPolicyIndices.outFactors
            this.outFactors = subPremiumIndices.outFactors
            subRunOffIndices.inEventSeverities = this.inEventSeverities
            subSeverityIndices.inEventSeverities = this.inEventSeverities
            subFrequencyIndices.inEventSeverities = this.inEventSeverities
            subPolicyIndices.inEventSeverities = this.inEventSeverities
            subPremiumIndices.inEventSeverities = this.inEventSeverities
        }
    }
}