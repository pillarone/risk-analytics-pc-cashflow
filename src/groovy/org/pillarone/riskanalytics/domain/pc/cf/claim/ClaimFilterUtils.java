package org.pillarone.riskanalytics.domain.pc.cf.claim;

import org.pillarone.riskanalytics.domain.utils.marker.IReinsuranceContractMarker;

import java.util.ArrayList;
import java.util.List;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class ClaimFilterUtils {

    /**
     * @param claims    the list of claims to filter
     * @param contracts the contract markers to filter by, if any; null means no filtering (all are returned)
     * @return the list of claims that passed through the filter (i.e. whose reinsurance contract is listed in contracts)
     */
    public static List<ClaimCashflowPacket> filterClaimsByContract(List<ClaimCashflowPacket> claims,
                                                                   List<IReinsuranceContractMarker> contracts) {
        List<ClaimCashflowPacket> filteredClaims = new ArrayList<ClaimCashflowPacket>();
        if (contracts == null || contracts.size() == 0) {
            filteredClaims.addAll(claims);
        }
        else {
            for (ClaimCashflowPacket claim : claims) {
                if (contracts.contains(claim.reinsuranceContract())) {
                    filteredClaims.add(claim);
                }
            }
        }
        return filteredClaims;
    }

}
