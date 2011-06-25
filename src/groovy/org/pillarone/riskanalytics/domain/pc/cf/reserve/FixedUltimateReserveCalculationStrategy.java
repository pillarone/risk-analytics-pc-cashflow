package org.pillarone.riskanalytics.domain.pc.cf.reserve;

import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier;
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PatternPacket;

import java.util.Map;

/**
 * @author jessika.walter (at) intuitive-collaboration (dot) com
 */
public class FixedUltimateReserveCalculationStrategy extends AbstractReserveCalculationStrategy {

    private double ultimateAtReportingDate;

    public IParameterObjectClassifier getType() {
        return ReserveCalculationType.ULTIMATE;
    }

    public Map getParameters() {
        Map<String, Object> parameters = super.getParameters();
        parameters.put(ULTIMATE_AT_REPORTING_DATE, ultimateAtReportingDate);
        return parameters;
    }

    public Double getUltimate(PatternPacket payoutPattern, PatternPacket reportingPattern) {
        return ultimateAtReportingDate;
    }

    public static final String ULTIMATE_AT_REPORTING_DATE = "ultimateAtReportingDate";
}
