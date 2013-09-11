package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.additionalPremium;

import org.joda.time.DateTime;
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public enum CalcAPBasis implements IGetAPDates {

    NONE {
        @Override
        public APBasis uiAPBasis() {
            return APBasis.NONE;
        }
    },
    AGGREGATED{
        @Override
        public APBasis uiAPBasis() {
            throw new IllegalStateException("Should never be called - not valid in UI");
        }
    },
    PREMIUM{
        @Override
        public APBasis uiAPBasis() {
            return APBasis.PREMIUM;
        }
    },
    LOSS{
        @Override
        public APBasis uiAPBasis() {
            return APBasis.LOSS;
        }
    },
    TERM{
        @Override
        public APBasis uiAPBasis() {
            throw new IllegalStateException("Should never be called - not valid in UI");
        }
    },
    WEATHER {
        @Override
        public APBasis uiAPBasis() {
            throw new IllegalStateException("Should never be called - not valid in UI");
        }
    },
    REINSTATEMENT_PREMIUM {
        @Override
        public APBasis uiAPBasis() {
            throw new IllegalStateException("Should never be called - not valid in UI");
        }
    },
    LOSS_AP {
        @Override
        public APBasis uiAPBasis() {
            throw new IllegalStateException("Should never be called - not valid in UI");
        }
    },
    NCB{
        @Override
        public APBasis uiAPBasis() {
            return APBasis.NCB;
        }
    };

    public abstract APBasis uiAPBasis();

    public DateTime getAPDate(IPeriodCounter periodCounter) {
        return periodCounter.getCurrentPeriodEnd().minusDays(1);
    }


    public static CalcAPBasis getStringValue(String value) {
        CalcAPBasis[] values = CalcAPBasis.values();
        for (CalcAPBasis basis : values) {
            if (value.equals(basis.toString())) {
                return basis;
            }
        }
        throw new IllegalArgumentException("Enum not found for " + value);
    }
}
