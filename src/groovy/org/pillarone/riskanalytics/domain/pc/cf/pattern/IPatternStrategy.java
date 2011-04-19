package org.pillarone.riskanalytics.domain.pc.cf.pattern;

import org.joda.time.Period;
import org.pillarone.riskanalytics.core.parameterization.IParameterObject;

import java.util.List;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public interface IPatternStrategy extends IParameterObject {
    List<Double> getPatternValues();
    List<Double> getCumulativePatternValues();
    List<Period> getCumulativePeriods();
    int patternLength();
}
