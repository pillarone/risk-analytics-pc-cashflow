package org.pillarone.riskanalytics.domain.pc.cf.reserve;

import org.joda.time.DateTime;
import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObject;
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier;

import java.util.HashMap;
import java.util.Map;

/**
 * @author jessika.walter (at) intuitive-collaboration (dot) com
 */
public abstract class AbstractReserveCalculationStrategy extends AbstractParameterObject implements IReserveCalculationStrategy {

    protected DateTime reportingDate;
    protected DateTime averageInceptionDate;

    public IParameterObjectClassifier getType() {
        return ReserveCalculationType.ULTIMATE;
    }

    public Map getParameters() {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(REPORTING_DATE, reportingDate);
        parameters.put(AVERAGE_INCEPTION_DATE, averageInceptionDate);
        return parameters;
    }

    public DateTime getReportingDate() {
        return reportingDate;
    }

    public DateTime getAverageInceptionDate() {
        return averageInceptionDate;
    }

    public static final String REPORTING_DATE = "reportingDate";
    public static final String AVERAGE_INCEPTION_DATE = "averageInceptionDate";


}
