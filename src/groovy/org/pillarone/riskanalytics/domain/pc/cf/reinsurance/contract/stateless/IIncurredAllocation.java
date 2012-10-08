package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless;

import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ICededRoot;
import org.pillarone.riskanalytics.domain.pc.cf.claim.IClaimRoot;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * author simon.parten @ art-allianz . com
 */
public interface IIncurredAllocation {

    List<ICededRoot> allocateClaims(double incurredInPeriod, Set<IClaimRoot> allIncurredClaims, PeriodScope periodScope, ContractCoverBase base);

}