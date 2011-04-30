package org.pillarone.riskanalytics.domain.pc.cf.claim.generator;

import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimRoot;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimType;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.ExposureBase;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.FrequencyBase;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoUtils;
import org.pillarone.riskanalytics.domain.utils.math.distribution.DistributionModified;
import org.pillarone.riskanalytics.domain.utils.math.distribution.DistributionModifier;
import org.pillarone.riskanalytics.domain.utils.math.distribution.RandomDistribution;
import org.pillarone.riskanalytics.domain.utils.math.generator.IRandomNumberGenerator;
import org.pillarone.riskanalytics.domain.utils.math.generator.RandomNumberGeneratorFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
abstract public class AbstractSingleClaimsGeneratorStrategy extends AbstractClaimsGeneratorStrategy {

    private Map<String, IRandomNumberGenerator> cachedClaimNumberGenerators = new HashMap<String, IRandomNumberGenerator>();
    private IRandomNumberGenerator claimNumberGenerator;

    public List<ClaimRoot> generateClaims(List<UnderwritingInfoPacket> uwInfos, List uwInfosFilterCriteria,
                                          ExposureBase severityBase, FrequencyBase frequencyBase, ClaimType claimType,
                                          PeriodScope periodScope) {
        double frequencyScalingFactor = UnderwritingInfoUtils.scalingFactor(uwInfos, frequencyBase, uwInfosFilterCriteria);
        int numberOfClaims = (int) (claimNumberGenerator.nextValue().intValue() * frequencyScalingFactor);
        double severityScalingFactor = UnderwritingInfoUtils.scalingFactor(uwInfos, severityBase, uwInfosFilterCriteria);
        return generateClaims(severityScalingFactor, numberOfClaims, claimType, periodScope);
    }

    public List<ClaimRoot> generateClaims(ClaimType claimType, PeriodScope periodScope) {
        int numberOfClaims = claimNumberGenerator.nextValue().intValue();
        return generateClaims(1, numberOfClaims, claimType, periodScope);
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

    protected void setClaimNumberGenerator(RandomDistribution distribution) {
        setClaimNumberGenerator(distribution, DistributionModifier.getStrategy(DistributionModifier.NONE, null));
    }

    protected final static String PRODUCE_CLAIM = "produceClaim";

}
