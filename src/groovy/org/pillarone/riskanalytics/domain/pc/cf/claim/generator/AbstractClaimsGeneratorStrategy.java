package org.pillarone.riskanalytics.domain.pc.cf.claim.generator;

import org.joda.time.DateTime;
import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObject;
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimRoot;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimType;
import org.pillarone.riskanalytics.domain.utils.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
abstract public class AbstractClaimsGeneratorStrategy extends AbstractParameterObject implements IClaimsGeneratorStrategy {

    private Map<String, IRandomNumberGenerator> cachedClaimSizeGenerators = new HashMap<String, IRandomNumberGenerator>();
    private IRandomNumberGenerator claimSizeGenerator;
    protected IRandomNumberGenerator dateGenerator = RandomNumberGeneratorFactory.getUniformGenerator();

    public List<ClaimRoot> generateClaims(double scaleFactor, ClaimType claimType, PeriodScope periodScope) {
        return generateClaims(scaleFactor, claimType, periodScope);
    }

    protected List<ClaimRoot> generateClaims(double scaleFactor, int claimNumber, ClaimType claimType, PeriodScope periodScope) {
        return ClaimsGeneratorUtils.generateClaims(claimSizeGenerator, dateGenerator, claimNumber, claimType, periodScope);
    }

    protected List<ClaimRoot> getClaims(List<Double> claimValues, ClaimType claimType, PeriodScope periodScope) {
        List<ClaimRoot> baseClaims = new ArrayList<ClaimRoot>();
        for (int i = 0; i < claimValues.size(); i++) {
            double fractionOfPeriod = (Double) dateGenerator.nextValue();
            DateTime occurrenceDate = DateTimeUtilities.getDate(periodScope, fractionOfPeriod);
            // todo(sku): replace with information from underwriting
            DateTime exposureStartDate = occurrenceDate;
            baseClaims.add(new ClaimRoot(claimValues.get(i) * -1, claimType, exposureStartDate, occurrenceDate));
        }
        return baseClaims;
    }

    protected void setGenerator(RandomDistribution distribution, DistributionModified modifier) {
        String key = key(distribution, modifier);
        if (cachedClaimSizeGenerators.containsKey(key)) {
            claimSizeGenerator = cachedClaimSizeGenerators.get(key);
        } else {
            claimSizeGenerator = RandomNumberGeneratorFactory.getGenerator(distribution, modifier);
            cachedClaimSizeGenerators.put(key, claimSizeGenerator);
        }
    }

    protected void setGenerator(RandomDistribution distribution) {
        setGenerator(distribution, DistributionModifier.getStrategy(DistributionModifier.NONE, null));
    }

    protected void setDateGenerator(RandomDistribution distribution) {
        dateGenerator = RandomNumberGeneratorFactory.getGenerator(distribution);
    }

    protected String key(RandomDistribution distribution, DistributionModified modifier) {
        return String.valueOf(distribution.hashCode()) + String.valueOf(modifier.hashCode());
    }

//    public List<ClaimRoot> generateClaims(double scaleFactor, List<EventPacket> events) {
//        return null;  //To change body of implemented methods use File | Settings | File Templates.
//    }

//    public double calculateScaleFactor(List<UnderwritingInfo> underwritingInfos, ExposureBase scales) {
//
//    }

    protected final static String CLAIMS_SIZE_BASE = "claimsSizeBase";
    protected final static String CLAIMS_SIZE_DISTRIBUTION = "claimsSizeDistribution";
    protected final static String CLAIMS_SIZE_MODIFICATION = "claimsSizeModification";
    protected final static String FREQUENCY_BASE = "frequencyBase";
    protected final static String FREQUENCY_DISTRIBUTION = "frequencyDistribution";
    protected final static String FREQUENCY_MODIFICATION = "frequencyModification";
    protected final static String OCCURRENCE_DISTRIBUTION = "occurrenceDistribution";
}
