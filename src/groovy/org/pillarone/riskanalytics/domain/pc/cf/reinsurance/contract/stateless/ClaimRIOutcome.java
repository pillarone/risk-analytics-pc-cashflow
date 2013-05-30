package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless;

import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.additionalPremium.APBasis;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.additionalPremium.CalcAPBasis;

/**
 * Parameter helper class for additional premium.
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class ClaimRIOutcome {

    final ClaimCashflowPacket netClaim;
    final ClaimCashflowPacket cededClaim;
    final ClaimCashflowPacket grossClaim;

    public ClaimRIOutcome(ClaimCashflowPacket netClaim, ClaimCashflowPacket cededClaim, ClaimCashflowPacket grossClaim) {
        this.netClaim = netClaim;
        this.cededClaim = cededClaim;
        this.grossClaim = grossClaim;
    }

    public ClaimCashflowPacket getNetClaim() {
        return netClaim;
    }

    public ClaimCashflowPacket getCededClaim() {
        return cededClaim;
    }

    public ClaimCashflowPacket getGrossClaim() {
        return grossClaim;
    }

    @Override
    public String toString() {
        return "Outcome{" +
                "net=" + netClaim +
                ", ceded=" + cededClaim +
                ", gross=" + grossClaim +
                '}';
    }
}

