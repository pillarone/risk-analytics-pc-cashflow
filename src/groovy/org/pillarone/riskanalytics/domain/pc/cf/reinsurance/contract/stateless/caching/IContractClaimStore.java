package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.caching;

import com.google.common.collect.SetMultimap;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;
import org.pillarone.riskanalytics.domain.pc.cf.claim.IClaimRoot;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.ContractCoverBase;

import java.util.Collection;
import java.util.Set;

/**
 * author simon.parten @ art-allianz . com
 */
public interface IContractClaimStore {

    Collection<ClaimCashflowPacket> allClaimCashflowPackets();

    Set<IClaimRoot> allIncurredClaims(ContractCoverBase coverBase);

    SetMultimap<IClaimRoot, IClaimRoot> incurredClaimsByKey();

    void cacheClaims(Collection<ClaimCashflowPacket> claims);

}
