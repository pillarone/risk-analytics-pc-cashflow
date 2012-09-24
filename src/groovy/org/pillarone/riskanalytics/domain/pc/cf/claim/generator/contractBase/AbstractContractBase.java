package org.pillarone.riskanalytics.domain.pc.cf.claim.generator.contractBase;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObject;
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope;
import org.pillarone.riskanalytics.domain.pc.cf.event.EventPacket;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket;
import org.pillarone.riskanalytics.domain.utils.math.generator.IRandomNumberGenerator;
import org.pillarone.riskanalytics.domain.utils.math.generator.RandomNumberGeneratorFactory;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
abstract public class AbstractContractBase extends AbstractParameterObject implements IReinsuranceContractBaseStrategy {

    public DateTime occurrenceDate(EventPacket event) {
        return event.getDate();
    }

    /**
     * @param underwritingInfo
     * @return inception date of the exposure of the provided argument
     */
    public DateTime inceptionDate(UnderwritingInfoPacket underwritingInfo) {
        return underwritingInfo.getExposure().getInceptionDate();
    }

    /**
     * Don't split claims by default
     * @return 1
     */
    public int splittedClaimsNumber() {
        return 1;
    }

    public DateTime randomDateInPeriod(PeriodScope periodScope) {
        IRandomNumberGenerator gen = RandomNumberGeneratorFactory.getUniformGenerator();
        DateTime startDate = periodScope.getCurrentPeriodStartDate();
        DateTime endDate = periodScope.getNextPeriodStartDate();
        int daysBetween = Days.daysBetween(startDate, endDate).getDays();
        int randomness = ((int) (gen.nextValue().doubleValue() * (double) daysBetween));
        DateTime randomDate = startDate.plusDays(randomness);
        return randomDate;
    }


}
