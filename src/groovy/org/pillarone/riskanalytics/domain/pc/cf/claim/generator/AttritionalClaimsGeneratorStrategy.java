package org.pillarone.riskanalytics.domain.pc.cf.claim.generator;

import org.joda.time.DateTime;
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier;
import org.pillarone.riskanalytics.core.simulation.SimulationException;
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimRoot;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimType;
import org.pillarone.riskanalytics.domain.pc.cf.claim.generator.contractBase.IReinsuranceContractBaseStrategy;
import org.pillarone.riskanalytics.domain.pc.cf.dependency.EventDependenceStream;
import org.pillarone.riskanalytics.domain.pc.cf.dependency.SystematicFrequencyPacket;
import org.pillarone.riskanalytics.domain.pc.cf.event.EventSeverity;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.ExposureBase;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket;
import org.pillarone.riskanalytics.domain.pc.cf.indexing.Factors;
import org.pillarone.riskanalytics.domain.pc.cf.indexing.FactorsPacket;
import org.pillarone.riskanalytics.domain.pc.cf.indexing.IndexUtils;
import org.pillarone.riskanalytics.domain.utils.marker.IPerilMarker;
import org.pillarone.riskanalytics.domain.utils.math.dependance.DependancePacket;
import org.pillarone.riskanalytics.domain.utils.math.dependance.MarginalAndEvent;
import org.pillarone.riskanalytics.domain.utils.math.distribution.DistributionModified;
import org.pillarone.riskanalytics.domain.utils.math.distribution.RandomDistribution;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class AttritionalClaimsGeneratorStrategy extends AbstractClaimsGeneratorStrategy {

    protected ExposureBase claimsSizeBase;
    protected RandomDistribution claimsSizeDistribution;
    protected DistributionModified claimsSizeModification;

    public IParameterObjectClassifier getType() {
        return ClaimsGeneratorType.ATTRITIONAL;
    }

    public Map getParameters() {
        Map<String, Object> parameters = new HashMap<String, Object>(3);
        parameters.put(CLAIMS_SIZE_BASE, claimsSizeBase);
        parameters.put(CLAIMS_SIZE_DISTRIBUTION, claimsSizeDistribution);
        parameters.put(CLAIMS_SIZE_MODIFICATION, claimsSizeModification);
        return parameters;
    }

    /**
     * @param uwInfos
     * @param uwInfosFilterCriteria
     * @param factorsPackets        is ignored for attritional claims
     * @param periodScope
     * @return
     */
    public List<ClaimRoot> generateClaims(List<ClaimRoot> baseClaims,  List<UnderwritingInfoPacket> uwInfos,
                                          List<Factors> severityFactors, List uwInfosFilterCriteria,
                                          List<FactorsPacket> factorsPackets, PeriodScope periodScope,
                                          List<SystematicFrequencyPacket> systematicFrequencies, IPerilMarker filterCriteria) {
        if (baseClaims.size() == 1) {
            return baseClaims;
        }
        return generateClaim(uwInfos, severityFactors, uwInfosFilterCriteria, claimsSizeBase, periodScope);
    }

    @Override
    public List<ClaimRoot> calculateDependantClaimsWithContractBase(DependancePacket dependancePacket, IPerilMarker filterCriteria, PeriodScope periodScope, IReinsuranceContractBaseStrategy contractBase, Double underwritingInfoScaleFactor, List<Factors> indexSeverityFactors) {
        MarginalAndEvent marginalAndEvent = dependancePacket.getMarginal(filterCriteria, periodScope);
        List<ClaimRoot> baseClaims = new ArrayList<ClaimRoot>();
        DateTime exposureStartDate = contractBase.exposureStartDate(periodScope, getDateGenerator() );
        double scaleFactor = IndexUtils.aggregateFactor(indexSeverityFactors, exposureStartDate, periodScope.getPeriodCounter(), exposureStartDate);
        DateTime occurrenceDate = contractBase.occurrenceDate(exposureStartDate, dateGenerator, periodScope, null);
        setModifiedDistribution(claimsSizeDistribution, claimsSizeModification);
        double claimValue = (getModifiedClaimsSizeDistribution().inverseF(marginalAndEvent.getMarginalProbability()) + shift) * - underwritingInfoScaleFactor * scaleFactor;
        baseClaims.add(new ClaimRoot( claimValue, claimType(), exposureStartDate, occurrenceDate));
        return baseClaims;
    }

    public List<ClaimRoot> calculateClaims(List<UnderwritingInfoPacket> uwInfos, List uwInfosFilterCriteria,
                                           List<EventDependenceStream> eventStreams, IPerilMarker filterCriteria,
                                           PeriodScope periodScope) {
        setModifiedDistribution(claimsSizeDistribution, claimsSizeModification);
        List<EventSeverity> eventSeverities = ClaimsGeneratorUtils.filterEventSeverities(eventStreams, filterCriteria);
        return calculateClaims(uwInfos, uwInfosFilterCriteria, claimsSizeBase, periodScope, eventSeverities);
    }

    public ClaimType claimType() {
        return ClaimType.ATTRITIONAL;
    }

    @Override
    void lazyInitClaimsSizeGenerator() {
        setGenerator(claimsSizeDistribution, claimsSizeModification);
    }
}