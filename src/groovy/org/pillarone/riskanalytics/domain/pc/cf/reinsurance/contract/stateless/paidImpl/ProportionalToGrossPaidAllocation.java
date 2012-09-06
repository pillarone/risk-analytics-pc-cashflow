package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.paidImpl;

import com.google.common.collect.ArrayListMultimap;
import org.pillarone.riskanalytics.core.simulation.SimulationException;
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ICededRoot;
import org.pillarone.riskanalytics.domain.pc.cf.claim.IClaimRoot;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.ContractCoverBase;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.IPaidAllocation;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.filterUtilities.GRIUtilities;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.filterUtilities.RIUtilities;

import java.util.*;

/**
 * author simon.parten @ art-allianz . com
 */
public class ProportionalToGrossPaidAllocation implements IPaidAllocation {

    public List<ClaimCashflowPacket> allocatePaid(Map<Integer, Double> incrementalPaidByPeriod, List<ClaimCashflowPacket> grossCashflowsThisPeriod,
                                                  List<ClaimCashflowPacket> cededCashflowsToDate,
                                                  PeriodScope periodScope,
                                                  ContractCoverBase coverageBase, List<ICededRoot> incurredCededClaims) {



        List<ClaimCashflowPacket> claimsOfInterest = new ArrayList<ClaimCashflowPacket>();
        List<ClaimCashflowPacket> latestCededCashflowsByIncurredClaim = RIUtilities.latestCashflowByIncurredClaim(cededCashflowsToDate);

//        For each model period
        for (Map.Entry<Integer, Double> entry : incrementalPaidByPeriod.entrySet()) {
            List<ClaimCashflowPacket> cashflowsRelatedToModelPeriod = RIUtilities.cashflowsClaimsByPeriod(entry.getKey(), periodScope.getPeriodCounter(), grossCashflowsThisPeriod, coverageBase);
            double grossIncurredInPeriod = GRIUtilities.ultimateSumFromCashflows(cashflowsRelatedToModelPeriod);
            double cededPaidAmountInModelPeriodThisSimPeriod = (Double) entry.getValue();

            ArrayListMultimap<IClaimRoot, ClaimCashflowPacket> cashflowsByKey = RIUtilities.cashflowsByRoot(cashflowsRelatedToModelPeriod);
            Map<IClaimRoot, Collection<ClaimCashflowPacket>> cashflows = cashflowsByKey.asMap();

            for (Map.Entry<IClaimRoot, Collection<ClaimCashflowPacket>> packetEntrys : cashflows.entrySet()) {
                List<ClaimCashflowPacket> cashflowPackets = new ArrayList<ClaimCashflowPacket>(packetEntrys.getValue()) ;
                double grossIncurredByClaimRatio = packetEntrys.getKey().getUltimate() / grossIncurredInPeriod;
                double claimPaidInContractYear = grossIncurredByClaimRatio * cededPaidAmountInModelPeriodThisSimPeriod;

                double sumIncrementsOfThisClaim = GRIUtilities.incrementalCashflowSum(cashflowPackets);

                IClaimRoot keyClaim = cashflowPackets.get(0).getKeyClaim();
                ICededRoot cededRoot = GRIUtilities.findCededClaimRelatedToGrossClaim(packetEntrys.getKey(), incurredCededClaims);
                ClaimCashflowPacket latestCededCashflow = GRIUtilities.findCashflowToGrossClaim(keyClaim, latestCededCashflowsByIncurredClaim);
                boolean setUltimate = false;
                if(latestCededCashflow.getBaseClaim().getExposureStartDate() == null && latestCededCashflow.ultimate() == 0 ) {
//                    We know we have a dummy claim - this is the first time this incurred claim has ceded something.
                    setUltimate = true;
                }

                double cumulatedCededForThisClaim = latestCededCashflow.getPaidCumulatedIndexed();
                for (ClaimCashflowPacket cashflowPacket : cashflowPackets) {
                    double paidAgainstThisPacket = 0;

                    if (sumIncrementsOfThisClaim == 0 ||  (cashflowPackets.size() == 1) ) {
                        paidAgainstThisPacket = - claimPaidInContractYear;
                    } else {
                        paidAgainstThisPacket = claimPaidInContractYear * cashflowPacket.getPaidIncrementalIndexed() / -sumIncrementsOfThisClaim;
                    }

                    cumulatedCededForThisClaim += paidAgainstThisPacket;
                    ClaimCashflowPacket claimCashflowPacket = new ClaimCashflowPacket(cededRoot, cashflowPacket, paidAgainstThisPacket, cumulatedCededForThisClaim, setUltimate);
                    if (cumulatedCededForThisClaim < cededRoot.getUltimate()) {
                        throw new SimulationException("Insanity detected : " + cededRoot.toString() + " has an ultimate smaller " +
                                "than the paid amount " + claimCashflowPacket.toString() + "" +
                                "This will create inconsistencies in higher structures. Contact development"
                        );
                    }
                    setUltimate = false;
                    claimsOfInterest.add(claimCashflowPacket);
                }
            }
        }

        return claimsOfInterest;


    }
}
