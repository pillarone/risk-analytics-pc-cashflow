package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless;

import org.joda.time.DateTime;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;
import org.pillarone.riskanalytics.domain.pc.cf.claim.IClaimRoot;

/**
 * Created with IntelliJ IDEA.
 * User: sparten
 * Date: 31.08.12
 * Time: 12:48
 * To change this template use File | Settings | File Templates.
 */
public enum ContractCoverBase {

    LOSSES_OCCURING{
        @Override
        public DateTime claimCoverDate(ClaimCashflowPacket packet) {
            return  claimCoverDate( packet.getKeyClaim() );
        }

        @Override
        public DateTime claimCoverDate(IClaimRoot rootClaim) {
            return rootClaim.getOccurrenceDate();
        }
    }

    ,
    RISKS_ATTACHING{
        @Override
        public DateTime claimCoverDate(ClaimCashflowPacket packet) {
            return claimCoverDate( packet.getKeyClaim() );
        }

        @Override
        public DateTime claimCoverDate(IClaimRoot rootClaim) {
            return rootClaim.getExposureStartDate();
        }
    } ;

    public abstract DateTime claimCoverDate(ClaimCashflowPacket packet);

    public abstract DateTime claimCoverDate(IClaimRoot rootClaim);

}
