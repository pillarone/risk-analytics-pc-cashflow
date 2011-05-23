package org.pillarone.riskanalytics.domain.pc.cf.claim.generator;

import org.pillarone.riskanalytics.core.parameterization.IParameterObject;
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimRoot;
import org.pillarone.riskanalytics.domain.pc.cf.claim.IPerilMarker;
import org.pillarone.riskanalytics.domain.pc.cf.dependency.EventDependenceStream;
import org.pillarone.riskanalytics.domain.pc.cf.dependency.SystematicFrequencyPacket;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket;
import org.pillarone.riskanalytics.domain.pc.cf.indexing.FactorsPacket;

import java.util.List;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public interface IClaimsGeneratorStrategy extends IParameterObject {

    /**
     * @param uwInfos
     * @param uwInfosFilterCriteria
     * @param factorsPackets        is used only for frequency based strategies in order to apply indices on frequency
     * @param periodScope
     * @return
     */
    List<ClaimRoot> generateClaims(List<ClaimRoot> baseClaims, List<UnderwritingInfoPacket> uwInfos, List uwInfosFilterCriteria,
                                   List<FactorsPacket> factorsPackets, PeriodScope periodScope,
                                   List<SystematicFrequencyPacket> systematicFrequencies,
                                   IPerilMarker filterCriteria);

    List<ClaimRoot> calculateClaims(List<UnderwritingInfoPacket> uwInfos, List uwInfosFilterCriteria,
                                    List<EventDependenceStream> eventStreams,
                                    IPerilMarker filterCriteria, PeriodScope periodScope);

}