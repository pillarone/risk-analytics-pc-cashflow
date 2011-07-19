package org.pillarone.riskanalytics.domain.pc.cf.discounting;

import org.joda.time.DateTime;
import org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter;
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;
import org.pillarone.riskanalytics.domain.pc.cf.claim.IClaimRoot;
import org.pillarone.riskanalytics.domain.pc.cf.indexing.*;

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
     * Only applicable to criteria originating from Discountings
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
                                                                   double reservedGross, double reservedCeded, double reservedNet ){
        DiscountedValuesPacket discountedValues = new DiscountedValuesPacket();
        discountedValues.setDiscountedPaidIncrementalGross(paidGross);
        discountedValues.setDiscountedPaidIncrementalCeded(paidCeded);
        discountedValues.setDiscountedPaidIncrementalNet(paidNet);
        discountedValues.setDiscountedReservedGross(reservedGross);
        discountedValues.setDiscountedReservedCeded(reservedCeded);
        discountedValues.setDiscountedReservedNet(reservedNet);
        return discountedValues;
    }


}
