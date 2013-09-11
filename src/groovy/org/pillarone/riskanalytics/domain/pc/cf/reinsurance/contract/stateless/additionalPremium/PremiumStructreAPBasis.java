package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.additionalPremium;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public enum PremiumStructreAPBasis {

    PREMIUM{
    },
    LOSS{
    };


    public static PremiumStructreAPBasis getStringValue(String value) {
        PremiumStructreAPBasis[] values = PremiumStructreAPBasis.values();
        for (PremiumStructreAPBasis basis : values) {
            if (value.equals(basis.toString())) {
                return basis;
            }
        }
        throw new IllegalArgumentException("Enum not found for " + value);
    }
}
