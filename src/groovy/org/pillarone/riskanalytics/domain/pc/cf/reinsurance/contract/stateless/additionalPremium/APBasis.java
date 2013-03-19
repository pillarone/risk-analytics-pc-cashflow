package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.additionalPremium;

import org.joda.time.DateTime;
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public enum APBasis{

    NONE {
        @Override
        public CalcAPBasis calcAPBasis() {
            return CalcAPBasis.NONE;
        }
    },
    PREMIUM{
        @Override
        public CalcAPBasis calcAPBasis() {
            return CalcAPBasis.PREMIUM;
        }
    },
    LOSS{
        @Override
        public CalcAPBasis calcAPBasis() {
            return CalcAPBasis.LOSS;
        }
    },
    NCB{
        @Override
        public CalcAPBasis calcAPBasis() {
            return CalcAPBasis.NCB;
        }
    };

    public abstract CalcAPBasis calcAPBasis();


    public static APBasis getStringValue(String value) {
        APBasis[] values = APBasis.values();
        for (APBasis basis : values) {
            if (value.equals(basis.toString())) {
                return basis;
            }
        }
        throw new IllegalArgumentException("Enum not found for " + value);
    }
}
