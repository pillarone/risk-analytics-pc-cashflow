package org.pillarone.riskanalytics.domain.pc.cf.pattern;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class PremiumPattern extends Pattern implements IPremiumPatternMarker {
    @Override
    protected Class<? extends IPatternMarker> getPatternMarker() {
        return IPremiumPatternMarker.class;
    }
}
