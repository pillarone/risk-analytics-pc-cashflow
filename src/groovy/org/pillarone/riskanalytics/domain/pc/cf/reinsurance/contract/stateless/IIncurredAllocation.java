package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless;

import org.pillarone.riskanalytics.domain.pc.cf.claim.IClaimRoot;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * author simon.parten @ art-allianz . com
 */
public interface IIncurredAllocation {

    List<IClaimRoot> allocateClaims(double incurredInPeriod, Set<IClaimRoot> periodClaims);

}
