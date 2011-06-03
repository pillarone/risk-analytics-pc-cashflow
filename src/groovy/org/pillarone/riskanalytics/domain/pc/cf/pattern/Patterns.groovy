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

    PayoutReportingCombinedPatterns subPayoutAndReportingPatterns = new PayoutReportingCombinedPatterns()
    PayoutPatterns subPayoutPatterns = new PayoutPatterns()
    ReportingPatterns subReportingPatterns = new ReportingPatterns()
    RecoveryPatterns subRecoveryPatterns = new RecoveryPatterns()
    PremiumPatterns subPremiumPatterns = new PremiumPatterns()

    @Override protected void doCalculation() {
        subPayoutAndReportingPatterns.start()
        subPayoutPatterns.start()
        subReportingPatterns.start()
        subRecoveryPatterns.start()
        subPremiumPatterns.start()
    }

    @Override
    void wire() {
        WiringUtils.use(PortReplicatorCategory) {
            this.outPatterns = subPayoutAndReportingPatterns.outPattern
            this.outPatterns = subPayoutPatterns.outPattern
            this.outPatterns = subReportingPatterns.outPattern
            this.outPatterns = subRecoveryPatterns.outPattern
            this.outPatterns = subPremiumPatterns.outPattern
        }
    }
}
