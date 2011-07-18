package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract;

import org.joda.time.DateTime;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimUtils;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class ClaimHistoryAndApplicableContract {
    private ClaimCashflowPacket claim;
    private IReinsuranceContract contract;
    private ClaimStorage storage;

    public ClaimHistoryAndApplicableContract(ClaimCashflowPacket claim, ClaimStorage claimStorage, IReinsuranceContract contract) {
        this.claim = claim;
        this.storage = claimStorage;
        this.contract = contract;
    }

    public void updateIncrements() {
        storage.addIncrements(claim);
    }

    public DateTime getUpdateDate() {
        return claim.getUpdateDate();
    }

    public ClaimCashflowPacket getGrossClaim() {
        return claim;
    }

    public ClaimCashflowPacket getCededClaim() {
        if (claim.getNominalUltimate() > 0) {
            // claim is positive is ceded claims are covered, inverting sign required
            return contract.calculateClaimCeded(ClaimUtils.scale(claim, -1), storage);
        }
        else {
            return contract.calculateClaimCeded(claim, storage);
        }
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(claim.toString());
        buffer.append(", ");
        buffer.append(contract.toString());
        return buffer.toString();
    }

    public boolean hasContract(IReinsuranceContract contract) {
        return contract == this.contract;
    }
}
