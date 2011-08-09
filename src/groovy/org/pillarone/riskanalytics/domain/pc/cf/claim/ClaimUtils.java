package org.pillarone.riskanalytics.domain.pc.cf.claim;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import org.apache.commons.lang.NotImplementedException;
import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.ExposureInfo;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.ClaimStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class ClaimUtils {

    /**
     * Adds up all claims and builds a corresponding new base claim. It's exposure info is null.
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
        double appliedIndex = 1;
        for (ClaimCashflowPacket claim : claims) {
            ultimate += claim.ultimate();
            paidIncremental += claim.getPaidIncrementalIndexed();
            paidCumulated += claim.getPaidCumulatedIndexed();
            reportedIncremental += claim.getReportedIncrementalIndexed();
            reportedCumulated += claim.getReportedCumulatedIndexed();
            reserves += claim.reservedIndexed();
            appliedIndex *= claim.getAppliedIndexValue();
        }
        ClaimRoot baseClaim = new ClaimRoot(ultimate, claims.get(0).getBaseClaim());
        DateTime updateDate = claims.get(0).getUpdateDate();
        int updatePeriod = claims.get(0).getUpdatePeriod();
        ClaimCashflowPacket summedClaims = new ClaimCashflowPacket(baseClaim, ultimate, paidIncremental, paidCumulated,
                reportedIncremental, reportedCumulated, reserves, null, updateDate, updatePeriod);
        applyMarkers(claims.get(0), summedClaims);
        summedClaims.setAppliedIndexValue(appliedIndex);
        return summedClaims;
    }

    public static List<ClaimCashflowPacket> aggregateByBaseClaim(List<ClaimCashflowPacket> claims) {
        List<ClaimCashflowPacket> aggregateByBaseClaim = new ArrayList<ClaimCashflowPacket>();
        ListMultimap<IClaimRoot, ClaimCashflowPacket> claimsByBaseClaim = ArrayListMultimap.create();
        for (ClaimCashflowPacket claim : claims) {
            claimsByBaseClaim.put(claim.getBaseClaim(), claim);
        }
        for (Collection<ClaimCashflowPacket> claimsWithSameBaseClaim : claimsByBaseClaim.asMap().values()) {
            if (claimsWithSameBaseClaim.size() == 1) {
                aggregateByBaseClaim.add(claimsWithSameBaseClaim.iterator().next());
            }
            else {
                double ultimate = 0;
                double paidIncremental = 0;
                double paidCumulated = 0;
                double reportedIncremental = 0;
                double reportedCumulated = 0;
                DateTime mostRecentClaimUpdate = null;
                double latestReserves = 0;
                double appliedIndex = 1;
                for (ClaimCashflowPacket claim : claimsWithSameBaseClaim) {
                    ultimate += claim.ultimate();
                    paidIncremental += claim.getPaidIncrementalIndexed();
                    reportedIncremental += claim.getReportedIncrementalIndexed();
                    appliedIndex *= claim.getAppliedIndexValue();
                    if (mostRecentClaimUpdate == null || claim.getUpdateDate().isAfter(mostRecentClaimUpdate)) {
                        mostRecentClaimUpdate = claim.getUpdateDate();
                        reportedCumulated = claim.getReportedCumulatedIndexed();
                        paidCumulated = claim.getPaidCumulatedIndexed();
                        latestReserves = claim.reservedIndexed();
                    }
                }
                ClaimRoot baseClaim = new ClaimRoot(ultimate, claims.get(0).getBaseClaim());
                int updatePeriod = claims.get(0).getUpdatePeriod();
                ClaimCashflowPacket aggregateClaim = new ClaimCashflowPacket(baseClaim, ultimate, paidIncremental, paidCumulated,
                    reportedIncremental, reportedCumulated, latestReserves, null, mostRecentClaimUpdate, updatePeriod);
                aggregateClaim.setAppliedIndexValue(appliedIndex);
                applyMarkers(claims.get(0), aggregateClaim);
                aggregateByBaseClaim.add(aggregateClaim);
            }
        }
        return aggregateByBaseClaim;
    }

    /**
     * @param claim
     * @param factor
     * @return new ClaimCashflowPacket()
     */
    public static ClaimCashflowPacket scale(ClaimCashflowPacket claim, double factor) {
        if (notTrivialValues(claim)) {
            double scaledUltimate = claim.ultimate() * factor;
            double scaledReserves = scaledUltimate - claim.getPaidCumulatedIndexed() * factor;
            ClaimCashflowPacket scaledClaim = new ClaimCashflowPacket(claim.getBaseClaim(), scaledUltimate,
                    claim.getPaidIncrementalIndexed() * factor, claim.getPaidCumulatedIndexed() * factor,
                    claim.getReportedIncrementalIndexed() * factor, claim.getReportedCumulatedIndexed() * factor, scaledReserves,
                    claim.getExposureInfo(), claim.getUpdateDate(), claim.getUpdatePeriod());
            applyMarkers(claim, scaledClaim);
            return scaledClaim;
        }
        return claim;
    }

    public static ClaimCashflowPacket scale(ClaimCashflowPacket claim, double factor, IClaimRoot scaledBaseClaim) {
        if (notTrivialValues(claim)) {
            double scaledReserves = (claim.developedUltimate() - claim.getPaidCumulatedIndexed()) * factor;
            double scaledUltimate = claim.ultimate() * factor;
            ClaimCashflowPacket scaledClaim = new ClaimCashflowPacket(scaledBaseClaim, scaledUltimate,
                    claim.getPaidIncrementalIndexed() * factor, claim.getPaidCumulatedIndexed() * factor,
                    claim.getReportedIncrementalIndexed() * factor, claim.getReportedCumulatedIndexed() * factor, scaledReserves,
                    claim.getExposureInfo(), claim.getUpdateDate(), claim.getUpdatePeriod());
            applyMarkers(claim, scaledClaim);
            return scaledClaim;
        }
        return claim;
    }

    /**
     * exposure info is not affected by scaling.
     * @param claim
     * @param factor
     * @param scaleBaseClaim
     * @return
     */
    public static ClaimCashflowPacket scale(ClaimCashflowPacket claim, double factor, boolean scaleBaseClaim) {
        if (!scaleBaseClaim) return scale(claim, factor);
        if (notTrivialValues(claim)) {
            double scaledReserves = (claim.developedUltimate() - claim.getPaidCumulatedIndexed()) * factor;
            IClaimRoot scaledBaseClaim = claim.getBaseClaim().withScale(factor);
            double scaledUltimate = claim.ultimate() * factor;
            ClaimCashflowPacket scaledClaim = new ClaimCashflowPacket(scaledBaseClaim, scaledUltimate,
                    claim.getPaidIncrementalIndexed() * factor, claim.getPaidCumulatedIndexed() * factor,
                    claim.getReportedIncrementalIndexed() * factor, claim.getReportedCumulatedIndexed() * factor, scaledReserves,
                    claim.getExposureInfo(), claim.getUpdateDate(), claim.getUpdatePeriod());
            applyMarkers(claim, scaledClaim);
            return scaledClaim;
        }
        return claim;
    }

    /**
     * Scales all figures either by the reported or paid scale factor. No distinction between incremental and cumulated
     * claim properties.
     * @param grossClaim
     * @param storage
     * @param scaleFactorUltimate
     * @param scaleFactorReported
     * @param scaleFactorPaid
     * @param adjustExposureInfo
     * @return
     */
    public static ClaimCashflowPacket getCededClaim(ClaimCashflowPacket grossClaim, ClaimStorage storage, double scaleFactorUltimate,
                                                    double scaleFactorReported, double scaleFactorPaid, boolean adjustExposureInfo) {
        if (scaleFactorReported == -0) { scaleFactorReported = 0; }
        if (scaleFactorPaid == -0) { scaleFactorPaid = 0; }
        double cededPaidIncremental = grossClaim.getPaidIncrementalIndexed() * scaleFactorPaid;
        double cededReportedIncremental = grossClaim.getReportedIncrementalIndexed() * scaleFactorReported;
        storage.update(cededPaidIncremental, BasedOnClaimProperty.PAID);
        storage.update(cededReportedIncremental, BasedOnClaimProperty.REPORTED);
        double cededReserves = storage.cededReserves() + grossClaim.developmentResult() * scaleFactorUltimate;
        ExposureInfo cededExposureInfo = adjustExposureInfo && grossClaim.getExposureInfo() != null
                ? grossClaim.getExposureInfo().withScale(scaleFactorUltimate) : grossClaim.getExposureInfo();
        ClaimCashflowPacket cededClaim = new ClaimCashflowPacket(
                storage.getReference(),
                avoidNegativeZero(grossClaim.ultimate() * scaleFactorUltimate),
                storage.getReferenceCeded().getUltimate(),
                avoidNegativeZero(storage.getIncrementalPaidCeded()),
                avoidNegativeZero(storage.getCumulatedCeded(BasedOnClaimProperty.PAID)),
                avoidNegativeZero(storage.getIncrementalReportedCeded()),
                avoidNegativeZero(storage.getCumulatedCeded(BasedOnClaimProperty.REPORTED)),
                avoidNegativeZero(cededReserves),
                cededExposureInfo,
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
            boolean isProportionalContract = cededClaim.reinsuranceContract() != null && cededClaim.reinsuranceContract().adjustExposureInfo();
            double factor = 0;
            ExposureInfo netExposureInfo = isProportionalContract && grossClaim.getExposureInfo() != null
                    ? grossClaim.getExposureInfo().withScale(factor) : grossClaim.getExposureInfo();
            ClaimCashflowPacket netClaim = new ClaimCashflowPacket(
                grossClaim.getBaseClaim(),
                grossClaim.ultimate() + cededClaim.ultimate(),
                grossClaim.nominalUltimate() + cededClaim.nominalUltimate(),
                grossClaim.getPaidIncrementalIndexed() + cededClaim.getPaidIncrementalIndexed(),
                grossClaim.getPaidCumulatedIndexed() + cededClaim.getPaidCumulatedIndexed(),
                grossClaim.getReportedIncrementalIndexed() + cededClaim.getReportedIncrementalIndexed(),
                grossClaim.getReportedCumulatedIndexed() + cededClaim.getReportedCumulatedIndexed(),
                grossClaim.reservedIndexed() + cededClaim.reservedIndexed(),
                netExposureInfo,
                grossClaim.getUpdateDate(),
                grossClaim.getUpdatePeriod());
            applyMarkers(cededClaim, netClaim);
            return netClaim;
        }
    }

    public static boolean notTrivialValues(ClaimCashflowPacket claim) {
        return (!(claim.ultimate() == 0 && claim.getPaidIncrementalIndexed() == 0 && claim.getPaidCumulatedIndexed() == 0
                && claim.getReportedIncrementalIndexed() == 0 && claim.getReportedCumulatedIndexed() == 0
                && claim.reservedIndexed() == 0));
    }

    public static void applyMarkers(ClaimCashflowPacket source, ClaimCashflowPacket target) {
        target.setMarker(source.peril());
        target.setMarker(source.segment());
        target.setMarker(source.reinsuranceContract());
        target.setMarker(source.legalEntity());
        target.setMarker(source.reserve());
    }
}
