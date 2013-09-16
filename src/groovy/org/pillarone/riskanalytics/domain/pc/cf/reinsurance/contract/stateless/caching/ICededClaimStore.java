package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.caching;

import com.google.common.collect.ArrayListMultimap;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ICededRoot;
import org.pillarone.riskanalytics.domain.pc.cf.claim.IClaimRoot;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.ClaimRIOutcome;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.IncurredClaimRIOutcome;

import java.util.Collection;
import java.util.List;

/**
 * author simon.parten @ art-allianz . com
 */
public interface ICededClaimStore {

    List<ClaimCashflowPacket> allCededCashlowsToDate();

    List<ICededRoot> allCededRootClaimsToDate();

    List<ClaimRIOutcome> allRIOutcomesToDate();

    void cacheCededClaims(List<ClaimRIOutcome> cededCashflows, final List<IncurredClaimRIOutcome> cededIncurred);

    Collection<IncurredClaimRIOutcome> allIncurredRIOutcomesToDate();

    ArrayListMultimap<IClaimRoot, ClaimCashflowPacket> cashflowsByBaseClaim();
}
