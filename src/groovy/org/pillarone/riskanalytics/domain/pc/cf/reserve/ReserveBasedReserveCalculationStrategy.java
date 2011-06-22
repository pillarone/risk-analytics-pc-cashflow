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
public class ReserveBasedReserveCalculationStrategy extends AbstractParameterObject implements IReserveCalculationStrategy {

    private double reserveAtBaseDate;
    private DateTime baseDate;
    private DateTime occurrenceDate;
    private InterpolationMode interpolationMode;

    public IParameterObjectClassifier getType() {
        return ReserveCalculationType.RESERVEBASED;
    }

    public Map getParameters() {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("reserveAtBaseDate", reserveAtBaseDate);
        parameters.put("baseDate", baseDate);
        parameters.put("occurrenceDate", occurrenceDate);
        parameters.put("interpolationMode", interpolationMode);
        return parameters;
    }

    public Double getUltimate(PatternPacket payoutPattern, PatternPacket reportingPattern) {
        double numberOfMonths = DateTimeUtilities.deriveNumberOfMonths(occurrenceDate, baseDate);
        double reportedPortionAtBaseDate = 1.0;
        double payoutPortionAtBaseDate = 1.0;
        switch (interpolationMode) {
            case LINEAR:
                reportedPortionAtBaseDate = 1.0 - reportingPattern.outstandingShare(numberOfMonths);
                payoutPortionAtBaseDate = 1.0-payoutPattern.outstandingShare(numberOfMonths);
                break;
            case NONE:
                reportedPortionAtBaseDate = reportingPattern.getCumulativeValues().get(reportingPattern.thisOrLastPayoutIndex(numberOfMonths));
                payoutPortionAtBaseDate = payoutPattern.getCumulativeValues().get(payoutPattern.thisOrLastPayoutIndex(numberOfMonths));
                break;
        }
        if (reportedPortionAtBaseDate == payoutPortionAtBaseDate){
            throw new IllegalArgumentException("outstanding share is zero: Reserve based strategy not possible here!");
        }
        return reserveAtBaseDate/(reportedPortionAtBaseDate-payoutPortionAtBaseDate);
    }

    public DateTime getOccurrenceDate() {
        return occurrenceDate;
    }

}
