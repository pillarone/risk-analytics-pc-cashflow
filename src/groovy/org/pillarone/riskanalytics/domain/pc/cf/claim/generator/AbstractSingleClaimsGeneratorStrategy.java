package org.pillarone.riskanalytics.domain.pc.cf.claim.generator;

import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimRoot;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimType;
import org.pillarone.riskanalytics.domain.utils.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
abstract public class AbstractSingleClaimsGeneratorStrategy extends AbstractClaimsGeneratorStrategy {

    private Map<String, IRandomNumberGenerator> cachedClaimNumberGenerators = new HashMap<String, IRandomNumberGenerator>();
    private IRandomNumberGenerator claimNumberGenerator;

    public List<ClaimRoot> generateClaims(double scaleFactor, ClaimType claimType, PeriodScope periodScope) {
        int numberOfClaims = claimNumberGenerator.nextValue().intValue();
        return generateClaims(scaleFactor, numberOfClaims, claimType, periodScope);
    }

    protected void setClaimNumberGenerator(RandomDistribution distribution, DistributionModified modifier) {
        String key = key(distribution, modifier);
        if (cachedClaimNumberGenerators.containsKey(key)) {
            claimNumberGenerator = cachedClaimNumberGenerators.get(key);
        } else {
            claimNumberGenerator = RandomNumberGeneratorFactory.getGenerator(distribution, modifier);
            cachedClaimNumberGenerators.put(key, claimNumberGenerator);
        }
    }

    protected final static String PRODUCE_CLAIM = "produceClaim";
    protected final static String FREQUENCY_BASE = "frequencyBase";
    protected final static String FREQUENCY_DISTRIBUTION = "frequencyDistribution";
    protected final static String FREQUENCY_MODIFICATION = "frequencyModification";
}
