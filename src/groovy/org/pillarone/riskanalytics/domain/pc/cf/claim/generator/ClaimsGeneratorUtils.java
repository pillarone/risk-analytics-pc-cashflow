package org.pillarone.riskanalytics.domain.pc.cf.claim.generator;

import org.joda.time.DateTime;
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimRoot;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimType;
import org.pillarone.riskanalytics.domain.utils.datetime.DateTimeUtilities;
import org.pillarone.riskanalytics.domain.utils.math.distribution.DistributionModified;
import org.pillarone.riskanalytics.domain.utils.math.distribution.RandomDistribution;
import org.pillarone.riskanalytics.domain.utils.math.generator.IRandomNumberGenerator;
import org.pillarone.riskanalytics.domain.utils.math.generator.RandomNumberGeneratorFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class ClaimsGeneratorUtils {

    public static List<ClaimRoot> generateClaims(double severityScaleFactor, IRandomNumberGenerator claimSizeGenerator,
                                                 IRandomNumberGenerator dateGenerator, int claimNumber,
                                                 ClaimType claimType, PeriodScope periodScope) {
        List<ClaimRoot> baseClaims = new ArrayList<ClaimRoot>();
        for (int i = 0; i < claimNumber; i++) {
            double fractionOfPeriod = (Double) dateGenerator.nextValue();
            DateTime occurrenceDate = DateTimeUtilities.getDate(periodScope, fractionOfPeriod);
            // todo(sku): replace with information from underwriting
            DateTime exposureStartDate = occurrenceDate;
            baseClaims.add(new ClaimRoot((Double) claimSizeGenerator.nextValue() * - severityScaleFactor, claimType,
                    exposureStartDate, occurrenceDate));
        }
        return baseClaims;
    }

    public static List<ClaimRoot> generateClaims(double severityScaleFactor, RandomDistribution distribution, DistributionModified modifier,
                                                 ClaimType claimType, PeriodScope periodScope) {
        IRandomNumberGenerator claimSizeGenerator = RandomNumberGeneratorFactory.getGenerator(distribution, modifier);
        IRandomNumberGenerator dateGenerator = RandomNumberGeneratorFactory.getUniformGenerator();
        return generateClaims(severityScaleFactor, claimSizeGenerator, dateGenerator, 1, claimType, periodScope);
    }
}
