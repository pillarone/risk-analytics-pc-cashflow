package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.paidImpl;

import com.google.common.collect.ArrayListMultimap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pillarone.riskanalytics.core.simulation.SimulationException;
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ICededRoot;
import org.pillarone.riskanalytics.domain.pc.cf.claim.IClaimRoot;
import org.pillarone.riskanalytics.domain.pc.cf.global.SimulationConstants;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.ContractCoverBase;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.IPaidAllocation;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.IncurredClaimBase;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.filterUtilities.GRIUtilities;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.filterUtilities.RIUtilities;

import java.text.DecimalFormat;
import java.util.*;

/**
 * author simon.parten @ art-allianz . com
 */
public class ProportionalToCededPaidAllocation implements IPaidAllocation {

    private static Log LOG = LogFactory.getLog(ProportionalToCededPaidAllocation.class);

    public List<ClaimCashflowPacket> allocatePaid(Map<Integer, Double> incrementalPaidByPeriod, List<ClaimCashflowPacket> grossCashflowsThisPeriod,
                                                  List<ClaimCashflowPacket> cededCashflowsToDate,
                                                  PeriodScope periodScope,
                                                  ContractCoverBase coverageBase, List<ICededRoot> incurredCededClaims, boolean sanityChecks) {


        IncurredClaimBase base = IncurredClaimBase.BASE;

        List<ClaimCashflowPacket> claimsOfInterest = new ArrayList<ClaimCashflowPacket>();
        Collection<ClaimCashflowPacket> latestCededCashflowsByIncurredClaim = RIUtilities.latestCashflowByIncurredClaim(cededCashflowsToDate, base);

//        For each model period
        for (Map.Entry<Integer, Double> entry : incrementalPaidByPeriod.entrySet()) {
//            Find all the cashflows related to the model period.
            List<ClaimCashflowPacket> cashflowsRelatedToModelPeriod = RIUtilities.cashflowsClaimsByPeriod(entry.getKey(), periodScope.getPeriodCounter(), grossCashflowsThisPeriod, coverageBase);
            double cededPaidAmountInModelPeriodThisSimPeriod = (Double) entry.getValue();
            double cededIncurredToAllocateAdjustment = 0d;

            ArrayListMultimap<IClaimRoot, ClaimCashflowPacket> cashflowsByKey = RIUtilities.cashflowsByRoot(cashflowsRelatedToModelPeriod, base);
            Map<IClaimRoot, Collection<ClaimCashflowPacket>> cashflows = cashflowsByKey.asMap();
            Map<IClaimRoot, Collection<ClaimCashflowPacket>> cashflowsWithNonZeroPaidIncrements = new HashMap<IClaimRoot, Collection<ClaimCashflowPacket>>();
            Collection<IClaimRoot> cededClaimsWithNonZeroIncrements = new ArrayList<IClaimRoot>();

            /*
            We have a methodological problem if a claim which has a gross incurred value has zero incremental paid in a year. The proportional to incurred
            allocation will attempt to allocate a paid amount to a claim which pays nothing in the period. Therefore exclue claims with zero incremental paid
            from the allocation in this period. Ensure that claims have an ultimate amount transmitted.
             */
            for (Map.Entry<IClaimRoot, Collection<ClaimCashflowPacket>> iClaimRootCollectionEntry : cashflows.entrySet()) {
                if (GRIUtilities.incrementalCashflowSum(iClaimRootCollectionEntry.getValue()) > 0) {
                    cashflowsWithNonZeroPaidIncrements.put(iClaimRootCollectionEntry.getKey(), iClaimRootCollectionEntry.getValue());
                    ICededRoot cededRoot = RIUtilities.findCededClaimRelatedToGrossClaim(iClaimRootCollectionEntry.getKey(), incurredCededClaims);
                    cededClaimsWithNonZeroIncrements.add(cededRoot);
                } else {
                    ICededRoot cededRoot = RIUtilities.findCededClaimRelatedToGrossClaim(iClaimRootCollectionEntry.getKey(), incurredCededClaims);
                    IClaimRoot keyClaim = base.parentClaim(new ArrayList<ClaimCashflowPacket>(iClaimRootCollectionEntry.getValue()).get(0));
                    ClaimCashflowPacket aClaim = new ArrayList<ClaimCashflowPacket>(iClaimRootCollectionEntry.getValue()).get(0);
                    ClaimCashflowPacket latestCededCashflow = RIUtilities.findCashflowToGrossClaim(keyClaim, latestCededCashflowsByIncurredClaim, IncurredClaimBase.KEY);
                    cededIncurredToAllocateAdjustment += keyClaim.getUltimate();
                    if (base.parentClaim(latestCededCashflow).getExposureStartDate() == null && latestCededCashflow.ultimate() == 0) {
//                    We know we have a dummy claim - this is the first time this incurred claim has ceded something.
                        ClaimCashflowPacket claimCashflowPacket = new ClaimCashflowPacket(cededRoot, aClaim, 0d, 0d, true);
                        claimsOfInterest.add(claimCashflowPacket);
                    }
                }
            }

            double grossIncurredFromClaimsWithNonZeroIncrementsInPeriod = GRIUtilities.ultimateSumFromMap(cashflowsWithNonZeroPaidIncrements);
            double cededIncurredFromClaimsWithNonZeroIncrements = GRIUtilities.ultimateSum(cededClaimsWithNonZeroIncrements);
            for (Map.Entry<IClaimRoot, Collection<ClaimCashflowPacket>> packetEntrys : cashflowsWithNonZeroPaidIncrements.entrySet()) {
                List<ClaimCashflowPacket> cashflowPackets = new ArrayList<ClaimCashflowPacket>(packetEntrys.getValue());
                double sumIncrementsOfThisClaim = GRIUtilities.incrementalCashflowSum(cashflowPackets);

                double grossIncurredByClaimRatio;
                double cededIncurredByClaimRatio;
                ICededRoot cededRoot = RIUtilities.findCededClaimRelatedToGrossClaim(packetEntrys.getKey(), incurredCededClaims);
                if (Math.abs(grossIncurredFromClaimsWithNonZeroIncrementsInPeriod) == 0 || cededIncurredFromClaimsWithNonZeroIncrements == 0d ) {
                    grossIncurredByClaimRatio = 0d;
                    cededIncurredByClaimRatio = 0d;
                } else {
                    grossIncurredByClaimRatio = packetEntrys.getKey().getUltimate() / grossIncurredFromClaimsWithNonZeroIncrementsInPeriod;
                    cededIncurredByClaimRatio = cededRoot.getUltimate() / cededIncurredFromClaimsWithNonZeroIncrements;
                }
                double claimPaidInContractYear = cededIncurredByClaimRatio * cededPaidAmountInModelPeriodThisSimPeriod;
                IClaimRoot keyClaim = base.parentClaim(cashflowPackets.get(0));
                ClaimCashflowPacket latestCededCashflow = RIUtilities.findCashflowToGrossClaim(keyClaim, latestCededCashflowsByIncurredClaim, IncurredClaimBase.KEY);

                boolean setUltimate = false;
                if (base.parentClaim(latestCededCashflow).getExposureStartDate() == null && latestCededCashflow.ultimate() == 0) {
//                    We know we have a dummy claim - this is the first time this incurred claim has ceded something.
                    setUltimate = true;
                }

                double cumulatedCededForThisClaim = latestCededCashflow.getPaidCumulatedIndexed();
                for (ClaimCashflowPacket cashflowPacket : cashflowPackets) {
                    double paidAgainstThisPacket = 0;
                    paidAgainstThisPacket = claimPaidInContractYear * cashflowPacket.getPaidIncrementalIndexed() / sumIncrementsOfThisClaim;

                    cumulatedCededForThisClaim += paidAgainstThisPacket;
                    ClaimCashflowPacket claimCashflowPacket = new ClaimCashflowPacket(cededRoot, cashflowPacket, paidAgainstThisPacket, cumulatedCededForThisClaim, setUltimate);
                    if (Math.abs(cumulatedCededForThisClaim) > Math.abs(cededRoot.getUltimate()) + SimulationConstants.EPSILON) {
                        DecimalFormat df = new DecimalFormat("#.##");
                        String message = "Insanity detected : " + df.format(cededRoot.getUltimate()) + " has an ultimate of smaller magnitude " +
                                "than the paid amount " + df.format(claimCashflowPacket.getPaidCumulatedIndexed()) + ". Claim related to contract year " + entry.getKey() +
                                "This will create inconsistencies in higher structures. Contact development";
                        LOG.error(message);
                        if (sanityChecks) {
                            throw new SimulationException(message);
                        }
                    }
                    setUltimate = false;
                    claimsOfInterest.add(claimCashflowPacket);
                }
            }
        }
        return claimsOfInterest;
    }
}
