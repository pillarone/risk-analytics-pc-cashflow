package org.pillarone.riskanalytics.domain.pc.cf.claim.generator;

import org.pillarone.riskanalytics.core.parameterization.IParameterObject;
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimRoot;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimType;

import java.util.List;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public interface IClaimsGeneratorStrategy extends IParameterObject {

    List<ClaimRoot> generateClaims(PeriodScope periodScope);
    List<ClaimRoot> generateClaims(double scaleFactor, ClaimType claimType, PeriodScope periodScope);
//    List<ClaimCashflowPacket> generateClaims(double scaleFactor, List probabilities);
//    List<ClaimCashflowPacket> generateClaims(double scaleFactor, List<EventPacket> events);
}