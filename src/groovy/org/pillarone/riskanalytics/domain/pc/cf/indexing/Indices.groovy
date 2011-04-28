package org.pillarone.riskanalytics.domain.pc.cf.indexing

import org.pillarone.riskanalytics.core.wiring.PortReplicatorCategory
import org.pillarone.riskanalytics.core.wiring.WiringUtils
import org.pillarone.riskanalytics.core.packets.PacketList
import org.pillarone.riskanalytics.core.components.ComposedComponent

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class Indices extends ComposedComponent {

    PacketList<FactorsPacket> outFactors = new PacketList<FactorsPacket>(FactorsPacket)

    SeverityIndices subSeverityIndices = new SeverityIndices()
    FrequencyIndices subFrequencyIndices = new FrequencyIndices()

    @Override protected void doCalculation() {
        subSeverityIndices.start()
        subFrequencyIndices.start()
    }

    @Override
    void wire() {
        WiringUtils.use(PortReplicatorCategory) {
            this.outFactors = subSeverityIndices.outFactors
            this.outFactors = subFrequencyIndices.outFactors
        }
    }
}