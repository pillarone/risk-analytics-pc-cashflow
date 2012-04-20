package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.nonproportional;

import org.pillarone.riskanalytics.domain.pc.cf.claim.BasedOnClaimProperty;
import org.pillarone.riskanalytics.domain.pc.cf.indexing.FactorsPacket;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.ClaimStorage;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.allocation.IRIPremiumSplitStrategy;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stabilization.IStabilizationStrategy;

import java.util.List;

/**
 * <ul>
 *     <li>Reinstatements are calculated on a paid base and cover is refilled permanently.</li>
 *     <li>order of application: (1) attachment point, limit, (2) aggregate deductible, (3) aggregate limit</li>
 *     <li>aggregate deductibles are re-init for every period as calculations are based on cumulated values</li>
 * </ul>
 *
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class TermXLContract extends XLContract implements INonPropReinsuranceContract {

    protected ThresholdStore termDeductible;
    protected EqualUsagePerPeriodThresholdStore termLimit;


    /**
     * All provided values have to be absolute! Scaling is done within the parameter strategy.
     * @param cededPremiumFixed
     * @param attachmentPoint
     * @param limit
     * @param aggregateDeductible
     * @param aggregateLimit
     * @param stabilization
     * @param reinstatementPremiumFactors
     * @param riPremiumSplit
     * @param termDeductible
     * @param termLimit
     */
    public TermXLContract(double cededPremiumFixed, double attachmentPoint, double limit, double aggregateDeductible,
                          double aggregateLimit, IStabilizationStrategy stabilization,
                          List<Double> reinstatementPremiumFactors, IRIPremiumSplitStrategy riPremiumSplit,
                          ThresholdStore termDeductible, EqualUsagePerPeriodThresholdStore termLimit) {
        super(cededPremiumFixed, attachmentPoint, limit, aggregateDeductible, aggregateLimit, stabilization,
                reinstatementPremiumFactors, riPremiumSplit);
        this.termDeductible = termDeductible;
        this.termLimit = termLimit;
    }

    @Override
    public void initPeriod(int period, List<FactorsPacket> inFactors) {
        super.initPeriod(period, inFactors);
        termLimit.initPeriod(period);
    }

    protected double cededValue(double claimPropertyCumulated, BasedOnClaimProperty claimPropertyBase, ClaimStorage storage,
                               double stabilizationFactor) {
        double aggregateLimitValue = periodLimit.get(claimPropertyBase, stabilizationFactor);
        if (aggregateLimitValue > 0) {
            int occurrencePeriod = storage.getReference().getOccurrencePeriod(null);
            double termLimitValue = termLimit.get(claimPropertyBase, stabilizationFactor, occurrencePeriod);
            if (termLimitValue > 0) {
                double ceded = Math.min(Math.max(-claimPropertyCumulated - attachmentPoint * stabilizationFactor, 0), limit * stabilizationFactor);

                double cededAfterTermDeductible = Math.max(0, ceded - termDeductible.get(claimPropertyBase, stabilizationFactor));
                double reduceTermDeductible = ceded - cededAfterTermDeductible;
                termDeductible.set(Math.max(0, termDeductible.get(claimPropertyBase) - reduceTermDeductible), claimPropertyBase);

                double cededAfterAAD = Math.max(0, ceded - periodDeductible.get(claimPropertyBase, stabilizationFactor));
                double reduceAAD = cededAfterTermDeductible - cededAfterAAD;
                periodDeductible.set(Math.max(0, periodDeductible.get(claimPropertyBase) - reduceAAD), claimPropertyBase);

                double incrementalCeded = Math.max(0, cededAfterAAD - storage.getCumulatedCeded(claimPropertyBase));
                double cededAfterAAL = aggregateLimitValue > incrementalCeded ? incrementalCeded : aggregateLimitValue;
                periodLimit.plus(-cededAfterAAL, claimPropertyBase);
                double cededAfterTermLimit = termLimitValue > cededAfterAAL ? cededAfterAAL : termLimitValue;
                termLimit.plus(-cededAfterTermLimit, claimPropertyBase, occurrencePeriod);
                return cededAfterTermLimit;
            }
            return 0;
        }
        else {
            return 0;
        }
    }


    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer(super.toString());
        buffer.append("term deductible: ");
        buffer.append(termDeductible);
        buffer.append(", term limit: ");
        buffer.append(termLimit);
        return buffer.toString();
    }

}
