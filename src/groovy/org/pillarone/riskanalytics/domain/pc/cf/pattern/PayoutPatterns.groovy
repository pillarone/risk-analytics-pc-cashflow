package org.pillarone.riskanalytics.domain.pc.cf.pattern

import org.pillarone.riskanalytics.core.components.Component
import org.pillarone.riskanalytics.core.components.DynamicComposedComponent
import org.pillarone.riskanalytics.core.packets.PacketList

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class PayoutPatterns extends DynamicComposedComponent {

    PacketList<PatternPacket> outPattern = new PacketList<PatternPacket>(PatternPacket)

    @Override
    Component createDefaultSubComponent() {
        Pattern pattern = new PayoutPattern(parmPattern: PatternStrategyType.getDefault())
        return pattern
    }

    @Override protected void doCalculation() {
        for (Pattern pattern : componentList) {
            pattern.start()
        }
    }

    @Override
    void wire() {
        replicateOutChannels this, 'outPattern'
    }
}
