package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless;

import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ICededRoot;
import org.pillarone.riskanalytics.domain.pc.cf.claim.IClaimRoot;

/**
 * Parameter helper class for additional premium.
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class IncurredClaimRIOutcome {

    final ICededRoot netClaim;
    final ICededRoot cededClaim;
    final IClaimRoot grossClaim;

    public IncurredClaimRIOutcome(final ICededRoot netClaim, final ICededRoot cededClaim, final IClaimRoot grossClaim) {
        this.netClaim = netClaim;
        this.cededClaim = cededClaim;
        this.grossClaim = grossClaim;
    }

    public ICededRoot getNetClaim() {
        return netClaim;
    }

    public ICededRoot getCededClaim() {
        return cededClaim;
    }

    public IClaimRoot getGrossClaim() {
        return grossClaim;
    }

    @Override
    public String toString() {
        return "Outcome{" +
                "Net=" + netClaim +
                ", Ceded=" + cededClaim +
                ", Gross=" + grossClaim +
                '}';
    }
}

