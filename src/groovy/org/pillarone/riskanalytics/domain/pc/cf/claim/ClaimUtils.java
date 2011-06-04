package org.pillarone.riskanalytics.domain.pc.cf.claim;

import org.apache.commons.lang.NotImplementedException;
import org.joda.time.DateTime;

import java.util.List;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class ClaimUtils {

    /**
     * Adds up all claims and builds a corresponding new base claim.
     * @param claims
     * @param sameBaseClaim
     * @return null if claims is empty
     */
    public static ClaimCashflowPacket sum(List<ClaimCashflowPacket> claims, boolean sameBaseClaim) {
        if (claims.isEmpty()) return null;
        if (!sameBaseClaim) {
            throw new NotImplementedException("method is currently implemented for same base claim only");
        }
        double ultimate = 0;
        double paidIncremental = 0;
        double paidCumulated = 0;
        double reportedIncremental = 0;
        double reportedCumulated = 0;
        double reserves = 0;
        for (ClaimCashflowPacket claim : claims) {
            ultimate += claim.ultimate();
            paidIncremental += claim.getPaidIncremental();
            paidCumulated += claim.getPaidCumulated();
            reportedIncremental += claim.getReportedIncremental();
            reportedCumulated += claim.getReportedCumulated();
            reserves += claim.reserved();
        }
        boolean hasUltimate = ultimate > 0;
        ClaimRoot baseClaim = new ClaimRoot(ultimate, claims.get(0).getBaseClaim());
        DateTime updateDate = claims.get(0).getUpdateDate();
        int updatePeriod = claims.get(0).getUpdatePeriod();
        return new ClaimCashflowPacket(baseClaim, paidIncremental, paidCumulated,
                reportedIncremental, reportedCumulated, reserves, updateDate, updatePeriod, hasUltimate);
    }

    public static ClaimCashflowPacket scale(ClaimCashflowPacket claim, double factor) {
        if (notTrivialValues(claim)) {
            ClaimRoot baseClaim = new ClaimRoot(claim.ultimate() * factor, claim.getBaseClaim());
            ClaimCashflowPacket scaledClaim = new ClaimCashflowPacket(baseClaim, claim.getPaidIncremental() * factor, claim.getPaidCumulated() * factor,
                    claim.getReportedIncremental() * factor, claim.getReportedCumulated() * factor, claim.reserved() * factor,
                    claim.getUpdateDate(), claim.getUpdatePeriod(), claim.ultimate() > 0);
            applyMarkers(claim, scaledClaim);
            return scaledClaim;
        }
        return claim;
    }

    public static boolean notTrivialValues(ClaimCashflowPacket claim) {
        return (!(claim.ultimate() == 0 && claim.getPaidIncremental() == 0 && claim.getPaidCumulated() == 0
                && claim.getReportedIncremental() == 0 && claim.getReportedCumulated() == 0
                && claim.reserved() == 0));
    }

    public static void applyMarkers(ClaimCashflowPacket source, ClaimCashflowPacket target) {
        target.setMarker(source.peril());
        target.setMarker(source.segment());
        target.setMarker(source.reinsuranceContract());
        target.setMarker(source.legalEntity());
    }
}
