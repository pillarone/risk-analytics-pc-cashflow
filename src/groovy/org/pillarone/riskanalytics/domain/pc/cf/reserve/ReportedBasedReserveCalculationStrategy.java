package org.pillarone.riskanalytics.domain.pc.cf.reserve;

import org.joda.time.DateTime;
import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObject;
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier;
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PatternPacket;
import org.pillarone.riskanalytics.domain.utils.datetime.DateTimeUtilities;

import java.text.BreakIterator;
import java.util.HashMap;
import java.util.Map;

/**
 * @author jessika.walter (at) intuitive-collaboration (dot) com
 */
public class ReportedBasedReserveCalculationStrategy extends AbstractParameterObject implements IReserveCalculationStrategy {

    private double reportedAtBaseDate;
    private DateTime baseDate;
    private DateTime occurrenceDate;
    private UseInterpolation useInterpolation;

    public IParameterObjectClassifier getType() {
        return ReserveCalculationType.REPORTEDBASED;
    }

    public Map getParameters() {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("reportedAtBaseDate", reportedAtBaseDate);
        parameters.put("baseDate", baseDate);
        parameters.put("occurrenceDate", occurrenceDate);
        parameters.put("useInterpolation", useInterpolation);
        return parameters;
    }

    public Double getUltimate(PatternPacket payoutPattern, PatternPacket reportingPattern) {
        double numberOfMonths = DateTimeUtilities.deriveNumberOfMonths(occurrenceDate, baseDate);
        double reportedPortionAtBaseDate = 1.0;
        switch (useInterpolation) {
            case YES:
                reportedPortionAtBaseDate = 1.0 - reportingPattern.outstandingShare(numberOfMonths);
                break;
            case NO:
                reportedPortionAtBaseDate = reportingPattern.getCumulativeValues().get(reportingPattern.thisOrLastPayoutIndex(numberOfMonths));
                break;
        }
        if (reportedPortionAtBaseDate == 0){
            throw new IllegalArgumentException("cumulative reported value at base date is zero!");
        }
        return reportedAtBaseDate/reportedPortionAtBaseDate;
    }

    public DateTime getOccurrenceDate() {
        return occurrenceDate;
    }

}
