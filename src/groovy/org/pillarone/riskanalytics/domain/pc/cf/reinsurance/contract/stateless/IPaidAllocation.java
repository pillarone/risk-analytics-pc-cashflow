package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless;

import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope;
import org.pillarone.riskanalytics.domain.pc.cf.claim.CededClaimRoot;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ICededRoot;
import org.pillarone.riskanalytics.domain.pc.cf.claim.IClaimRoot;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * author simon.parten @ art-allianz . com
 */
public interface IPaidAllocation {

    List<ClaimCashflowPacket> allocatePaid(Map<Integer, Double> incrementalPaidByPeriod, List<ClaimCashflowPacket> grossCashflowsThisPeriod, List<ClaimCashflowPacket> cededCashflowsToDate, PeriodScope periodScope, double termExcess, double termLimit, PeriodLayerParameters layerParameters, ContractCoverBase coverageBase, List<ICededRoot> incurredCededClaims);

}

