package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.nonproportional;

import org.pillarone.riskanalytics.core.parameterization.AbstractMultiDimensionalParameter;
import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObject;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoUtils;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.IReinsuranceContractStrategy;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.allocation.IRIPremiumSplitStrategy;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stabilization.IStabilizationStrategy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public abstract class TermXLConstractStrategy extends AbstractParameterObject implements IReinsuranceContractStrategy {

    /** Premium can be expressed as a fraction of a base quantity. */
    protected XLPremiumBase premiumBase;

    /** Premium as a percentage of the premium base */
    protected double premium;

    /** Strategy to allocate the ceded premium to the different lines of business  */
    protected IRIPremiumSplitStrategy riPremiumSplit;
    /** As a percentage of premium */
    protected AbstractMultiDimensionalParameter reinstatementPremiums;
    protected double attachmentPoint;
    protected double limit;
    protected double aggregateDeductible;
    protected double aggregateLimit;
    protected double termDeductible = 0;
    protected double termLimit = 0;
    protected IStabilizationStrategy stabilization;

    public Map getParameters() {
        Map params = new HashMap();
        params.put(PREMIUM_BASE, premiumBase);
        params.put(PREMIUM, premium);
        params.put(PREMIUM_ALLOCATION, riPremiumSplit);
        params.put(REINSTATEMENT_PREMIUMS, reinstatementPremiums);
        params.put(ATTACHMENT_POINT, attachmentPoint);
        params.put(LIMIT, limit);
        params.put(AGGREGATE_DEDUCTIBLE, aggregateDeductible);
        params.put(AGGREGATE_LIMIT, aggregateLimit);
        params.put(TERM_DEDUCTIBLE, termDeductible);
        params.put(TERM_LIMIT, termLimit);
        params.put(STABILIZATION, stabilization);
        return params;
    }

    public double getTermDeductible() {
        return termDeductible;
    }

    public double getTermLimit() {
        return termLimit;
    }

    protected Double getCededPremiumFixed(List<UnderwritingInfoPacket> underwritingInfoPackets) {
        double cededPremiumFixed = 0;
        switch (premiumBase) {
            case ABSOLUTE:
                cededPremiumFixed = premium;
                break;
            case GNPI:
                cededPremiumFixed = premium * UnderwritingInfoUtils.sumPremiumWritten(underwritingInfoPackets);
                break;
            case RATE_ON_LINE:
                cededPremiumFixed = premium * limit;
                break;
            case NUMBER_OF_POLICIES:
                cededPremiumFixed = premium * UnderwritingInfoUtils.sumNumberOfPolicies(underwritingInfoPackets);
                break;
        }
        return cededPremiumFixed;
    }

    public static final String PREMIUM_BASE = "premiumBase";
    public static final String PREMIUM = "premium";
    public static final String PREMIUM_ALLOCATION = "riPremiumSplit";
    public static final String REINSTATEMENT_PREMIUMS = "reinstatementPremiums";
    public static final String ATTACHMENT_POINT = "attachmentPoint";
    public static final String LIMIT = "limit";
    public static final String AGGREGATE_DEDUCTIBLE = "aggregateDeductible";
    public static final String AGGREGATE_LIMIT = "aggregateLimit";
    public static final String TERM_DEDUCTIBLE = "termDeductible";
    public static final String TERM_LIMIT = "termLimit";
    public static final String STABILIZATION = "stabilization";
}
