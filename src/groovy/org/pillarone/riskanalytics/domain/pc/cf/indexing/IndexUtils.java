package org.pillarone.riskanalytics.domain.pc.cf.indexing;

import org.joda.time.DateTime;
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter;
import org.pillarone.riskanalytics.core.parameterization.ConstrainedString;

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
        for (FactorsPacket factorPacket : factorsPackets) {
            int row = indices.indexOf((IIndexMarker) factorPacket.getOrigin());
            if (row > -1) {
                filteredFactors.add(new Factors(factorPacket, BaseDateMode.valueOf(baseDateModes.get(row)),
                        IndexMode.valueOf(indexModes.get(row))));
            }
        }
        return filteredFactors;
    }

    public static IndexPacket aggregate(List<Factors> factors, DateTime evaluationDate) {
        IndexPacket packet = new IndexPacket();
        if (factors != null) {
            for (Factors factor : factors) {
                packet.multiply(factor.getIndices(evaluationDate));
            }
        }
        return packet;
    }



    public static Double aggregateFactor(List<Factors> factors, DateTime payoutDate) {
        if (factors == null) return 1d;
        Double productFactor = 1d;
        for (Factors factor : factors) {
            productFactor *= factor.getFactor(payoutDate);
        }
        return productFactor;
    }
}
