package org.pillarone.riskanalytics.domain.pc.cf.dependency;

import org.pillarone.riskanalytics.core.components.DynamicComposedComponent
import org.pillarone.riskanalytics.core.packets.PacketList
import org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter
import org.pillarone.riskanalytics.domain.utils.math.copula.CopulaType
import org.pillarone.riskanalytics.domain.utils.math.copula.ICorrelationMarker

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class Dependencies extends DynamicComposedComponent {

    PacketList<DependenceStream> outProbabilities = new PacketList<DependenceStream>(DependenceStream)

    public Copula createDefaultSubComponent() {
        return new Copula(parmCopulaStrategy: CopulaType.getDefault())
    }

    protected void doCalculation() {
        for (Copula component: componentList) {
            component.start()
        }
    }

    public void wire() {
        replicateOutChannels this, 'outProbabilities'
    }

    public void setOutProbabilities(PacketList<DependenceStream> outProbabilities) {
        this.outProbabilities = outProbabilities;
    }

    public PacketList<DependenceStream> getOutProbabilities() {
        return outProbabilities;
    }
}