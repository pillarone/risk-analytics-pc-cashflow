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

    private double ultimateAtReportingDate;
    private DateTime reportingDate;
    private DateTime averageInceptionDate;

    public IParameterObjectClassifier getType() {
        return ReserveCalculationType.ULTIMATE;
    }

    public Map getParameters() {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("ultimateAtReportingDate", ultimateAtReportingDate);
        parameters.put("reportingDate", reportingDate);
        parameters.put("averageInceptionDate", averageInceptionDate);
        return parameters;
    }

    public Double getUltimate(PatternPacket payoutPattern, PatternPacket reportingPattern) {
        return ultimateAtReportingDate;
    }

    public DateTime getAverageInceptionDate() {
        return averageInceptionDate;
    }

}
