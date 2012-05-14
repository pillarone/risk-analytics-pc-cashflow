package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract;

import org.joda.time.DateTime;
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimUtils;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.TrivialContract;

/**
 * This objects links a ClaimCashflowPacket with its ClaimStorage and the IReinsuranceContract being applied to it.
 *
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class ClaimHistoryAndApplicableContract {
    private ClaimCashflowPacket claim;
    private IReinsuranceContract contract;
    private ClaimStorage storage;

    public ClaimHistoryAndApplicableContract(ClaimCashflowPacket claim, ClaimStorage claimStorage, IReinsuranceContract contract) {
        if (claim.getNominalUltimate() > 0) {
            // claim is positive if ceded claims are covered, inverting sign required
            this.claim = ClaimUtils.scale(claim, -1, true, true);
        }
        else {
            this.claim = claim;
        }
        this.storage = claimStorage;
        this.contract = contract;
    }

    public DateTime getUpdateDate() {
        return claim.getUpdateDate();
    }

    public ClaimCashflowPacket getGrossClaim() {
        return claim;
    }

    public ClaimCashflowPacket getCededClaim(IPeriodCounter periodCounter) {
        // todo: why is this switch required here and in the c'tor?
        if (claim.getNominalUltimate() > 0) {
            // claim is positive if ceded claims are covered, inverting sign required
            return contract.calculateClaimCeded(ClaimUtils.scale(claim, -1, true, true), storage, periodCounter);
        }
        else {
            return contract.calculateClaimCeded(claim, storage, periodCounter);
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(claim.toString());
        builder.append(", ");
        builder.append(contract.toString());
        return builder.toString();
    }

    public boolean hasContract(IReinsuranceContract contract) {
        return contract == this.contract;
    }

    /**
     * @return true if contract is implementing TrivialContract
     */
    public boolean isTrivialContract() {
        return contract instanceof TrivialContract;
    }
}
