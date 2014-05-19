package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.indexation;

import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.nonproportional.XLContract;

/**
 * @author stefan (dot) kunz (at) intuitive-collaboration (dot) com
 */
public enum SurplusBoundaryIndexApplication {
    NONE {
        @Override
        public void applicableIndex(XLContract contract, double index) {
        }
    }, RETENTION {
        @Override
        public void applicableIndex(XLContract contract, double index) {
            contract.multiplyLimitBy(index);
            contract.multiplyAggregateLimitBy(index);
        }
    };

    abstract public void applicableIndex(XLContract contract, double index);
}
