package org.pillarone.riskanalytics.domain.pc.cf.claim.generator;

import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier;
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimRoot;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimType;
import org.pillarone.riskanalytics.domain.pc.cf.dependency.EventDependenceStream;
import org.pillarone.riskanalytics.domain.pc.cf.dependency.SystematicFrequencyPacket;
import org.pillarone.riskanalytics.domain.pc.cf.event.EventSeverity;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.FrequencyBase;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoUtils;
import org.pillarone.riskanalytics.domain.pc.cf.indexing.FactorsPacket;
import org.pillarone.riskanalytics.domain.utils.marker.IPerilMarker;
import org.pillarone.riskanalytics.domain.utils.math.distribution.DistributionModified;
import org.pillarone.riskanalytics.domain.utils.math.distribution.FrequencyDistributionUtils;
import org.pillarone.riskanalytics.domain.utils.math.distribution.RandomFrequencyDistribution;
import org.pillarone.riskanalytics.domain.utils.math.generator.IRandomNumberGenerator;
import org.pillarone.riskanalytics.domain.utils.math.generator.RandomNumberGeneratorFactory;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class FrequencyAverageAttritionalClaimsGeneratorStrategy extends AttritionalClaimsGeneratorStrategy {

    private FrequencyBase frequencyBase;
    private RandomFrequencyDistribution frequencyDistribution;
    private DistributionModified frequencyModification;

    public IParameterObjectClassifier getType() {
        return ClaimsGeneratorType.FREQUENCY_AVERAGE_ATTRITIONAL;
    }

    public Map getParameters() {
        Map<String, Object> parameters = super.getParameters();
        parameters.put(FREQUENCY_BASE, frequencyBase);
        parameters.put(FREQUENCY_DISTRIBUTION, frequencyDistribution);
        parameters.put(FREQUENCY_MODIFICATION, frequencyModification);
        return parameters;
    }

    public List<ClaimRoot> generateClaims(List<ClaimRoot> baseClaims, List<UnderwritingInfoPacket> uwInfos,
                                          List uwInfosFilterCriteria, List<FactorsPacket> factorsPackets,
                                          PeriodScope periodScope, List<SystematicFrequencyPacket> systematicFrequencies,
                                          IPerilMarker filterCriteria) {

        double severityScalingFactor = UnderwritingInfoUtils.scalingFactor(uwInfos, claimsSizeBase, uwInfosFilterCriteria);
        double frequencyFactor = UnderwritingInfoUtils.scalingFactor(uwInfos, frequencyBase, uwInfosFilterCriteria);
        RandomFrequencyDistribution systematicFrequencyDistribution = ClaimsGeneratorUtils.extractFrequencyDistribution(systematicFrequencies, filterCriteria);
        IRandomNumberGenerator frequencyGenerator = RandomNumberGeneratorFactory.getGenerator(
                FrequencyDistributionUtils.getIdiosyncraticDistribution(frequencyDistribution, systematicFrequencyDistribution), frequencyModification);
        IRandomNumberGenerator claimsSizeGenerator = RandomNumberGeneratorFactory.getGenerator(claimsSizeDistribution, claimsSizeModification);
        int numberOfClaims = (int) (frequencyGenerator.nextValue().intValue() * frequencyFactor);
        double claimValue = baseClaims.get(0).getUltimate();
        for (int i = 0; i < numberOfClaims; i++) {
            claimValue += claimsSizeGenerator.nextValue().doubleValue() * severityScalingFactor;
        }
        List<Double> claimValues = new ArrayList<Double>();
        claimValues.add(claimValue);
        return getClaims(claimValues, ClaimType.ATTRITIONAL, periodScope);
    }

    public List<ClaimRoot> calculateClaims(List<UnderwritingInfoPacket> uwInfos, List uwInfosFilterCriteria,
                                           List<EventDependenceStream> eventStreams, IPerilMarker filterCriteria,
                                           PeriodScope periodScope) {
        setModifiedDistribution(claimsSizeDistribution, claimsSizeModification);
        List<EventSeverity> eventSeverities = ClaimsGeneratorUtils.filterEventSeverities(eventStreams, filterCriteria);
        List<Double> severities = ClaimsGeneratorUtils.extractSeverities(eventSeverities);
        double severityScalingFactor = UnderwritingInfoUtils.scalingFactor(uwInfos, claimsSizeBase, uwInfosFilterCriteria);
        double claimValue = 0;
        for (int i = 0; i < severities.size(); i++) {
            claimValue += -(modifiedClaimsSizeDistribution.inverseF(severities.get(i)) + shift);
        }
        List<Double> claimValues = new ArrayList<Double>();
        claimValues.add(claimValue * severityScalingFactor);
        return getClaims(claimValues, ClaimType.ATTRITIONAL, periodScope);
    }

}