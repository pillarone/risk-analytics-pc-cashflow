package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract;

import java.util.Comparator;

/**
 * Sorts ClaimHistoryAndApplicableContract by update date
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

    /** based on update date */
    public int compare(ClaimHistoryAndApplicableContract claimHistory, ClaimHistoryAndApplicableContract otherClaimHistory) {
        return claimHistory.getUpdateDate().compareTo(otherClaimHistory.getUpdateDate());
    }
}
