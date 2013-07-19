package org.pillarone.riskanalytics.domain.pc.cf.claim.generator.contractBase;

import org.joda.time.DateTime;
import org.joda.time.Months;
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier;
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope;
import org.pillarone.riskanalytics.domain.pc.cf.claim.GrossClaimRoot;
import org.pillarone.riskanalytics.domain.pc.cf.claim.IClaimRoot;
import org.pillarone.riskanalytics.domain.pc.cf.event.EventPacket;
import org.pillarone.riskanalytics.domain.pc.cf.event.IEvent;
import org.pillarone.riskanalytics.domain.utils.datetime.DateTimeUtilities;
import org.pillarone.riskanalytics.domain.utils.math.generator.IRandomNumberGenerator;
import org.pillarone.riskanalytics.domain.utils.math.generator.RandomNumberGeneratorFactory;

import java.util.ArrayList;
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
     *
     * @param inceptionDate ignored in the context of this strategy
     * @param dateGenerator
     * @param periodScope
     * @param event its date is returned if argument is not null
     * @return generates a new date if event is null or returns the event date
     */
    public DateTime occurrenceDate(DateTime inceptionDate, IRandomNumberGenerator dateGenerator,
                                   PeriodScope periodScope, IEvent event) {
        if (event == null) {
            return DateTimeUtilities.randomDate(inceptionDate, periodScope.getNextPeriodStartDate(), dateGenerator);
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

    public DateTime generateInceptionDate(IClaimRoot aClaim, PeriodScope periodScope) {
        return periodScope.getPeriodCounter().startOfPeriod(aClaim.getOccurrenceDate());
    }


    /**
     *
     * @param claimsAfterUpdate
     * @param periodScope
     * @return identity funtion
     */
    public List<GrossClaimRoot> splitClaims(List<GrossClaimRoot> claimsAfterUpdate, PeriodScope periodScope) {
        List<GrossClaimRoot> claimRoots = new ArrayList<GrossClaimRoot>();
        IRandomNumberGenerator dateGen = RandomNumberGeneratorFactory.getUniformGenerator();
        DateTime inceptionStartDate = periodScope.getCurrentPeriodStartDate();
        int months = Months.monthsBetween(inceptionStartDate, periodScope.getNextPeriodStartDate()).getMonths();
        for (GrossClaimRoot claimRoot : claimsAfterUpdate) {
            double splittedUltimate = claimRoot.getUltimate() / months;
            for (int j = 0; j < months; j++) {
                DateTime occurrenceDate = occurrenceDate(claimRoot.getExposureStartDate(), dateGen, periodScope, claimRoot.getEvent());
                if(claimRoot.getExposureStartDate().isAfter(occurrenceDate)) {
                    claimRoots.add(new GrossClaimRoot(claimRoot, splittedUltimate, claimRoot.getExposureStartDate(), claimRoot.getStartDateForPatterns()));
                } else {
                    claimRoots.add(new GrossClaimRoot(claimRoot, splittedUltimate, occurrenceDate, claimRoot.getStartDateForPatterns()));
                }
            }
        }
        return claimRoots;
    }

    public int getContractLength() {
        return 0;
    }
}
