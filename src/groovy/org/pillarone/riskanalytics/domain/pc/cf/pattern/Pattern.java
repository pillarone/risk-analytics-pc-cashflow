package org.pillarone.riskanalytics.domain.pc.cf.pattern;

import org.pillarone.riskanalytics.core.components.Component;
import org.pillarone.riskanalytics.core.packets.PacketList;

import java.util.Collections;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class Pattern extends Component {

    private PacketList<PatternPacket> outPattern = new PacketList<PatternPacket>(PatternPacket.class);
    private IPatternStrategy parmPattern = PatternStrategyType.getStrategy(PatternStrategyType.NONE, Collections.emptyMap());
    private PatternPacket pattern;

    @Override
    protected void doCalculation() {
        initSimulation();
        outPattern.add(pattern);
    }

    private void initSimulation() {
        if (pattern == null) {
            pattern = parmPattern.getPattern();
            pattern.setOrigin(this);
        }
    }

    public PacketList<PatternPacket> getOutPattern() {
        return outPattern;
    }

    public void setOutPattern(PacketList<PatternPacket> outPattern) {
        this.outPattern = outPattern;
    }

    public IPatternStrategy getParmPattern() {
        return parmPattern;
    }

    public void setParmPattern(IPatternStrategy parmPattern) {
        this.parmPattern = parmPattern;
    }
}
