package org.pillarone.riskanalytics.domain.pc.cf.discounting;

import org.joda.time.DateTime;
import org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter;
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter;
import org.pillarone.riskanalytics.core.parameterization.ConstrainedString;
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter;
import org.pillarone.riskanalytics.domain.pc.cf.indexing.*;

import java.util.ArrayList;
import java.util.List;

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
        List<IDiscountMarker> discounts = criteria.getValuesAsObjects();
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


}
