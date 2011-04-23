package org.pillarone.riskanalytics.domain.pc.cf.claim.generator;

import org.pillarone.riskanalytics.core.parameterization.IParameterObject;
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimRoot;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimType;
import org.pillarone.riskanalytics.domain.pc.cf.event.EventPacket;

import java.util.List;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public interface IClaimsGeneratorStrategy extends IParameterObject {
//    Exposure getClaimsSizeBase();
//
//    RandomDistribution getClaimsSizeDistribution();
//
//    DistributionModified getClaimsSizeModification();

    List<ClaimRoot> generateClaims(PeriodScope periodScope);
    List<ClaimRoot> generateClaims(double scaleFactor, ClaimType claimType, PeriodScope periodScope);
//    List<ClaimCashflowPacket> generateClaims(double scaleFactor, List probabilities);
//    List<ClaimCashflowPacket> generateClaims(double scaleFactor, List<EventPacket> events);
}