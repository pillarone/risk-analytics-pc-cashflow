package org.pillarone.riskanalytics.domain.pc.cf.reserve;

import org.joda.time.DateTime;
import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObject;
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier;
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PatternPacket;

import java.util.HashMap;
import java.util.Map;

/**
 * @author jessika.walter (at) intuitive-collaboration (dot) com
 */
public class FixedUltimateReserveCalculationStrategy extends AbstractParameterObject implements IReserveCalculationStrategy {

    private double ultimateAtBaseDate;
    private DateTime occurrenceDate;

    public IParameterObjectClassifier getType() {
        return ReserveCalculationType.ULTIMATE;
    }

    public Map getParameters() {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("ultimateAtBaseDate", ultimateAtBaseDate);
        parameters.put("occurrenceDate", occurrenceDate);
        return parameters;
    }

    public Double getUltimate(PatternPacket payoutPattern, PatternPacket reportingPattern){
        return ultimateAtBaseDate;
    }

    public DateTime getOccurrenceDate(){
        return occurrenceDate;
    }

}
