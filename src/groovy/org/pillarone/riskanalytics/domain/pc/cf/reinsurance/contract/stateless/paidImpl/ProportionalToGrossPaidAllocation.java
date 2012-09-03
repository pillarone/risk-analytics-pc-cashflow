package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.paidImpl;

import com.google.common.collect.ArrayListMultimap;
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ICededRoot;
import org.pillarone.riskanalytics.domain.pc.cf.claim.IClaimRoot;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.ContractCoverBase;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.IPaidAllocation;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.PeriodLayerParameters;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.filterUtilities.GRIUtilities;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.filterUtilities.RIUtilities;

import java.util.*;

/**
 * author simon.parten @ art-allianz . com
 */
public class ProportionalToGrossPaidAllocation implements IPaidAllocation {

    public List<ClaimCashflowPacket> allocatePaid(Map<Integer, Double> incrementalPaidByPeriod, List<ClaimCashflowPacket> grossCashflowsThisPeriod,
                                                  List<ClaimCashflowPacket> cededCashflowsToDate,
                                                  PeriodScope periodScope, double termExcess, double termLimit,
                                                  PeriodLayerParameters layerParameters, ContractCoverBase coverageBase, List<ICededRoot> incurredCededClaims) {



        List<ClaimCashflowPacket> claimsOfInterest = new ArrayList<ClaimCashflowPacket>();
        List<ClaimCashflowPacket> latestCashflowsByIncurredClaim = RIUtilities.latestCashflowByIncurredClaim(cededCashflowsToDate);

//        In each period
        for (Map.Entry<Integer, Double> entry : incrementalPaidByPeriod.entrySet()) {
            List<ClaimCashflowPacket> cashflowsRelatedToModelPeriod = RIUtilities.cashflowsClaimsByPeriod(entry.getKey(), periodScope.getPeriodCounter(), grossCashflowsThisPeriod, coverageBase);
            double grossIncurredInPeriod = GRIUtilities.ultimateSumFromCashflows(cashflowsRelatedToModelPeriod);
            double cededPaidAmountInModelPeriodThisSimPeriod = entry.getValue();

//            Sort the claims into lists of their respective root claims.
            ArrayListMultimap<IClaimRoot, ClaimCashflowPacket> cashflowsByKey = RIUtilities.cashflowsByRoot(cashflowsRelatedToModelPeriod);
            Collection<Map.Entry<IClaimRoot,ClaimCashflowPacket>> cashflows = cashflowsByKey.entries();

//            For each root claim allocate the appropriate paid amount.
            for (Map.Entry<IClaimRoot, ClaimCashflowPacket> packetEntrys : cashflowsByKey.entries()) {
                List<ClaimCashflowPacket> cashflowPackets = cashflowsByKey.get(packetEntrys.getKey());
                double grossIncurredByClaimRatio = packetEntrys.getKey().getUltimate() / grossIncurredInPeriod;
                double claimPaidInContractYear = grossIncurredByClaimRatio * cededPaidAmountInModelPeriodThisSimPeriod;

                double sumIncrementsOfThisClaim = GRIUtilities.incrementalCashflowSum(cashflowPackets);

                IClaimRoot keyClaim = cashflowPackets.get(0).getKeyClaim();

                ICededRoot cededRoot = GRIUtilities.findCededClaimRelatedToGrossClaim(packetEntrys.getKey(), incurredCededClaims);

                ClaimCashflowPacket latestCededCashflow = GRIUtilities.findCashflowToGrossClaim(keyClaim, latestCashflowsByIncurredClaim);

                double cumulatedCededForThisClaim = latestCededCashflow.getPaidCumulatedIndexed();
                for (ClaimCashflowPacket cashflowPacket : cashflowPackets) {
                    double paidAgainstThisPacket = cashflowPacket.getPaidIncrementalIndexed() * claimPaidInContractYear / sumIncrementsOfThisClaim;
                    cumulatedCededForThisClaim += paidAgainstThisPacket;
                    ClaimCashflowPacket claimCashflowPacket = new ClaimCashflowPacket(cededRoot, cashflowPacket, paidAgainstThisPacket,  cumulatedCededForThisClaim );
                    claimsOfInterest.add(claimCashflowPacket);
                }
            }
        }

        return claimsOfInterest;


    }
}
