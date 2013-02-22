package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.additionalPremium;

import org.joda.time.DateTime;
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public enum APBasis implements IGetAPDates {

    NONE {
    },
    AGGREGATED{
    },
    PREMIUM{
    },
    LOSS{
    },
    TERM{
    },
    NCB{
    };

    public DateTime getAPDate(IPeriodCounter periodCounter) {
        return periodCounter.getCurrentPeriodEnd().minusDays(1);
    }


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
