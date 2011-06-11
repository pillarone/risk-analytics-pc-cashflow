package org.pillarone.riskanalytics.domain.pc.cf.dependency;

import org.pillarone.riskanalytics.core.components.DynamicComposedComponent
import org.pillarone.riskanalytics.core.packets.PacketList
import org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter
import org.pillarone.riskanalytics.domain.utils.math.copula.CopulaType
import org.pillarone.riskanalytics.domain.utils.marker.ICorrelationMarker

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class Dependencies extends DynamicComposedComponent {

    PacketList<EventDependenceStream> outEventSeverities = new PacketList<EventDependenceStream>(EventDependenceStream)

    public Copula createDefaultSubComponent() {
        return new Copula(parmCopulaStrategy: CopulaType.getDefault())
    }

    protected void doCalculation() {
        for (Copula component: componentList) {
            component.start()
        }
    }

    public void wire() {
        replicateOutChannels this, 'outEventSeverities'
    }

}