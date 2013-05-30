package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless;

import com.google.common.collect.Lists;
import edu.emory.mathcs.backport.java.util.Collections;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ICededRoot;
import org.pillarone.riskanalytics.domain.pc.cf.claim.IClaimRoot;

import java.util.List;

/**
 * Parameter helper class for additional premium.
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class AllClaimsRIOutcome {

    final List<IncurredClaimRIOutcome> allIncurredOutcomes;

    public AllClaimsRIOutcome() {
        this.allIncurredOutcomes = Lists.newArrayList();
    }

    public void addClaim(IncurredClaimRIOutcome claim) {
        allIncurredOutcomes.add(claim);
    }

    public List<ICededRoot> getAllCededClaims(){
        final List<ICededRoot> iCededRoots = Lists.newArrayList();
        for (IncurredClaimRIOutcome allIncurredOutcome : allIncurredOutcomes) {
            iCededRoots.add(allIncurredOutcome.getCededClaim());
        }
        return Collections.unmodifiableList(iCededRoots);
    }

    public List<ICededRoot> getAllNetClaims(){
        final List<ICededRoot> iCededRoots = Lists.newArrayList();
        for (IncurredClaimRIOutcome allIncurredOutcome : allIncurredOutcomes) {
            iCededRoots.add(allIncurredOutcome.getNetClaim());
        }
        return Collections.unmodifiableList(iCededRoots);
    }

    public List<IncurredClaimRIOutcome> getAllIncurredOutcomes() {
        return Collections.unmodifiableList( allIncurredOutcomes );
    }

    @Override
    public String toString() {
        return "AllClaimsRIOutcome{" +
                "allIncurredOutcomes=" + allIncurredOutcomes +
                '}';
    }
}

