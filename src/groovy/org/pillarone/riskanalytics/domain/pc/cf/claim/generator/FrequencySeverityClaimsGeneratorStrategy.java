package org.pillarone.riskanalytics.domain.pc.cf.claim.generator;

import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter;
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier;
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimRoot;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimType;
import org.pillarone.riskanalytics.domain.pc.cf.claim.FrequencySeverityClaimType;
import org.pillarone.riskanalytics.domain.pc.cf.dependency.EventDependenceStream;
import org.pillarone.riskanalytics.domain.pc.cf.dependency.SystematicFrequencyPacket;
import org.pillarone.riskanalytics.domain.pc.cf.event.EventPacket;
import org.pillarone.riskanalytics.domain.pc.cf.event.EventSeverity;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.ExposureBase;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.FrequencyBase;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket;
import org.pillarone.riskanalytics.domain.pc.cf.indexing.Factors;
import org.pillarone.riskanalytics.domain.pc.cf.indexing.FactorsPacket;
import org.pillarone.riskanalytics.domain.pc.cf.indexing.IndexUtils;
import org.pillarone.riskanalytics.domain.utils.marker.IPerilMarker;
import org.pillarone.riskanalytics.domain.utils.math.distribution.DistributionModified;
import org.pillarone.riskanalytics.domain.utils.math.distribution.FrequencyDistributionUtils;
import org.pillarone.riskanalytics.domain.utils.math.distribution.RandomDistribution;
import org.pillarone.riskanalytics.domain.utils.math.distribution.RandomFrequencyDistribution;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class FrequencySeverityClaimsGeneratorStrategy extends AbstractSingleClaimsGeneratorStrategy {

    protected ConstrainedMultiDimensionalParameter frequencyIndices;
    protected FrequencyBase frequencyBase;
    protected RandomFrequencyDistribution frequencyDistribution;
    protected DistributionModified frequencyModification;
    protected ExposureBase claimsSizeBase;
    protected RandomDistribution claimsSizeDistribution;
    protected DistributionModified claimsSizeModification;
    protected FrequencySeverityClaimType produceClaim;

    public IParameterObjectClassifier getType() {
        return ClaimsGeneratorType.FREQUENCY_SEVERITY;
    }

    public Map getParameters() {
        Map<String, Object> parameters = new HashMap<String, Object>(8);
        parameters.put(CLAIMS_SIZE_BASE, claimsSizeBase);
        parameters.put(CLAIMS_SIZE_DISTRIBUTION, claimsSizeDistribution);
        parameters.put(CLAIMS_SIZE_MODIFICATION, claimsSizeModification);
        parameters.put(FREQUENCY_INDICES, frequencyIndices);
        parameters.put(FREQUENCY_BASE, frequencyBase);
        parameters.put(FREQUENCY_DISTRIBUTION, frequencyDistribution);
        parameters.put(FREQUENCY_MODIFICATION, frequencyModification);
        parameters.put(PRODUCE_CLAIM, produceClaim);
        return parameters;
    }


    public List<ClaimRoot> generateClaims(List<ClaimRoot> baseClaims, List<UnderwritingInfoPacket> uwInfos,
                                          List<Factors> severityFactors,
                                          List uwInfosFilterCriteria, List<FactorsPacket> factorPackets,
                                          PeriodScope periodScope, List<SystematicFrequencyPacket> systematicFrequencies,
                                          IPerilMarker filterCriteria) {
        RandomFrequencyDistribution systematicFrequencyDistribution = ClaimsGeneratorUtils.extractFrequencyDistribution(systematicFrequencies, filterCriteria);
        setClaimNumberGenerator(FrequencyDistributionUtils.getIdiosyncraticDistribution(frequencyDistribution, systematicFrequencyDistribution),
                frequencyModification);
        List<Factors> factors = IndexUtils.filterFactors(factorPackets, frequencyIndices);
        baseClaims.addAll(generateClaims(uwInfos, severityFactors, uwInfosFilterCriteria, claimsSizeBase, frequencyBase, factors, periodScope));
        return baseClaims;
    }

    @Override
    void lazyInitClaimsSizeGenerator() {
        setGenerator(claimsSizeDistribution, claimsSizeModification);
    }

    public List<ClaimRoot> calculateClaims(List<UnderwritingInfoPacket> uwInfos, List uwInfosFilterCriteria,
                                           List<EventDependenceStream> eventStreams, IPerilMarker filterCriteria,
                                           PeriodScope periodScope) {
        setModifiedDistribution(claimsSizeDistribution, claimsSizeModification);
        List<EventSeverity> eventSeverities = ClaimsGeneratorUtils.filterEventSeverities(eventStreams, filterCriteria);
        return calculateClaims(uwInfos, uwInfosFilterCriteria, claimsSizeBase, periodScope, eventSeverities);
    }

    public ClaimType claimType() {
        return produceClaim == FrequencySeverityClaimType.SINGLE ? ClaimType.SINGLE : ClaimType.AGGREGATED_EVENT;
    }
}