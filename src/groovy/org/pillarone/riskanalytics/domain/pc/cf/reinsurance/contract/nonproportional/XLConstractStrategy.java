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
public abstract class XLConstractStrategy extends AbstractParameterObject implements IReinsuranceContractStrategy {

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
        params.put(STABILIZATION, stabilization);
        return params;
    }

    protected Double getCededPremiumFixed(List<UnderwritingInfoPacket> underwritingInfoPackets) {
        return UnderwritingInfoUtils.scalePremium(underwritingInfoPackets, premiumBase, premium, limit);
    }

    public double getTermDeductible() {
        return 0;
    }

    public double getTermLimit() {
        return 0;
    }

    public static final String PREMIUM_BASE = "premiumBase";
    public static final String PREMIUM = "premium";
    public static final String PREMIUM_ALLOCATION = "riPremiumSplit";
    public static final String REINSTATEMENT_PREMIUMS = "reinstatementPremiums";
    public static final String ATTACHMENT_POINT = "attachmentPoint";
    public static final String LIMIT = "limit";
    public static final String AGGREGATE_DEDUCTIBLE = "aggregateDeductible";
    public static final String AGGREGATE_LIMIT = "aggregateLimit";
    public static final String STABILIZATION = "stabilization";
}
