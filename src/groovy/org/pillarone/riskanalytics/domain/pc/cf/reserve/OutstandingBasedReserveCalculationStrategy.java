package org.pillarone.riskanalytics.domain.pc.cf.reserve;

import org.apache.commons.lang.NotImplementedException;
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier;
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PatternPacket;
import org.pillarone.riskanalytics.domain.utils.datetime.DateTimeUtilities;

import java.util.HashMap;
import java.util.Map;

/**
 * @author jessika.walter (at) intuitive-collaboration (dot) com
 */
public class OutstandingBasedReserveCalculationStrategy extends AbstractReserveCalculationStrategy {

    private double outstandingAtReportingDate;
    private InterpolationMode interpolationMode;

    public IParameterObjectClassifier getType() {
        return ReserveCalculationType.OUTSTANDINGBASED;
    }

    public Map getParameters() {
        Map<String, Object> parameters = super.getParameters();
        parameters.put(OUTSTANDING_AT_REPORTING_DATE, outstandingAtReportingDate);
        parameters.put(INTERPOLATION_MODE, interpolationMode);
        return parameters;
    }

    public Double getUltimate(PatternPacket payoutPattern, PatternPacket reportingPattern) {
        double numberOfMonths = DateTimeUtilities.deriveNumberOfMonths(averageInceptionDate, reportingDate);
        double reportedPortionAtBaseDate = 1.0;
        double payoutPortionAtBaseDate = 1.0;
        switch (interpolationMode) {
            case LINEAR:
                reportedPortionAtBaseDate = 1.0 - reportingPattern.outstandingShare(numberOfMonths);
                payoutPortionAtBaseDate = 1.0 - payoutPattern.outstandingShare(numberOfMonths);
                break;
            case NONE:
                reportedPortionAtBaseDate = reportingPattern.getCumulativeValues().get(reportingPattern.thisOrPreviousPayoutIndex(numberOfMonths));
                payoutPortionAtBaseDate = payoutPattern.getCumulativeValues().get(payoutPattern.thisOrPreviousPayoutIndex(numberOfMonths));
                break;
            default:
                throw new NotImplementedException("InterpolationMode " + interpolationMode.toString() + " not implemented");
        }
        if (reportedPortionAtBaseDate == payoutPortionAtBaseDate) {
            throw new IllegalArgumentException("outstandingIndexed share is zero: Reserve based strategy not possible here!");
        }
        return outstandingAtReportingDate / (reportedPortionAtBaseDate - payoutPortionAtBaseDate);
    }

    public static final String OUTSTANDING_AT_REPORTING_DATE = "outstandingAtReportingDate";
    public static final String INTERPOLATION_MODE = "interpolationMode";
}
