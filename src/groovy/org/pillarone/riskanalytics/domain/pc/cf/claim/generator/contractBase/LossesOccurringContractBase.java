package org.pillarone.riskanalytics.domain.pc.cf.claim.generator.contractBase;

import org.joda.time.DateTime;
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier;
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope;
import org.pillarone.riskanalytics.domain.pc.cf.claim.GrossClaimRoot;
import org.pillarone.riskanalytics.domain.pc.cf.event.EventPacket;
import org.pillarone.riskanalytics.domain.utils.datetime.DateTimeUtilities;
import org.pillarone.riskanalytics.domain.utils.math.generator.IRandomNumberGenerator;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class LossesOccurringContractBase extends AbstractContractBase implements IReinsuranceContractBaseStrategy {

    public IParameterObjectClassifier getType() {
        return ReinsuranceContractBaseType.LOSSESOCCURRING;
    }


    public Map getParameters() {
        return Collections.emptyMap();
    }

    /**
     *
     * @param inceptionDate ignored in the context of this strategy
     * @param dateGenerator
     * @param periodScope
     * @param event its date is returned if argument is not null
     * @return generates a new date if event is null or returns the event date
     */
    public DateTime occurrenceDate(DateTime inceptionDate, IRandomNumberGenerator dateGenerator,
                                   PeriodScope periodScope, EventPacket event) {
        if (event == null) {
            return DateTimeUtilities.getDate(periodScope, dateGenerator.nextValue().doubleValue());
        }
        else {
            return event.getDate();
        }
    }

    /**
     *
     * @param periodScope current period start date is returned
     * @param dateGenerator ignored in the context of this strategy
     * @return current period start date
     */
    public DateTime exposureStartDate(PeriodScope periodScope, IRandomNumberGenerator dateGenerator) {
        return periodScope.getCurrentPeriodStartDate();
    }


    /**
     * returns the same list passed in
     *
     *
     *
     * @param claimsAfterUpdate
     * @param periodScope
     * @return identity funtion
     */
    public List<GrossClaimRoot> splitClaims(List<GrossClaimRoot> claimsAfterUpdate, PeriodScope periodScope) {
        return claimsAfterUpdate;
    }
}
