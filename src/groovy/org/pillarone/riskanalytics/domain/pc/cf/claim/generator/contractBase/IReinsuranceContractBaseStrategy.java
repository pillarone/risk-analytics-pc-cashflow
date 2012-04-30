package org.pillarone.riskanalytics.domain.pc.cf.claim.generator.contractBase;

import org.joda.time.DateTime;
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier;
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope;
import org.pillarone.riskanalytics.domain.pc.cf.event.EventPacket;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket;
import org.pillarone.riskanalytics.domain.utils.math.generator.IRandomNumberGenerator;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public interface IReinsuranceContractBaseStrategy {

    IParameterObjectClassifier getType();

    DateTime occurrenceDate(DateTime inceptionDate, IRandomNumberGenerator dateGenerator,
                            PeriodScope periodScope, EventPacket event);

    /**
     * @param event
     * @return event date
     */
    DateTime occurrenceDate(EventPacket event);

    /**
     * @param underwritingInfo
     * @return underwritingInfo.getExposure().getInceptionDate()
     */
    DateTime inceptionDate(UnderwritingInfoPacket underwritingInfo);

    /**
     * @param dateGenerator
     * @return a generated date
     */
    DateTime inceptionDate(PeriodScope periodScope, IRandomNumberGenerator dateGenerator);

    int splittedClaimsNumber();
}
