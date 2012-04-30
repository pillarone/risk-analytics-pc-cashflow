package org.pillarone.riskanalytics.domain.pc.cf.claim.generator;

import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimRoot;
import org.pillarone.riskanalytics.domain.pc.cf.claim.generator.contractBase.DefaultContractBase;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.ExposureBase;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.FrequencyBase;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoUtils;
import org.pillarone.riskanalytics.domain.pc.cf.indexing.Factors;
import org.pillarone.riskanalytics.domain.pc.cf.indexing.IndexUtils;
import org.pillarone.riskanalytics.domain.utils.math.distribution.*;
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
                                          ExposureBase severityBase, FrequencyBase frequencyBase,
                                          List<Factors> factors, PeriodScope periodScope) {
        double frequencyScalingFactor = UnderwritingInfoUtils.scalingFactor(uwInfos, frequencyBase, uwInfosFilterCriteria);
        int numberOfClaims = 0;
        for (int i = 0; i < (int) frequencyScalingFactor; i++) {
            numberOfClaims += claimNumberGenerator.nextValue().intValue();
        }
        numberOfClaims = calculateNumberOfClaimsWithAppliedIndices(numberOfClaims, periodScope, factors);
        double severityScalingFactor = UnderwritingInfoUtils.scalingFactor(uwInfos, severityBase, uwInfosFilterCriteria);
        return generateClaims(severityScalingFactor, numberOfClaims, periodScope, new DefaultContractBase());
    }

    public List<ClaimRoot> generateClaims(List<UnderwritingInfoPacket> uwInfos, List uwInfosFilterCriteria,
                                          ExposureBase severityBase, List<Factors> factors, PeriodScope periodScope) {
        int numberOfClaims = claimNumberGenerator.nextValue().intValue();
        numberOfClaims = calculateNumberOfClaimsWithAppliedIndices(numberOfClaims, periodScope, factors);
        double severityScalingFactor = UnderwritingInfoUtils.scalingFactor(uwInfos, severityBase, uwInfosFilterCriteria);
        return generateClaims(severityScalingFactor, numberOfClaims, periodScope, new DefaultContractBase());
    }

    private int calculateNumberOfClaimsWithAppliedIndices(int numberOfClaims, PeriodScope periodScope, List<Factors> factors) {
        numberOfClaims *= IndexUtils.aggregateFactor(factors, periodScope.getCurrentPeriodStartDate());
        return numberOfClaims;
    }

    protected void setClaimNumberGenerator(AbstractRandomDistribution distribution, DistributionModified modifier) {
        String key = key(distribution, modifier);
        if (cachedClaimNumberGenerators.containsKey(key)) {
            claimNumberGenerator = cachedClaimNumberGenerators.get(key);
        }
        else {
            claimNumberGenerator = RandomNumberGeneratorFactory.getGenerator(distribution, modifier);
            cachedClaimNumberGenerators.put(key, claimNumberGenerator);
        }
    }

    protected void setClaimNumberGenerator(AbstractRandomDistribution distribution) {
        setClaimNumberGenerator(distribution, DistributionModifier.getStrategy(DistributionModifier.NONE, null));
    }

    protected final static String PRODUCE_CLAIM = "produceClaim";
    protected final static String FREQUENCY_INDICES = "frequencyIndices";

}
