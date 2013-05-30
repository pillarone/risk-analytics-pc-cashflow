package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless;

import com.google.common.collect.Lists;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;

import java.util.Collections;
import java.util.List;

/**
 * Parameter helper class for additional premium.
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class ContractRIOutcome {

    final List<ClaimRIOutcome> allClaims;

    public ContractRIOutcome() {
        this.allClaims = Lists.newArrayList();
    }

    public List<ClaimRIOutcome> getAllClaims() {
        return Collections.unmodifiableList( allClaims );
    }
}

