package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.nonproportional;

import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.allocation.IRIPremiumSplitStrategy;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stabilization.IStabilizationStrategy;

import java.util.List;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class TermWCXLContract extends TermCXLContract {

    /**
     * All provided values have to be absolute! Scaling is done within the parameter strategy.
     *
     * @param cededPremiumFixed
     * @param attachmentPoint
     * @param limit
     * @param aggregateDeductible
     * @param aggregateLimit
     * @param stabilization
     * @param reinstatementPremiumFactors
     * @param premiumAllocation
     */
    public TermWCXLContract(double cededPremiumFixed, double attachmentPoint, double limit, double aggregateDeductible,
                            double aggregateLimit, IStabilizationStrategy stabilization,
                            List<Double> reinstatementPremiumFactors, IRIPremiumSplitStrategy premiumAllocation,
                            ThresholdStore termDeductible, EqualUsagePerPeriodThresholdStore termLimit) {
        super(cededPremiumFixed, attachmentPoint, limit, aggregateDeductible, aggregateLimit,
                stabilization, reinstatementPremiumFactors, premiumAllocation, termDeductible, termLimit);
    }

}
