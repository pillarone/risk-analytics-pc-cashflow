package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.filterUtilities;

import com.google.common.collect.ArrayListMultimap;
import org.joda.time.DateTime;
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter;
import org.pillarone.riskanalytics.core.simulation.SimulationException;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimRoot;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimType;
import org.pillarone.riskanalytics.domain.pc.cf.claim.IClaimRoot;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.ContractCoverBase;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.IncurredClaimBase;

import java.util.*;

/**
 * author simon.parten @ art-allianz . com
 */
public class RIUtilities {

    public static Set<IClaimRoot> incurredClaims(List<ClaimCashflowPacket> allCashflows, IncurredClaimBase incurredClaimBase) {
        Set<IClaimRoot> iClaimRoots = new HashSet<IClaimRoot>();

        for (ClaimCashflowPacket aClaim : allCashflows) {
            switch (incurredClaimBase) {
                case KEY:
                    iClaimRoots.add(aClaim.getKeyClaim());
                    break;
                case BASE:
                    iClaimRoots.add(aClaim.getBaseClaim());
                    break;
                default:
                    throw new SimulationException("Unknown base claims type :" + incurredClaimBase.toString());
            }
        }
        return iClaimRoots;
    }


    public static Set<IClaimRoot> incurredClaimsByDate( DateTime startDate, DateTime endDate, Set<IClaimRoot> allIncurredClaims , ContractCoverBase coverBase  ) {
        Set<IClaimRoot> claimsOfInterest = new HashSet<IClaimRoot>();
        for (IClaimRoot anIncurredClaim : allIncurredClaims) {
            DateTime coverDateTime = coverBase.claimCoverDate(anIncurredClaim);
            if ((coverDateTime.equals(startDate) || coverDateTime.isAfter(startDate)) && coverDateTime.isBefore(endDate)) {
                claimsOfInterest.add(anIncurredClaim);
            }
        }
        return claimsOfInterest;
    }

    public static List<ClaimCashflowPacket> cashflowClaimsByOccurenceDate(DateTime startDate, DateTime endDate, List<ClaimCashflowPacket> cashflows) {
        List<ClaimCashflowPacket> claimsOfInterest = new ArrayList<ClaimCashflowPacket>();
        for (ClaimCashflowPacket aCashflow : cashflows) {
            DateTime coverDate = aCashflow.getDate();
            if ((coverDate.equals(startDate) || coverDate.isAfter(startDate)) && coverDate.isBefore(endDate)) {
                claimsOfInterest.add(aCashflow);
            }
        }
        return claimsOfInterest;
    }

    public static List<ClaimCashflowPacket> latestCashflowByIncurredClaim( List<ClaimCashflowPacket> cashflows ) {
        Set<IClaimRoot> claimRoots = RIUtilities.incurredClaims(cashflows, IncurredClaimBase.KEY);

        ArrayListMultimap<IClaimRoot, ClaimCashflowPacket> cashflowsByKey = ArrayListMultimap.create();

        for(ClaimCashflowPacket aCashflow : cashflows) {
            cashflowsByKey.put(aCashflow.getKeyClaim(), aCashflow);
        }

        List<ClaimCashflowPacket> latestUpdates = new ArrayList<ClaimCashflowPacket>();

        for (IClaimRoot claimRoot : claimRoots) {

            IClaimRoot claimRoot1 = new ClaimRoot(0, ClaimType.ATTRITIONAL, null, new DateTime(1900, 1, 1, 1, 1, 1, 1));
            ClaimCashflowPacket latestPacket = new ClaimCashflowPacket(claimRoot1, claimRoot1);

            List<ClaimCashflowPacket> cashflowPackets = cashflowsByKey.get(claimRoot);
            for (ClaimCashflowPacket cashflowPacket : cashflowPackets) {
                if(cashflowPacket.getDate().isAfter(latestPacket.getDate())) {
                    latestPacket = cashflowPacket;
                }
            }
            latestUpdates.add(latestPacket);
        }

        return latestUpdates;


    }




    public static List<ClaimCashflowPacket> cashflowsByIncurredDate( DateTime startDate, DateTime endDate, List<ClaimCashflowPacket> cashflows , ContractCoverBase coverBase  ) {
        List<ClaimCashflowPacket> claimsOfInterest = new ArrayList<ClaimCashflowPacket>();
        for (ClaimCashflowPacket anIncurredClaim : cashflows) {
            DateTime coverDateTime = coverBase.claimCoverDate(anIncurredClaim);
            if ((coverDateTime.equals(startDate) || coverDateTime.isAfter(startDate)) && coverDateTime.isBefore(endDate)) {
                claimsOfInterest.add(anIncurredClaim);
            }
        }
        return claimsOfInterest;
    }


    public static Set<IClaimRoot> incurredClaimsByPeriod( Integer period, IPeriodCounter periodCounter, Set<IClaimRoot> allIncurredClaims , ContractCoverBase coverBase  ) {

        Set<IClaimRoot> claimsOfInterest = new HashSet<IClaimRoot>();
        for (IClaimRoot anIncurredClaim : allIncurredClaims) {
            DateTime coverDateTime = coverBase.claimCoverDate(anIncurredClaim);
            periodCounter.belongsToPeriod(coverDateTime);
        }
        return claimsOfInterest;
    }
}
