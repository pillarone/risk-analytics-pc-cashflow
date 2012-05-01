package org.pillarone.riskanalytics.domain.pc.cf.claim.generator;

import org.pillarone.riskanalytics.core.packets.PacketList;
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimRoot;
import org.pillarone.riskanalytics.domain.pc.cf.claim.generator.contractBase.IReinsuranceContractBaseStrategy;
import org.pillarone.riskanalytics.domain.pc.cf.dependency.EventDependenceStream;
import org.pillarone.riskanalytics.domain.pc.cf.dependency.SystematicFrequencyPacket;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket;
import org.pillarone.riskanalytics.domain.pc.cf.indexing.Factors;
import org.pillarone.riskanalytics.domain.utils.marker.IPerilMarker;

import java.util.List;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public interface IPeriodDependingClaimsGeneratorStrategy {
    /**
     * @param inUnderwritingInfo is combined with parmSeverityBase in order to filter matching underwriting info and calculate the scale factor
     * @param inEventFrequencies ignored for attritional claims
     * @param inEventSeverities  ignored for attritional claims
     * @param severityFactors    applied on ultimate generation
     * @param contractBase       triggering inception and occurrence date generation
     * @param filterCriteria     for inEventSeverities
     * @param periodScope        used for date generation
     * @return ClaimRoot objects
     */
    List<ClaimRoot> baseClaims(PacketList<UnderwritingInfoPacket> inUnderwritingInfo,
                               PacketList<SystematicFrequencyPacket> inEventFrequencies,
                               PacketList<EventDependenceStream> inEventSeverities,
                               List<Factors> severityFactors,
                               IReinsuranceContractBaseStrategy contractBase,
                               IPerilMarker filterCriteria, PeriodScope periodScope);
}
