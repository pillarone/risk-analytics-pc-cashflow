package org.pillarone.riskanalytics.domain.pc.cf.claim;

import org.apache.commons.lang.NotImplementedException;
import org.joda.time.DateTime;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.ClaimStorage;

import java.util.List;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class ClaimUtils {

    /**
     * Adds up all claims and builds a corresponding new base claim.
     * @param claims
     * @param sameBaseClaim: Marker interface of returned packet are all null if false
     * @return null if claims is empty. New object if claims.size() > 1
     */
    public static ClaimCashflowPacket sum(List<ClaimCashflowPacket> claims, boolean sameBaseClaim) {
        if (claims.isEmpty()) return null;
        if (claims.size() == 1) return claims.get(0);
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
        ClaimCashflowPacket summedClaims = new ClaimCashflowPacket(baseClaim, ultimate, paidIncremental, paidCumulated,
                reportedIncremental, reportedCumulated, reserves, updateDate, updatePeriod);
        applyMarkers(claims.get(0), summedClaims);
        return summedClaims;
    }

    public static ClaimCashflowPacket scale(ClaimCashflowPacket claim, double factor) {
        if (notTrivialValues(claim)) {
            double scaledUltimate = claim.ultimate() * factor;
            double scaledReserves = scaledUltimate - claim.getPaidCumulated() * factor;
            ClaimCashflowPacket scaledClaim = new ClaimCashflowPacket(claim.getBaseClaim(), scaledUltimate,
                    claim.getPaidIncremental() * factor, claim.getPaidCumulated() * factor,
                    claim.getReportedIncremental() * factor, claim.getReportedCumulated() * factor, scaledReserves,
                    claim.getUpdateDate(), claim.getUpdatePeriod());
            applyMarkers(claim, scaledClaim);
            return scaledClaim;
        }
        return claim;
    }

    /**
     * Scales all figures either by the reported or paid scale factor. No distinction between incremental and cumulated
     * claim properties.
     * @param grossClaim
     * @param scaleFactorUltimate
     * @param scaleFactorReported
     * @param scaleFactorPaid
     * @return
     */
    public static ClaimCashflowPacket getCededClaim(ClaimCashflowPacket grossClaim, ClaimStorage storage, double scaleFactorUltimate,
                                                    double scaleFactorReported, double scaleFactorPaid) {
        if (scaleFactorReported == -0) { scaleFactorReported = 0; }
        if (scaleFactorPaid == -0) { scaleFactorPaid = 0; }
        double cededPaidIncremental = grossClaim.getPaidIncremental() * scaleFactorPaid;
        double cededReportedIncremental = grossClaim.getReportedIncremental() * scaleFactorReported;
        storage.update(cededPaidIncremental, BasedOnClaimProperty.PAID);
        storage.update(cededReportedIncremental, BasedOnClaimProperty.REPORTED);
        ClaimCashflowPacket cededClaim = new ClaimCashflowPacket(
                storage.getReference(),
                avoidNegativeZero(grossClaim.ultimate() * scaleFactorUltimate),
                avoidNegativeZero(storage.getIncrementalPaidCeded()),
                avoidNegativeZero(storage.getCumulatedCeded(BasedOnClaimProperty.PAID)),
                avoidNegativeZero(storage.getIncrementalReportedCeded()),
                avoidNegativeZero(storage.getCumulatedCeded(BasedOnClaimProperty.REPORTED)),
                storage.cededReserves(),
                grossClaim.getUpdateDate(),
                grossClaim.getUpdatePeriod());
        applyMarkers(grossClaim, cededClaim);
        return cededClaim;
    }

    public static double avoidNegativeZero(double value) {
        return value == -0 ? 0 : value;
    }

    /**
     * @param grossClaim
     * @param cededClaim
     * @return
     */
    public static ClaimCashflowPacket getNetClaim(ClaimCashflowPacket grossClaim, ClaimCashflowPacket cededClaim) {
        if (cededClaim == null) {
            return (ClaimCashflowPacket) grossClaim.clone();
        }
        else {
            ClaimCashflowPacket netClaim = new ClaimCashflowPacket(
                grossClaim.getBaseClaim(),
                grossClaim.ultimate() + cededClaim.ultimate(),
                grossClaim.getPaidIncremental() + cededClaim.getPaidIncremental(),
                grossClaim.getPaidCumulated() + cededClaim.getPaidCumulated(),
                grossClaim.getReportedIncremental() + cededClaim.getReportedIncremental(),
                grossClaim.getReportedCumulated() + cededClaim.getReportedCumulated(),
                grossClaim.reserved() + cededClaim.reserved(),
                grossClaim.getUpdateDate(),
                grossClaim.getUpdatePeriod());
            applyMarkers(cededClaim, netClaim);
            return netClaim;
        }
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
