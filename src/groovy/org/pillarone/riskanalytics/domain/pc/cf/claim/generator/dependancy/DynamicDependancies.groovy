package org.pillarone.riskanalytics.domain.pc.cf.claim.generator.dependancy

import org.pillarone.riskanalytics.core.packets.PacketList
import org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter
import org.pillarone.riskanalytics.domain.utils.marker.IPerilMarker
import org.pillarone.riskanalytics.core.components.DynamicComposedComponent
import org.pillarone.riskanalytics.domain.utils.math.copula.PerilCopulaType
import org.pillarone.riskanalytics.domain.utils.math.copula.CopulaType
import org.pillarone.riskanalytics.domain.utils.math.dependance.DependancePacket

/**
*   author simon.parten @ art-allianz . com
 */
class DynamicDependancies extends DynamicComposedComponent {

    private PacketList<DependancePacket> outProbabilities = new PacketList(DependancePacket.class);

    public MultiPeriodCopula createDefaultSubComponent() {
        return new MultiPeriodCopula(
                parmCopulaStrategy: CopulaType.getDefault()
        )
    }

    protected void doCalculation() {
        for (MultiPeriodCopula component: componentList) {
            component.start()
        }
    }

    public void wire() {
        replicateOutChannels this, 'outProbabilities'
    }

    PacketList<DependancePacket> getOutProbabilities() {
        return outProbabilities
    }

    void setOutProbabilities(PacketList<DependancePacket> outProbabilities) {
        this.outProbabilities = outProbabilities
    }
}
