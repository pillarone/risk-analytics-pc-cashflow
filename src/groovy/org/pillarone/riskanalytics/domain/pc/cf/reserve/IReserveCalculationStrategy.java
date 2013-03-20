package org.pillarone.riskanalytics.domain.pc.cf.reserve;

import org.joda.time.DateTime;
import org.pillarone.riskanalytics.core.parameterization.IParameterObject;
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PatternPacket;

/**
 * @author jessika.walter (at) intuitive-collaboration (dot) com
 */
public interface IReserveCalculationStrategy extends IParameterObject {

    Double getUltimate(PatternPacket payoutPattern, PatternPacket reportingPattern);
    DateTime getReportingDate();
    DateTime getAverageInceptionDate();
}
