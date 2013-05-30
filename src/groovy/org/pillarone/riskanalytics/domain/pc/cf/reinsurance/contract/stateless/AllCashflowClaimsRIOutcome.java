package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless;

import com.google.common.collect.Lists;
import edu.emory.mathcs.backport.java.util.Collections;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ICededRoot;

import java.util.List;

/**
 * Parameter helper class for additional premium.
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class AllCashflowClaimsRIOutcome {

    final List<ClaimRIOutcome> allIncurredOutcomes;

    public AllCashflowClaimsRIOutcome() {
        this.allIncurredOutcomes = Lists.newArrayList();
    }

    public void addClaim(ClaimRIOutcome claim) {
        allIncurredOutcomes.add(claim);
    }

    public List<ClaimCashflowPacket> getAllCededClaims(){
        final List<ClaimCashflowPacket> iCededRoots = Lists.newArrayList();
        for (ClaimRIOutcome allIncurredOutcome : allIncurredOutcomes) {
            iCededRoots.add(allIncurredOutcome.getCededClaim());
        }
        return Collections.unmodifiableList(iCededRoots);
    }

    public List<ClaimCashflowPacket> getAllNetClaims(){
        final List<ClaimCashflowPacket> iCededRoots = Lists.newArrayList();
        for (ClaimRIOutcome allIncurredOutcome : allIncurredOutcomes) {
            iCededRoots.add(allIncurredOutcome.getNetClaim());
        }
        return Collections.unmodifiableList(iCededRoots);
    }

    public List<ClaimRIOutcome> getAllIncurredOutcomes() {
        return Collections.unmodifiableList( allIncurredOutcomes );
    }

    @Override
    public String toString() {
        return "AllCashflowClaimsRIOutcome{" +
                "allIncurredOutcomes=" + allIncurredOutcomes +
                '}';
    }
}

