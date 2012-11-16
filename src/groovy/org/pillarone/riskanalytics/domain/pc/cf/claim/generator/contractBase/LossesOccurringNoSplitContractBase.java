package org.pillarone.riskanalytics.domain.pc.cf.claim.generator.contractBase;

import org.joda.time.DateTime;
import org.joda.time.Months;
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier;
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope;
import org.pillarone.riskanalytics.domain.pc.cf.claim.GrossClaimRoot;
import org.pillarone.riskanalytics.domain.pc.cf.event.EventPacket;
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
public class LossesOccurringNoSplitContractBase extends LossesOccurringContractBase implements IReinsuranceContractBaseStrategy {

    public IParameterObjectClassifier getType() {
        return ReinsuranceContractBaseType.LOSSESOCCURRING_NO_SPLIT;
    }

    /**
     * @param claimsAfterUpdate
     * @param periodScope
     * @return identity funtion
     */
    public List<GrossClaimRoot> splitClaims(List<GrossClaimRoot> claimsAfterUpdate, PeriodScope periodScope) {
        List<GrossClaimRoot> claimRoots = new ArrayList<GrossClaimRoot>();
        IRandomNumberGenerator dateGen = RandomNumberGeneratorFactory.getUniformGenerator();
        for (GrossClaimRoot claimRoot : claimsAfterUpdate) {
            DateTime occurrenceDate = occurrenceDate(claimRoot.getExposureStartDate(), dateGen, periodScope, claimRoot.getEvent());
            if (claimRoot.getExposureStartDate().isAfter(occurrenceDate)) {
                claimRoots.add(new GrossClaimRoot(claimRoot, claimRoot.getUltimate(), claimRoot.getExposureStartDate(), claimRoot.getStartDateForPatterns()));
            } else {
                claimRoots.add(new GrossClaimRoot(claimRoot, claimRoot.getUltimate(), occurrenceDate, claimRoot.getStartDateForPatterns()));
            }
        }
        return claimRoots;
    }
}
