package org.pillarone.riskanalytics.domain.pc.cf.claim;

import java.util.Comparator;

/**
 *  *  Compares Claim object using their date attribute.
 *
 *  @return -1 if claim.date is before otherClaim.date, 0 if both dates are identical
 *
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class SortClaimsByOccurrenceDate implements Comparator<ClaimCashflowPacket> {

    private static SortClaimsByOccurrenceDate instance = null;

    private SortClaimsByOccurrenceDate() {
    }

    public static SortClaimsByOccurrenceDate getInstance() {
        if (instance == null) {
            instance = new SortClaimsByOccurrenceDate();
        }
        return instance;
    }

    public int compare(ClaimCashflowPacket claim, ClaimCashflowPacket otherClaim) {
        return claim.getOccurrenceDate().compareTo(otherClaim.getOccurrenceDate());
    }
}
