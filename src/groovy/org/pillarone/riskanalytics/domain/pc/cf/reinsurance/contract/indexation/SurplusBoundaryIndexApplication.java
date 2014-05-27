package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.indexation;

import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.SurplusContract;

/**
 * @author stefan (dot) kunz (at) intuitive-collaboration (dot) com
 */
public enum SurplusBoundaryIndexApplication {
    NONE {
        @Override
        public void applicableIndex(SurplusContract contract, double index) {
        }
    }, RETENTION {
        @Override
        public void applicableIndex(SurplusContract contract, double index) {
            contract.multiplyRetentionBy(index);
        }
    };

    abstract public void applicableIndex(SurplusContract contract, double index);
}
