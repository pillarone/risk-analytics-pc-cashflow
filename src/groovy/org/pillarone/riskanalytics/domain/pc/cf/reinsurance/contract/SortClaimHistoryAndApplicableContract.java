package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract;

import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;

import java.util.Comparator;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class SortClaimHistoryAndApplicableContract implements Comparator<ClaimHistoryAndApplicableContract> {

    private static SortClaimHistoryAndApplicableContract instance = null;

    private SortClaimHistoryAndApplicableContract() {
    }

    public static SortClaimHistoryAndApplicableContract getInstance() {
        if (instance == null) {
            instance = new SortClaimHistoryAndApplicableContract();
        }
        return instance;
    }

    public int compare(ClaimHistoryAndApplicableContract claimHistory, ClaimHistoryAndApplicableContract otherClaimHistory) {
        return claimHistory.getUpdateDate().compareTo(otherClaimHistory.getUpdateDate());
    }
}
