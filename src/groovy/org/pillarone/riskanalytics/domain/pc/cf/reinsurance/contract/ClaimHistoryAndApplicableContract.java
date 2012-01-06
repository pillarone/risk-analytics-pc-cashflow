package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract;

import org.joda.time.DateTime;
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter;
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
        if (claim.getNominalUltimate() > 0) {
            this.claim = ClaimUtils.scale(claim, -1);
        }
        else {
            this.claim = claim;
        }
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

    public ClaimCashflowPacket getCededClaim(IPeriodCounter periodCounter) {
        if (claim.getNominalUltimate() > 0) {
            // claim is positive if ceded claims are covered, inverting sign required
            return contract.calculateClaimCeded(ClaimUtils.scale(claim, -1, true), storage, periodCounter);
        }
        else {
            return contract.calculateClaimCeded(claim, storage, periodCounter);
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
