package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.filterUtilities

import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket
import org.pillarone.riskanalytics.domain.pc.cf.claim.IClaimRoot
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.ContractCoverBase
import org.joda.time.DateTime
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.IncurredClaimBase
import org.pillarone.riskanalytics.domain.pc.cf.claim.ICededRoot
import org.pillarone.riskanalytics.core.simulation.SimulationException

/**
 *   author simon.parten @ art-allianz . com
 */
class GRIUtilities {

    public static List<ClaimCashflowPacket> cashflowsRelatedToRoots(Set<IClaimRoot> rootClaims, List<ClaimCashflowPacket> cashflows) {
        new ArrayList<ClaimCashflowPacket>(cashflows.findAll {it -> rootClaims.contains(it.baseClaim) })
    }


    public static Double ultimateSumFromCashflows(List<ClaimCashflowPacket> cashflows) {
       List<IClaimRoot> ultimates = RIUtilities.incurredClaims(cashflows, IncurredClaimBase.BASE)
        return ultimateSum(ultimates)

    }

    public static ICededRoot findCededClaimRelatedToGrossClaim(IClaimRoot grossClaim, List<ICededRoot> allCededClaims ) {
        ICededRoot claim = allCededClaims.find {grossClaim.equals(it.getGrossClaim())}
        if(claim == null) {
            throw new SimulationException("Failed to match a gross claim to the list of ceded claims")
        }
        return claim
    }

    public static ClaimCashflowPacket findCashflowToGrossClaim(IClaimRoot cededKeyClaim, List<ClaimCashflowPacket> allCashflows ) {
        ClaimCashflowPacket claim = allCashflows.find {cededKeyClaim.equals(it.getKeyClaim())}
        if(claim == null) {
            throw new SimulationException("Failed to match a gross claim to the list of ceded claims")
        }
        return claim
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
