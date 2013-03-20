package org.pillarone.riskanalytics.domain.pc.cf.pattern

import org.pillarone.riskanalytics.core.components.ComposedComponent
import org.pillarone.riskanalytics.core.packets.PacketList
import org.pillarone.riskanalytics.core.wiring.PortReplicatorCategory
import org.pillarone.riskanalytics.core.wiring.WiringUtils

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class Patterns extends ComposedComponent {

    PacketList<PatternPacket> outPatterns = new PacketList<PatternPacket>(PatternPacket)

    PremiumPatterns subPremiumPatterns = new PremiumPatterns()
    PayoutReportingCombinedPatterns subPayoutAndReportingPatterns = new PayoutReportingCombinedPatterns()
    RecoveryPatterns subRecoveryPatterns = new RecoveryPatterns()

    @Override protected void doCalculation() {
        subPayoutAndReportingPatterns.start()
        subRecoveryPatterns.start()
        subPremiumPatterns.start()
    }

    @Override
    void wire() {
        WiringUtils.use(PortReplicatorCategory) {
            this.outPatterns = subPayoutAndReportingPatterns.outPattern
            this.outPatterns = subRecoveryPatterns.outPattern
            this.outPatterns = subPremiumPatterns.outPattern
        }
    }
}
