package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.caching;

import com.google.common.collect.SetMultimap;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;
import org.pillarone.riskanalytics.domain.pc.cf.claim.IClaimRoot;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.ContractCoverBase;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.filterUtilities.RIUtilities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * author simon.parten @ art-allianz . com
 */
public class ListOnlyContractClaimStore implements IContractClaimStore {

    private final Collection<ClaimCashflowPacket> allCashflows = new ArrayList<ClaimCashflowPacket>();

    @Override
    public Collection<ClaimCashflowPacket> allClaimCashflowPackets() {
        return allCashflows;
    }

    @Override
    public Set<IClaimRoot> allIncurredClaims(ContractCoverBase coverBase) {
        return null;
    }

    @Override
    public SetMultimap<IClaimRoot, IClaimRoot> incurredClaimsByKey() {
        return RIUtilities.incurredClaims(allCashflows);
    }

    @Override
    public void cacheClaims(Collection<ClaimCashflowPacket> claims) {
        allCashflows.addAll(claims);
    }
}
