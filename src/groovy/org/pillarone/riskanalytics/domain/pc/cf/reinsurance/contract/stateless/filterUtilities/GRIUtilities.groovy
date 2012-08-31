package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.filterUtilities

import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket
import org.pillarone.riskanalytics.domain.pc.cf.claim.IClaimRoot
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.ContractCoverBase
import org.joda.time.DateTime
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope

/**
 *   author simon.parten @ art-allianz . com
 */
class GRIUtilities {

    public static List<ClaimCashflowPacket> cashflowsRelatedToRoots(Set<IClaimRoot> rootClaims, List<ClaimCashflowPacket> cashflows) {

        new ArrayList<ClaimCashflowPacket>(cashflows.findAll {it -> rootClaims.contains(it.baseClaim) })

    }

    public static Double ultimateSum(List<IClaimRoot> incurredClaims) {
        return (Double) incurredClaims*.getUltimate().sum()
    }

    public static Double incrementalCashflowSum(List<ClaimCashflowPacket> incurredClaims) {
        return (Double) incurredClaims*.getPaidIncrementalIndexed().sum()
    }

    public static List<ClaimCashflowPacket> uncoveredClaims(ContractCoverBase coverageBase, DateTime coverStart, DateTime coverEnd, List<ClaimCashflowPacket> incomingClaims) {

        final List<ClaimCashflowPacket> uncoveredClaims = incomingClaims.findAll {ClaimCashflowPacket it ->
            DateTime claimCoverDate = coverageBase.claimCoverDate(it);
            boolean inCoverPeriod = (coverStart.isEqual(claimCoverDate) || coverStart.isBefore(claimCoverDate)) && coverEnd.isAfter(claimCoverDate)
            return !inCoverPeriod
        }
        return uncoveredClaims;
    }

    public static ArrayList<IClaimRoot> claimsCoveredInPeriod(List<IClaimRoot> incurredClaims, PeriodScope periodScope, ContractCoverBase base, int period ) {

        ArrayList<IClaimRoot> claims  = incurredClaims.findAll {
            it ->
            DateTime coverDate = base.claimCoverDate(it)
            return periodScope.getPeriodCounter().belongsToPeriod(coverDate) == period
        }

        return claims
    }

    public static ArrayList<ClaimCashflowPacket> cashflowsCoveredInModelPeriod(List<ClaimCashflowPacket> cashflows, PeriodScope periodScope, ContractCoverBase base, int period ) {

        return cashflows.findAll {
            it ->
            DateTime coverDate = base.claimCoverDate(it)
            return periodScope.getPeriodCounter().belongsToPeriod(coverDate) == period
        }
    }


}
