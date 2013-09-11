package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.additionalPremium;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public enum NCBPaidTyoe {

    EXPIRY{

    },
    ELAPSED_TIME{

    };

    public static NCBPaidTyoe getStringValue(String value) {
        NCBPaidTyoe[] values = NCBPaidTyoe.values();
        for (NCBPaidTyoe basis : values) {
            if (value.equals(basis.toString())) {
                return basis;
            }
        }
        throw new IllegalArgumentException("Enum not found for " + value);
    }
}
