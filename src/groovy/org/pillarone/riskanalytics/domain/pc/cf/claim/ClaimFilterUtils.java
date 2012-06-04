package org.pillarone.riskanalytics.domain.pc.cf.claim;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.omg.CORBA.PUBLIC_MEMBER;
import org.pillarone.riskanalytics.domain.utils.datetime.DateTimeUtilities;
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

    /**
     * Filters a list of claims into a new list depending on whether claim dates are between the specified dates
     * @param claims list of claims to filter
     * @param startDate date to start filtering
     * @param endDate end date of filtering 
     * @return sub list of claims which are between the specified dates.
     */
    public static List<ClaimCashflowPacket> claimsInReportingPeriod(List<ClaimCashflowPacket> claims,
                                                                    DateTime startDate, DateTime endDate) {
        if(startDate.isAfter(endDate) || startDate.isEqual(endDate)){
            throw new IllegalArgumentException("Start date : " + DateTimeUtilities.formatDate.print(startDate)
                    + " is after end date : " + DateTimeUtilities.formatDate.print(endDate) + " in claim filter method" );
        }
        ArrayList<ClaimCashflowPacket> claimCashflowPackets = new ArrayList<ClaimCashflowPacket>();
        for (ClaimCashflowPacket claim : claims) {
            if (DateTimeUtilities.isBetween(startDate.minusMillis(1), endDate, claim.getDate())) {
                claimCashflowPackets.add(claim);
            }
        }
        return claimCashflowPackets;
    }

    /**
     * Sums paid amount of incremental indexed claims in a list
     * @param claims list of claims
     * @return sum of paid incremental indexed amounts
     */
    public static double sumPaidIncrementalIndexedInClaimList(List<ClaimCashflowPacket> claims) {
        double paidAmount = 0;
        for (ClaimCashflowPacket claim : claims) {
                paidAmount += claim.getPaidCumulatedIndexed();
        }
        return paidAmount;
    }

    /**
     * Filters claims between a certain dates and returns the sum of their paid incremental indexed amounts.
     * @param claims list of claims
     * @param startDate start date of reporting period
     * @param endDate end date of reporting period
     * @return sum paid incremental indexed of claims between the two dates supplied.
     */
    public static double paidAmountInReportingPeriod(List<ClaimCashflowPacket> claims, DateTime startDate, DateTime endDate ){
        List<ClaimCashflowPacket> claimsInPeriod = claimsInReportingPeriod(claims, startDate, endDate);
        return sumPaidIncrementalIndexedInClaimList(claimsInPeriod);
    }

}
