package org.pillarone.riskanalytics.domain.pc.cf.discounting;

import org.joda.time.DateTime;
import org.pillarone.riskanalytics.core.components.PeriodStore;
import org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter;
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter;
import org.pillarone.riskanalytics.core.simulation.engine.IterationScope;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;
import org.pillarone.riskanalytics.domain.pc.cf.claim.IClaimRoot;
import org.pillarone.riskanalytics.domain.pc.cf.indexing.*;
import org.pillarone.riskanalytics.domain.pc.cf.segment.Segment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class DiscountUtils {

    /**
     * This method sets the BaseDateMode automatically to start of projection and IndexMode to continuous.
     * Only applicable to criteria originating from Discounting
     *
     * @param factorsPackets
     * @param criteria
     * @param fixedDate      is a dummy parameter
     * @return
     */
    public static List<Factors> filterFactors(List<FactorsPacket> factorsPackets,
                                              ComboBoxTableMultiDimensionalParameter criteria, DateTime fixedDate) {
        List<Factors> filteredFactors = new ArrayList<Factors>();
        if (criteria.isEmpty()) return null;
        List<IDiscountMarker> discounts = criteria.getValuesAsObjects(0, true);
        for (FactorsPacket factorPacket : factorsPackets) {
            int row = discounts.indexOf((IDiscountMarker) factorPacket.getOrigin());
            if (row > -1) {
                filteredFactors.add(new Factors(factorPacket, BaseDateMode.START_OF_PROJECTION,
                        IndexMode.CONTINUOUS, fixedDate));
            }
        }
        return filteredFactors;
    }

    public static double getDiscountFactor(List<Factors> factors, DateTime payoutDate, IPeriodCounter periodCounter) {
        Double productFactor = IndexUtils.aggregateFactor(factors, payoutDate, periodCounter, periodCounter.startOfFirstPeriod());
        return 1d / productFactor;
    }

    public static double getSumOfDiscountedIncrementalPaids(List<ClaimCashflowPacket> claims, List<Factors> factors, IPeriodCounter periodCounter) {
        double discountedIncrementalPaid = 0d;
        for (ClaimCashflowPacket claim : claims) {
            DateTime date = claim.getUpdateDate();
            double discountFactor = getDiscountFactor(factors, date, periodCounter);
            discountedIncrementalPaid += claim.getPaidIncrementalIndexed() * discountFactor;
        }
        return discountedIncrementalPaid;
    }

    public static double getDiscountedReservedAtEndOfPeriod(List<ClaimCashflowPacket> claims, List<Factors> factors, IPeriodCounter periodCounter) {
        Map<IClaimRoot, ClaimCashflowPacket> latestCashflowPerBaseClaim = new HashMap<IClaimRoot, ClaimCashflowPacket>();
        for (ClaimCashflowPacket claim : claims) {
            ClaimCashflowPacket latestCashflow = latestCashflowPerBaseClaim.get(claim.getBaseClaim());
            if (latestCashflow == null || claim.getUpdateDate().isAfter(latestCashflow.getUpdateDate())) {
                latestCashflow = claim;
                latestCashflowPerBaseClaim.put(claim.getBaseClaim(), latestCashflow);
            }
        }
        double discountedReserved = 0d;
        for (ClaimCashflowPacket claim : latestCashflowPerBaseClaim.values()) {
            DateTime date = claim.getUpdateDate();
            double discountFactor = getDiscountFactor(factors, date, periodCounter);
            discountedReserved += claim.reservedIndexed() * discountFactor;
        }
        return discountedReserved;
    }

    public static DiscountedValuesPacket getDiscountedValuesPacket(double paidGross, double paidCeded, double paidNet,
                                                                   double reservedGross, double reservedCeded, double reservedNet) {
        DiscountedValuesPacket discountedValues = new DiscountedValuesPacket();
        discountedValues.setDiscountedPaidIncrementalGross(paidGross);
        discountedValues.setDiscountedPaidIncrementalCeded(paidCeded);
        discountedValues.setDiscountedPaidIncrementalNet(paidNet);
        discountedValues.setDiscountedReservedGross(reservedGross);
        discountedValues.setDiscountedReservedCeded(reservedCeded);
        discountedValues.setDiscountedReservedNet(reservedNet);
        return discountedValues;
    }

    public static void getDiscountedGrossValues(List<FactorsPacket> factorsPackets, ComboBoxTableMultiDimensionalParameter criteria,
                                                List<ClaimCashflowPacket> claims, PeriodStore periodStore, IPeriodCounter periodCounter) {
        List<Factors> factors = filterFactors(factorsPackets, criteria, periodCounter.startOfFirstPeriod());
        periodStore.put(Segment.FILTERED_FACTORS, factors);
        double discountedIncrementalPaidGross = DiscountUtils.getSumOfDiscountedIncrementalPaids(claims, factors, periodCounter);
        double discountedReservedGross = DiscountUtils.getDiscountedReservedAtEndOfPeriod(claims, factors, periodCounter);
        double startNetPresentValueGross = periodStore.exists(Segment.NET_PRESENT_VALUE_GROSS) ? (Double) periodStore.get(Segment.NET_PRESENT_VALUE_GROSS, -1) : 0d;
        double endNetPresentValueGross = startNetPresentValueGross + discountedIncrementalPaidGross;
        periodStore.put(Segment.NET_PRESENT_VALUE_GROSS, endNetPresentValueGross);
        periodStore.put(Segment.DISCOUNTED_INCREMENTAL_PAID_GROSS, discountedIncrementalPaidGross);
        periodStore.put(Segment.DISCOUNTED_RESERVED_GROSS, discountedReservedGross);
    }

    public static void getDiscountedNetValuesAndFillOutChannels(List<ClaimCashflowPacket> claimsCeded,
                                                                List<ClaimCashflowPacket> claimsNet,
                                                                List<DiscountedValuesPacket> outDiscountedValues,
                                                                List<NetPresentValuesPacket> outNetPresentValues,
                                                                PeriodStore periodStore, IterationScope iterationScope) {
        IPeriodCounter periodCounter = iterationScope.getPeriodScope().getPeriodCounter();
        List<Factors> factors = (List<Factors>) periodStore.get(Segment.FILTERED_FACTORS, 0);
        double discountedIncrementalPaidGross = (Double) periodStore.get(Segment.DISCOUNTED_INCREMENTAL_PAID_GROSS, 0);
        double discountedIncrementalPaidCeded = DiscountUtils.getSumOfDiscountedIncrementalPaids(claimsCeded, factors, periodCounter);
        double discountedIncrementalPaidNet = DiscountUtils.getSumOfDiscountedIncrementalPaids(claimsNet, factors, periodCounter);
        double discountedReservedGross = (Double) periodStore.get(Segment.DISCOUNTED_RESERVED_GROSS, 0);
        double discountedReservedCeded = DiscountUtils.getDiscountedReservedAtEndOfPeriod(claimsCeded, factors, periodCounter);
        double discountedReservedNet = DiscountUtils.getDiscountedReservedAtEndOfPeriod(claimsNet, factors, periodCounter);

        double startNetPresentValueCeded = periodStore.exists(Segment.NET_PRESENT_VALUE_CEDED) ? (Double) periodStore.get(Segment.NET_PRESENT_VALUE_CEDED, -1) : 0d;
        double startNetPresentValueNet = periodStore.exists(Segment.NET_PRESENT_VALUE_NET) ? (Double) periodStore.get(Segment.NET_PRESENT_VALUE_NET, -1) : 0d;
        double endNetPresentValueCeded = startNetPresentValueCeded + discountedIncrementalPaidCeded;
        double endNetPresentValueNet = startNetPresentValueNet + discountedIncrementalPaidNet;
        periodStore.put(Segment.NET_PRESENT_VALUE_CEDED, endNetPresentValueCeded);
        periodStore.put(Segment.NET_PRESENT_VALUE_NET, endNetPresentValueNet);

        DiscountedValuesPacket discountedValues = DiscountUtils.getDiscountedValuesPacket(discountedIncrementalPaidGross,
                discountedIncrementalPaidCeded, discountedIncrementalPaidNet, discountedReservedGross,
                discountedReservedCeded, discountedReservedNet);
        outDiscountedValues.add(discountedValues);

        int period = iterationScope.getPeriodScope().getCurrentPeriod();
        if (period + 1 == iterationScope.getNumberOfPeriods()) {
            NetPresentValuesPacket netPresentValues = new NetPresentValuesPacket();
            netPresentValues.setNetPresentValueGross((Double) periodStore.get(Segment.NET_PRESENT_VALUE_GROSS, 0));
            netPresentValues.setNetPresentValueCeded(endNetPresentValueCeded);
            netPresentValues.setNetPresentValueNet(endNetPresentValueNet);
            netPresentValues.period = 0;
            outNetPresentValues.add(netPresentValues);
        }
    }

}
