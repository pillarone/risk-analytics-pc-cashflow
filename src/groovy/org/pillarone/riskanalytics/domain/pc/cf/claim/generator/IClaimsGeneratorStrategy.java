package org.pillarone.riskanalytics.domain.pc.cf.claim.generator;

import org.pillarone.riskanalytics.core.parameterization.IParameterObject;
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimRoot;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimType;
import org.pillarone.riskanalytics.domain.pc.cf.claim.generator.contractBase.IReinsuranceContractBaseStrategy;
import org.pillarone.riskanalytics.domain.pc.cf.dependency.EventDependenceStream;
import org.pillarone.riskanalytics.domain.pc.cf.dependency.SystematicFrequencyPacket;
import org.pillarone.riskanalytics.domain.pc.cf.event.EventSeverity;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket;
import org.pillarone.riskanalytics.domain.pc.cf.indexing.Factors;
import org.pillarone.riskanalytics.domain.pc.cf.indexing.FactorsPacket;
import org.pillarone.riskanalytics.domain.utils.marker.IPerilMarker;
import org.pillarone.riskanalytics.domain.utils.math.dependance.DependancePacket;

import java.util.List;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public interface IClaimsGeneratorStrategy extends IParameterObject {

    /**
     *
     * @param baseClaims this list is needed in order to calculate the number of required claims after calculateDependantClaimsWithContractBase
     *                   has been executed i.e. if external severity information is provided and only the missing claim
     *                   number needs to be generated.
     * @param uwInfos
     * @param uwInfosFilterCriteria
     * @param frequencyFactorsPackets s used only for frequency based strategies in order to apply indices on frequency
     *@param periodScope
     * @param systematicFrequencies
     * @param filterCriteria    @return
     */
    List<ClaimRoot> generateClaims(List<ClaimRoot> baseClaims, List<UnderwritingInfoPacket> uwInfos,
                                   List<Factors> severityFactors, List uwInfosFilterCriteria,
                                   List<FactorsPacket> frequencyFactorsPackets, PeriodScope periodScope,
                                   List<SystematicFrequencyPacket> systematicFrequencies,
                                   IPerilMarker filterCriteria);

    List<ClaimRoot> calculateClaims(List<UnderwritingInfoPacket> uwInfos, List uwInfosFilterCriteria,
                                    List<EventDependenceStream> eventStreams,
                                    IPerilMarker filterCriteria, PeriodScope periodScope);

    List<ClaimRoot> calculateDependantClaimsWithContractBase(DependancePacket dependancePacket,
                                                             IPerilMarker filterCriteria, PeriodScope periodScope, IReinsuranceContractBaseStrategy contractBase, Double underwritingInfoScaleFactor, List<Factors> indexSeverityFactors);

    /**
     * Forces an implementation of claims generation reliant on a series of events. Usually called when a claims generator is not
     * independent, for instance when a copula is used in generation the marginal distributions.
     * @param scaleFactor
     * @param periodScope
     * @param eventSeverities usually from copulae
     * @return list of dependant claim roots.
     */
    List<ClaimRoot> calculateClaims(double scaleFactor, PeriodScope periodScope, List<EventSeverity> eventSeverities);

    /**
     * Forces an implementation of independent claim severities, often for standalone generators.
     * @param scaleFactor
     * @param severitiesFactors
     * @param claimNumber
     * @param periodScope
     * @param contractBase
     * @return list of independant claim roots.
     */
    List<ClaimRoot> generateClaims(double scaleFactor, List<Factors> severitiesFactors, int claimNumber,
                                   PeriodScope periodScope, IReinsuranceContractBaseStrategy contractBase);

    ClaimType claimType();
}