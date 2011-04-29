package org.pillarone.riskanalytics.domain.pc.cf.claim.generator;

import org.pillarone.riskanalytics.core.parameterization.IParameterObject;
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimRoot;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket;

import java.util.List;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public interface IClaimsGeneratorStrategy extends IParameterObject {

    List<ClaimRoot> generateClaims(List<UnderwritingInfoPacket> uwInfos, List uwInfosFilterCriteria, PeriodScope periodScope);
}