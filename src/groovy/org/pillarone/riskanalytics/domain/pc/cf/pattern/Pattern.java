package org.pillarone.riskanalytics.domain.pc.cf.pattern;

import org.pillarone.riskanalytics.core.components.Component;
import org.pillarone.riskanalytics.core.packets.PacketList;

import java.util.Collections;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
abstract public class Pattern extends Component {

    protected PacketList<PatternPacket> outPattern = new PacketList<PatternPacket>(PatternPacket.class);
    private IPatternStrategy parmPattern = PatternStrategyType.getStrategy(PatternStrategyType.NONE, Collections.emptyMap());
    private PatternPacket pattern;

    @Override
    protected void doCalculation() {
        initSimulation();
        outPattern.add(pattern);
    }

    private void initSimulation() {
        if (pattern == null) {
            pattern = parmPattern.getPattern(getPatternMarker());
            pattern.setOrigin(this);
        }
    }

    abstract protected Class<? extends IPatternMarker> getPatternMarker();

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
