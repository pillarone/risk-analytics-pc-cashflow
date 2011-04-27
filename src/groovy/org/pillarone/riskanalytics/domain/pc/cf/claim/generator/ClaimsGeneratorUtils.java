package org.pillarone.riskanalytics.domain.pc.cf.claim.generator;

import org.joda.time.DateTime;
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimRoot;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimType;
import org.pillarone.riskanalytics.domain.pc.cf.claim.GrossClaimRoot;
import org.pillarone.riskanalytics.domain.utils.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class ClaimsGeneratorUtils {

    public static List<ClaimRoot> generateClaims(IRandomNumberGenerator claimSizeGenerator, IRandomNumberGenerator dateGenerator,
                                      int claimNumber, ClaimType claimType, PeriodScope periodScope) {
        List<ClaimRoot> baseClaims = new ArrayList<ClaimRoot>();
        for (int i = 0; i < claimNumber; i++) {
            double fractionOfPeriod = (Double) dateGenerator.nextValue();
            DateTime occurrenceDate = DateTimeUtilities.getDate(periodScope, fractionOfPeriod);
            // todo(sku): replace with information from underwriting
            DateTime exposureStartDate = occurrenceDate;
            baseClaims.add(new ClaimRoot((Double) claimSizeGenerator.nextValue() * -1, claimType, exposureStartDate, occurrenceDate));
        }
        return baseClaims;
    }

    public static List<ClaimRoot> generateClaims(RandomDistribution distribution, DistributionModified modifier,
                                                 ClaimType claimType, PeriodScope periodScope) {
        IRandomNumberGenerator claimSizeGenerator = RandomNumberGeneratorFactory.getGenerator(distribution, modifier);
        IRandomNumberGenerator dateGenerator = RandomNumberGeneratorFactory.getUniformGenerator();
        return generateClaims(claimSizeGenerator, dateGenerator, 1, claimType, periodScope);
    }
}
