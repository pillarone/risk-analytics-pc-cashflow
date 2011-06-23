package org.pillarone.riskanalytics.domain.pc.cf.reserve;

import org.joda.time.DateTime;
import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObject;
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier;
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PatternPacket;
import org.pillarone.riskanalytics.domain.utils.datetime.DateTimeUtilities;

import java.util.HashMap;
import java.util.Map;

/**
 * @author jessika.walter (at) intuitive-collaboration (dot) com
 */
public class ReportedBasedReserveCalculationStrategy extends AbstractParameterObject implements IReserveCalculationStrategy {

    private double reportedAtReportingDate;
    private DateTime reportingDate;
    private DateTime averageInceptionDate;
    private InterpolationMode interpolationMode;

    public IParameterObjectClassifier getType() {
        return ReserveCalculationType.REPORTEDBASED;
    }

    public Map getParameters() {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("reportedAtReportingDate", reportedAtReportingDate);
        parameters.put("reportingDate", reportingDate);
        parameters.put("averageInceptionDate", averageInceptionDate);
        parameters.put("interpolationMode", interpolationMode);
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
        }
        if (reportedPortionAtBaseDate == 0){
            throw new IllegalArgumentException("cumulative reported value at base date is zero!");
        }
        return reportedAtReportingDate /reportedPortionAtBaseDate;
    }

    public DateTime getAverageInceptionDate() {
        return averageInceptionDate;
    }

}
