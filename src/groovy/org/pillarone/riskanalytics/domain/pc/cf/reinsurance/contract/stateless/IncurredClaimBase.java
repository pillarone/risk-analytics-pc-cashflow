package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless;

import org.apache.tools.ant.taskdefs.optional.depend.constantpool.ClassCPInfo;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ICededRoot;
import org.pillarone.riskanalytics.domain.pc.cf.claim.IClaimRoot;

/**
 * Created with IntelliJ IDEA.
 * User: sparten
 * Date: 31.08.12
 * Time: 12:52
 * To change this template use File | Settings | File Templates.
 */
public enum IncurredClaimBase {
    KEY {
        @Override
        public IClaimRoot parentClaim(ClaimCashflowPacket claimCashflowPacket) {
            return claimCashflowPacket.getKeyClaim();
        }

        @Override
        public ICededRoot parentCededClaim(ClaimCashflowPacket claimCashflowPacket) {
            return (ICededRoot) parentClaim(claimCashflowPacket);
        }

    }, BASE {
        @Override
        public IClaimRoot parentClaim(ClaimCashflowPacket claimCashflowPacket) {
            return claimCashflowPacket.getBaseClaim();
        }

        @Override
        public ICededRoot parentCededClaim(ClaimCashflowPacket claimCashflowPacket) {
            return (ICededRoot) parentClaim(claimCashflowPacket);
        }
    };

    public abstract IClaimRoot parentClaim(ClaimCashflowPacket claimCashflowPacket);

    public abstract ICededRoot parentCededClaim(ClaimCashflowPacket claimCashflowPacket);

}
