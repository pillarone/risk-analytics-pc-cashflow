package org.pillarone.riskanalytics.domain.pc.cf.claim;

import java.util.Comparator;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class SortClaimsByUpdateDate implements Comparator<ClaimCashflowPacket> {

    private static SortClaimsByUpdateDate instance = null;

    private SortClaimsByUpdateDate() {
    }

    public static SortClaimsByUpdateDate getInstance() {
        if (instance == null) {
            instance = new SortClaimsByUpdateDate();
        }
        return instance;
    }

    public int compare(ClaimCashflowPacket claim, ClaimCashflowPacket otherClaim) {
        return claim.getUpdateDate().compareTo(otherClaim.getUpdateDate());
    }
}
