package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.filterUtilities

import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket
import org.pillarone.riskanalytics.domain.pc.cf.claim.IClaimRoot
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.ContractCoverBase
import org.joda.time.DateTime
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.IncurredClaimBase
import org.pillarone.riskanalytics.domain.pc.cf.claim.ICededRoot
import org.pillarone.riskanalytics.core.simulation.SimulationException
import org.pillarone.riskanalytics.domain.utils.datetime.DateTimeUtilities
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter

/**
 *   author simon.parten @ art-allianz . com
 */
@Deprecated /* Too slow. Use Java */
class GRIUtilities {

    public static List<ClaimCashflowPacket> cashflowsRelatedToRoots(Set<IClaimRoot> rootClaims, List<ClaimCashflowPacket> cashflows) {
        new ArrayList<ClaimCashflowPacket>(cashflows.findAll {it -> rootClaims.contains(it.baseClaim) })
    }

    public static boolean hasGrossCashflow(List<ClaimCashflowPacket> cashflowPacketList, ICededRoot root) {
        return cashflowPacketList*.getBaseClaim().contains(root)
    }

    public static Double ultimateSumFromMap(Map<IClaimRoot, Collection<ClaimCashflowPacket>> aMap) {
        return ultimateSumFromCashflows(aMap.values().flatten())
    }

    @Deprecated /* too slow */
    public static Double ultimateSumFromCashflows(Collection<ClaimCashflowPacket> cashflows) {
        Set<IClaimRoot> incurredClaims = RIUtilities.incurredClaims(cashflows, IncurredClaimBase.BASE)
        ArrayList<IClaimRoot> claims = new ArrayList<IClaimRoot>()
        claims.addAll(incurredClaims)
        return ultimateSum(claims)

    }

    public static ICededRoot findCededClaimRelatedToGrossClaim(IClaimRoot grossClaim, List<ICededRoot> allCededClaims) {
        ICededRoot claim = allCededClaims.find {grossClaim.equals(it.getGrossClaim())}
        if (claim == null) {
            throw new SimulationException("Failed to match a gross claim to the list of ceded claims; " + claim.toString())
        }
        return claim
    }

    public static ClaimCashflowPacket findCashflowToGrossClaim(IClaimRoot cededKeyClaim, Collection<ClaimCashflowPacket> allCashflows, IncurredClaimBase base) {
        ClaimCashflowPacket claim = allCashflows.find { cededKeyClaim.equals( base.parentClaim(it) )}
//        There ought to be no prior ceded claims. Pump out a dummy claim.
        if (claim == null) {
            claim = new ClaimCashflowPacket()
        }
        return claim
    }

    @Deprecated /* tool slow ! */
    public static Double ultimateSum(Collection<IClaimRoot> incurredClaims) {
        if (incurredClaims.size() > 0) {
            return (Double) incurredClaims*.getUltimate().sum()
        }
        return 0d
    }

    @Deprecated /* Too slow */
    public static Double incrementalCashflowSum(Collection<ClaimCashflowPacket> incurredClaims) {
        if (incurredClaims.size() > 0) {
            return (Double) incurredClaims*.getPaidIncrementalIndexed().sum()
        }
        return 0d
    }

    /**
     * This method is extremely slow. Convert to java...
      * @param coverageBase
     * @param coverStart
     * @param coverEnd
     * @param incomingClaims
     * @return
     */
    public static List<ClaimCashflowPacket> uncoveredClaims(ContractCoverBase coverageBase, DateTime coverStart, DateTime coverEnd, List<ClaimCashflowPacket> incomingClaims) {

        final List<ClaimCashflowPacket> uncoveredClaims = incomingClaims.findAll {ClaimCashflowPacket it ->
            DateTime claimCoverDate = coverageBase.claimCoverDate(it);
            Boolean inCoverPeriod = (coverStart.isEqual(claimCoverDate) || coverStart.isBefore(claimCoverDate)) && coverEnd.isAfter(claimCoverDate)
            return !inCoverPeriod
        }
        return uncoveredClaims;
    }

    public static Collection<IClaimRoot> claimsCoveredInPeriod(Collection<IClaimRoot> incurredClaims, PeriodScope periodScope, ContractCoverBase base, int period) {

        Collection<IClaimRoot> claims = incurredClaims.findAll {
            it ->
            DateTime coverDate = base.claimCoverDate(it)
            return periodScope.getPeriodCounter().belongsToPeriod(coverDate) == period
        }

        return claims
    }

    public static Collection<ClaimCashflowPacket> cashflowsCoveredInModelPeriod(Collection<ClaimCashflowPacket> cashflows, PeriodScope periodScope, ContractCoverBase base, int period) {
        IPeriodCounter counter = periodScope.getPeriodCounter()
        return cashflows.findAll {
            it ->
            DateTime coverDate = base.claimCoverDate(it)
            return counter.belongsToPeriod(coverDate) == period
        }
    }

    public static Collection<DateTime> filterDates(DateTime allDatesAfterOrEqual, DateTime endDate, Collection<DateTime> dates) {
        dates.findAll {it -> DateTimeUtilities.isBetween(allDatesAfterOrEqual, endDate, it )  }
    }
}
