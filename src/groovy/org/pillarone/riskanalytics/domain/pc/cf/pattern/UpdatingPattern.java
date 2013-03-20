package org.pillarone.riskanalytics.domain.pc.cf.pattern;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class UpdatingPattern extends Pattern implements IUpdatingPatternMarker {
    @Override
    protected Class<? extends IPatternMarker> getPatternMarker() {
        return IUpdatingPatternMarker.class;
    }
}
