package org.pillarone.riskanalytics.domain.pc.cf.pattern

import org.pillarone.riskanalytics.core.components.Component
import org.pillarone.riskanalytics.core.components.DynamicComposedComponent
import org.pillarone.riskanalytics.core.packets.PacketList

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class PayoutReportingCombinedPatterns extends DynamicComposedComponent {

    PacketList<PatternPacket> outPattern = new PacketList<PatternPacket>(PatternPacket)

    @Override
    Component createDefaultSubComponent() {
        new PayoutReportingCombinedPattern(parmPattern: PayoutReportingCombinedPatternStrategyType.getDefault())
    }

    @Override protected void doCalculation() {
        componentList*.start()
    }

    @Override
    void wire() {
        replicateOutChannels this, 'outPattern'
    }
}
