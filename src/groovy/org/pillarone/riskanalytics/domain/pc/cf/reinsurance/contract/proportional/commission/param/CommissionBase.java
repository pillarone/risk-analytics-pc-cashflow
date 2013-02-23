package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.commission.param;

import org.pillarone.riskanalytics.domain.pc.cf.claim.BasedOnClaimProperty;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public enum CommissionBase {
    INITIAL {
        @Override
        BasedOnClaimProperty convert() {
            return BasedOnClaimProperty.ULTIMATE_UNINDEXED;
        }
    },
    REPORTED {
        @Override
        BasedOnClaimProperty convert() {
            return BasedOnClaimProperty.REPORTED;
        }
    },
    PAID {
        @Override
        BasedOnClaimProperty convert() {
            return BasedOnClaimProperty.PAID;
        }
    };

    abstract BasedOnClaimProperty convert();
}
