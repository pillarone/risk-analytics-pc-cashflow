package org.pillarone.riskanalytics.domain.pc.cf.pattern;

import org.pillarone.riskanalytics.core.components.Component;
import org.pillarone.riskanalytics.core.packets.PacketList;

import java.util.Collections;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class PayoutReportingCombinedPattern extends Component implements IPayoutPatternMarker, IReportingPatternMarker {

    private PacketList<PatternPacket> outPattern = new PacketList<PatternPacket>(PatternPacket.class);
    private IPayoutReportingCombinedPatternStrategy parmPattern = PayoutReportingCombinedPatternStrategyType.getStrategy(
            PayoutReportingCombinedPatternStrategyType.NONE, Collections.emptyMap());
    private PatternPacket payoutPattern;
    private PatternPacket reportingPattern;

    @Override
    protected void doCalculation() {
        initSimulation();
        outPattern.add(payoutPattern);
        outPattern.add(reportingPattern);
    }

    private void initSimulation() {
        if (payoutPattern == null) {
            payoutPattern = parmPattern.getPayoutPattern();
            payoutPattern.setOrigin(this);
            reportingPattern = parmPattern.getReportingPattern();
            reportingPattern.setOrigin(this);
        }
    }

    public PacketList<PatternPacket> getOutPattern() {
        return outPattern;
    }

    public void setOutPattern(PacketList<PatternPacket> outPattern) {
        this.outPattern = outPattern;
    }

    public IPayoutReportingCombinedPatternStrategy getParmPattern() {
        return parmPattern;
    }

    public void setParmPattern(IPayoutReportingCombinedPatternStrategy parmPattern) {
        this.parmPattern = parmPattern;
    }
}
