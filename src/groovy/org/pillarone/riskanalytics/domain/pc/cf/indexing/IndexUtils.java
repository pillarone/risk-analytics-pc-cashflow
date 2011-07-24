package org.pillarone.riskanalytics.domain.pc.cf.indexing;

import org.joda.time.DateTime;
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter;
import org.pillarone.riskanalytics.core.parameterization.ConstrainedString;
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class IndexUtils {

    public static FactorsPacket filterFactors(List<FactorsPacket> factors, ConstrainedString criteria) {
        for (FactorsPacket factor : factors) {
            if (factor.getOrigin().equals(criteria.getSelectedComponent())) {
                return factor;
            }
        }
        return null;
    }

    public static List<Factors> filterFactors(List<FactorsPacket> factorsPackets, ConstrainedMultiDimensionalParameter criteria) {
        List<Factors> filteredFactors = new ArrayList<Factors>();
        if (criteria.isEmpty()) return null;
        List<IIndexMarker> indices = criteria.getValuesAsObjects(criteria.getColumnIndex(IndexSelectionTableConstraints.INDEX));
        List<String> indexModes = criteria.getColumnByName(IndexSelectionTableConstraints.MODE);
        List<String> baseDateModes = criteria.getColumnByName(IndexSelectionTableConstraints.BASEDATEMODE);
        List<DateTime> fixedDates = criteria.getColumnByName(IndexSelectionTableConstraints.DATE);
        for (FactorsPacket factorPacket : factorsPackets) {
            int row = indices.indexOf((IIndexMarker) factorPacket.getOrigin());
            if (row > -1) {
                filteredFactors.add(new Factors(factorPacket, BaseDateMode.valueOf(baseDateModes.get(row)),
                        IndexMode.valueOf(indexModes.get(row)), fixedDates.get(row)));
            }
        }
        return filteredFactors;
    }

    /**
     * This method sets the BaseDateMode automatically to fixed date.
     * @param factorsPackets
     * @param criteria
     * @param fixedDate
     * @return
     */
    public static List<Factors> filterFactors(List<FactorsPacket> factorsPackets,
                                              ConstrainedMultiDimensionalParameter criteria, DateTime fixedDate) {
        List<Factors> filteredFactors = new ArrayList<Factors>();
        if (criteria.isEmpty()) return null;
        List<IIndexMarker> indices = criteria.getValuesAsObjects(criteria.getColumnIndex(IndexSelectionTableConstraints.INDEX));
        List<String> indexModes = criteria.getColumnByName(IndexSelectionTableConstraints.MODE);
        for (FactorsPacket factorPacket : factorsPackets) {
            int row = indices.indexOf((IIndexMarker) factorPacket.getOrigin());
            if (row > -1) {
                filteredFactors.add(new Factors(factorPacket, BaseDateMode.FIXED_DATE,
                        IndexMode.valueOf(indexModes.get(row)), fixedDate));
            }
        }
        return filteredFactors;
    }

    public static List<Factors> convertFactors(List<FactorsPacket> factorsPackets, BaseDateMode baseDateMode,
                                               IndexMode indexMode, DateTime date) {
        List<Factors> factors = new ArrayList<Factors>(factorsPackets.size());
        for (FactorsPacket factorsPacket : factorsPackets) {
            if (date == null) {
                factors.add(new Factors(factorsPacket, baseDateMode, indexMode, factorsPacket.getDate()));
            }
            else {
                factors.add(new Factors(factorsPacket, baseDateMode, indexMode, date));
            }
        }
        return factors;
    }

    /**
     * @param factors
     * @param evaluationDate
     * @return aggregated indices for a specific evaluationDate
     */
    public static IndexPacket aggregate(List<Factors> factors, DateTime evaluationDate) {
        IndexPacket packet = new IndexPacket();
        if (factors != null) {
            for (Factors factor : factors) {
                packet.multiply(factor.getIndices(evaluationDate));
            }
        }
        return packet;
    }

    /**
     * @param factors
     * @param periodCounter
     * @param dateOfLoss
     * @return aggregate IndexPackets by evaluating each index series according its baseDate property
     */
    public static IndexPacket aggregate(List<Factors> factors, IPeriodCounter periodCounter, DateTime dateOfLoss) {
        IndexPacket packet = new IndexPacket();
        if (factors != null) {
            for (Factors factor : factors) {
                packet.multiply(factor.getIndices(periodCounter, dateOfLoss));
            }
        }
        return packet;
    }

    /**
     * @param factors
     * @param updateDate
     * @param periodCounter
     * @param dateOfLoss
     * @return aggregate factors by evaluating each index series according its baseDate property
     */
    public static Double aggregateFactor(List<Factors> factors, DateTime updateDate, IPeriodCounter periodCounter, DateTime dateOfLoss) {
        if (factors == null) return 1d;
        Double productFactor = 1d;
        for (Factors factor : factors) {
            productFactor *= factor.getFactor(periodCounter, dateOfLoss, updateDate);
        }
        return productFactor;
    }

    /**
     *
     * @param factors
     * @param payoutDate
     * @return aggregated factors for a specific payoutDate
     */
    public static Double aggregateFactor(List<Factors> factors, DateTime payoutDate) {
        if (factors == null) return 1d;
        Double productFactor = 1d;
        for (Factors factor : factors) {
            productFactor *= factor.getFactor(payoutDate);
        }
        return productFactor;
    }
}
