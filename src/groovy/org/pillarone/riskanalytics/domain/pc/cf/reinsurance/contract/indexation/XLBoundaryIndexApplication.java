package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.indexation;

import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.nonproportional.XLContract;

/**
 * @author stefan (dot) kunz (at) intuitive-collaboration (dot) com
 */
public enum XLBoundaryIndexApplication {
    NONE {
        @Override
        public void applicableIndex(XLContract contract, double index) {
        }
    }, LIMIT_AGGREGATE_LIMIT {
        @Override
        public void applicableIndex(XLContract contract, double index) {
            contract.multiplyLimitBy(index);
            contract.multiplyAggregateLimitBy(index);
        }
    }, ATTACHMENT_POINT {
        @Override
        public void applicableIndex(XLContract contract, double index) {
            contract.multiplyAttachmentPoint(index);
        }
    }, ATTACHMENT_POINT_LIMIT_AGGREGATE_LIMIT {
        @Override
        public void applicableIndex(XLContract contract, double index) {
            contract.multiplyAttachmentPoint(index);
            contract.multiplyLimitBy(index);
            contract.multiplyAggregateLimitBy(index);
        }
    }, ATTACHMENT_POINT_LIMIT_AGGREGATE_LIMIT_AGGREGATE_DEDUCTIBLE {
        @Override
        public void applicableIndex(XLContract contract, double index) {
            contract.multiplyAttachmentPoint(index);
            contract.multiplyLimitBy(index);
            contract.multiplyAggregateLimitBy(index);
            contract.multiplyAggregateDeductibleBy(index);
        }
    };

    abstract public void applicableIndex(XLContract contract, double index);
}
