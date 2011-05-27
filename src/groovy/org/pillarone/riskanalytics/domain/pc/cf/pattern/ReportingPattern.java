package org.pillarone.riskanalytics.domain.pc.cf.pattern;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class ReportingPattern extends Pattern implements IReportingPatternMarker {
    @Override
    protected Class<? extends IPatternMarker> getPatternMarker() {
        return IReportingPatternMarker.class;
    }
}
