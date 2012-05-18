package org.pillarone.riskanalytics.domain.pc.cf.reserve;

import org.apache.commons.lang.NotImplementedException;
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier;
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PatternPacket;
import org.pillarone.riskanalytics.domain.utils.datetime.DateTimeUtilities;

import java.util.Map;

/**
 * @author jessika.walter (at) intuitive-collaboration (dot) com
 */
public class ReportedBasedReserveCalculationStrategy extends AbstractReserveCalculationStrategy {

    private double reportedAtReportingDate;
    private InterpolationMode interpolationMode;

    public IParameterObjectClassifier getType() {
        return ReserveCalculationType.REPORTEDBASED;
    }

    public Map getParameters() {
        Map<String, Object> parameters = super.getParameters();
        parameters.put(REPORTED_AT_REPORTING_DATE, reportedAtReportingDate);
        parameters.put(INTERPOLATION_MODE, interpolationMode);
        return parameters;
    }

    public Double getUltimate(PatternPacket payoutPattern, PatternPacket reportingPattern) {
        double numberOfMonths = DateTimeUtilities.deriveNumberOfMonths(averageInceptionDate, reportingDate);
        double reportedPortionAtBaseDate = 1.0;
        switch (interpolationMode) {
            case LINEAR:
                reportedPortionAtBaseDate = 1.0 - reportingPattern.outstandingShare(numberOfMonths);
                break;
            case NONE:
                reportedPortionAtBaseDate = reportingPattern.getCumulativeValues().get(reportingPattern.thisOrPreviousPayoutIndex(numberOfMonths));
                break;
            default:
                throw new NotImplementedException("InterpolationMode " + interpolationMode.toString() + " not implemented");
        }
        if (reportedPortionAtBaseDate == 0){
            throw new IllegalArgumentException("cumulative reported value at base date is zero!");
        }
        return reportedAtReportingDate /reportedPortionAtBaseDate;
    }

    public static final String REPORTED_AT_REPORTING_DATE = "reportedAtReportingDate";
    public static final String INTERPOLATION_MODE = "interpolationMode";
}
