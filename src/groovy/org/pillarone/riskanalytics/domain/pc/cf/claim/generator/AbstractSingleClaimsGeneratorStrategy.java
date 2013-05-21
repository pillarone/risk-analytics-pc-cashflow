package org.pillarone.riskanalytics.domain.pc.cf.claim.generator;

import org.joda.time.DateTime;
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimRoot;
import org.pillarone.riskanalytics.domain.pc.cf.claim.generator.contractBase.LossesOccurringContractBase;
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

    public List<ClaimRoot> generateClaims(List<UnderwritingInfoPacket> uwInfos, List<Factors> severityFacotrs,
                                          List uwInfosFilterCriteria, ExposureBase severityBase, FrequencyBase frequencyBase,
                                          List<Factors> factors, PeriodScope periodScope) {
        double frequencyScalingFactor = UnderwritingInfoUtils.scalingFactor(uwInfos, frequencyBase, uwInfosFilterCriteria);
        DateTime currentPeriodStartDate = periodScope.getCurrentPeriodStartDate();
        double indexFactor = IndexUtils.aggregateFactor(factors, currentPeriodStartDate, periodScope.getPeriodCounter(), currentPeriodStartDate);
        int numberOfClaims = claimNumberGenerator.nextValue(frequencyScalingFactor * indexFactor).intValue();
        double severityScalingFactor = UnderwritingInfoUtils.scalingFactor(uwInfos, severityBase, uwInfosFilterCriteria);
        return generateClaims(severityScalingFactor, severityFacotrs, numberOfClaims, periodScope, new LossesOccurringContractBase());
    }

    public List<ClaimRoot> generateClaims(List<UnderwritingInfoPacket> uwInfos, List<Factors> severityFacotrs, List uwInfosFilterCriteria,
                                          ExposureBase severityBase, List<Factors> factors, PeriodScope periodScope) {
        int numberOfClaims = claimNumberGenerator.nextValue().intValue();
        numberOfClaims = calculateNumberOfClaimsWithAppliedIndices(numberOfClaims, periodScope, factors);
        double severityScalingFactor = UnderwritingInfoUtils.scalingFactor(uwInfos, severityBase, uwInfosFilterCriteria);
        return generateClaims(severityScalingFactor, severityFacotrs, numberOfClaims, periodScope, new LossesOccurringContractBase());
    }

    private int calculateNumberOfClaimsWithAppliedIndices(int numberOfClaims, PeriodScope periodScope, List<Factors> factors) {
        numberOfClaims *= IndexUtils.aggregateFactor(factors, periodScope.getCurrentPeriodStartDate(), periodScope.getPeriodCounter(), periodScope.getCurrentPeriodStartDate());
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
