package org.pillarone.riskanalytics.domain.pc.cf.pattern;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class RecoveryPattern extends Pattern implements IRecoveryPatternMarker {
    @Override
    protected Class<? extends IPatternMarker> getPatternMarker() {
        return IRecoveryPatternMarker.class;
    }
}
