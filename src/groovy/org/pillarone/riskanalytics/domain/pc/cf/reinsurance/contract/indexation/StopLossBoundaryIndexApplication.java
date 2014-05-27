package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.indexation;

import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.nonproportional.StopLossContract;

/**
 * @author stefan (dot) kunz (at) intuitive-collaboration (dot) com
 */
public enum StopLossBoundaryIndexApplication {
    NONE {
        @Override
        public void applicableIndex(StopLossContract contract, double index) {
        }
    }, LIMIT {
        @Override
        public void applicableIndex(StopLossContract contract, double index) {
            contract.multiplyLimitBy(index);
        }
    }, ATTACHMENT_POINT {
        @Override
        public void applicableIndex(StopLossContract contract, double index) {
            contract.multiplyAttachmentPoint(index);
        }
    }, ATTACHMENT_POINT_LIMIT{
        @Override
        public void applicableIndex(StopLossContract contract, double index) {
            contract.multiplyAttachmentPoint(index);
            contract.multiplyLimitBy(index);
        }
    }, ATTACHMENT_POINT_LIMIT_PREMIUM {
        @Override
        public void applicableIndex(StopLossContract contract, double index) {
            contract.multiplyAttachmentPoint(index);
            contract.multiplyLimitBy(index);
            contract.multiplyCededPremiumBy(index);
        }
    };

    abstract public void applicableIndex(StopLossContract contract, double index);
}
