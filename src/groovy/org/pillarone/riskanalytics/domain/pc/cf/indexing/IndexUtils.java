package org.pillarone.riskanalytics.domain.pc.cf.indexing;

import org.pillarone.riskanalytics.core.parameterization.ConstrainedString;

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
}
