package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless;

import com.google.common.collect.Lists;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;

import java.util.Collections;
import java.util.List;

/**
 * Parameter helper class for additional premium.
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class AllCashflowClaimsRIOutcome {

    final List<ClaimRIOutcome> allCashflowOutcomes;

    public AllCashflowClaimsRIOutcome() {
        this.allCashflowOutcomes = Lists.newArrayList();
    }

    public void addClaim(ClaimRIOutcome claim) {
        allCashflowOutcomes.add(claim);
    }

    public List<ClaimCashflowPacket> getAllCededClaims(){
        final List<ClaimCashflowPacket> iCededRoots = Lists.newArrayList();
        for (ClaimRIOutcome allIncurredOutcome : allCashflowOutcomes) {
            iCededRoots.add(allIncurredOutcome.getCededClaim());
        }
        return Collections.unmodifiableList(iCededRoots);
    }

    public List<ClaimCashflowPacket> getAllNetClaims(){
        final List<ClaimCashflowPacket> iCededRoots = Lists.newArrayList();
        for (ClaimRIOutcome allIncurredOutcome : allCashflowOutcomes) {
            iCededRoots.add(allIncurredOutcome.getNetClaim());
        }
        return Collections.unmodifiableList(iCededRoots);
    }

    public List<ClaimRIOutcome> getAllCashflowOutcomes() {
        return Collections.unmodifiableList(allCashflowOutcomes);
    }

    @Override
    public String toString() {
        return "AllCashflowClaimsRIOutcome{" +
                "allCashflowOutcomes=" + allCashflowOutcomes +
                '}';
    }
}

