package org.pillarone.riskanalytics.domain.pc.cf.claim;

import java.util.Comparator;

/**
 * author simon.parten @ art-allianz . com
 */
public class ClaimCashflowDateComparator implements Comparator<ClaimCashflowPacket> {

    public int compare(ClaimCashflowPacket o1, ClaimCashflowPacket o2) {
        return o1.getDate().compareTo(o2.getDate());
    }
}
