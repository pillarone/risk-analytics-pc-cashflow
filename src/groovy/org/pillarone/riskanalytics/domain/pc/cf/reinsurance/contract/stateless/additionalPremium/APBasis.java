package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.additionalPremium;

import org.joda.time.DateTime;
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public enum APBasis implements IGetAPDates {

    AGGREGATED {
        public DateTime getAPDate(IPeriodCounter periodCounter) {
            return periodCounter.endOfPeriod(periodCounter.currentPeriodIndex());
        }
    },
    NONE {
        public DateTime getAPDate(IPeriodCounter periodCounter) {
            return periodCounter.endOfPeriod(periodCounter.currentPeriodIndex());
        }
    },
    PREMIUM {
        public DateTime getAPDate(IPeriodCounter periodCounter) {
            return periodCounter.endOfPeriod(periodCounter.currentPeriodIndex());
        }
    },
    LOSS {
        public DateTime getAPDate(IPeriodCounter periodCounter) {
            return periodCounter.endOfPeriod(periodCounter.currentPeriodIndex());
        }
    },
    NCB {
        public DateTime getAPDate(IPeriodCounter periodCounter) {
            return periodCounter.endOfPeriod(periodCounter.currentPeriodIndex());
        }

    };


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
