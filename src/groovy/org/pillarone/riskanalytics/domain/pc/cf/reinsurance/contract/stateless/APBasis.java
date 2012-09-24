package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public enum APBasis {
    PREMIUM, LOSS, NCB;

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
